
package com.rac021.quarkus.certme.extention ;

/**
 *
 * @author ryahiaoui
 */

import java.io.File ;
import java.net.URI ;
import java.io.Writer ;
import java.util.List ;
import java.net.Socket ;
import java.util.Arrays ;
import java.util.HashSet ;
import java.io.FileReader ;
import java.io.FileWriter ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.nio.file.Paths ;
import java.net.InetAddress ;
import java.security.KeyPair ;
import javax.inject.Singleton ;
import java.security.Security ;
import org.shredzone.acme4j.Order ;
import java.net.InetSocketAddress ;
import org.shredzone.acme4j.Login ;
import org.shredzone.acme4j.Status ;
import org.shredzone.acme4j.Session ;
import org.shredzone.acme4j.Account ;
import java.util.concurrent.TimeUnit ;
import org.apache.logging.log4j.Level ;
import org.apache.logging.log4j.Logger ;
import org.apache.commons.io.FileUtils ;
import org.shredzone.acme4j.Certificate ;
import org.shredzone.acme4j.Authorization ;
import org.apache.logging.log4j.LogManager ;
import org.shredzone.acme4j.AccountBuilder ;
import org.shredzone.acme4j.util.CSRBuilder ;
import org.shredzone.acme4j.util.KeyPairUtils ;
import org.shredzone.acme4j.challenge.Challenge ;
import io.quarkus.runtime.annotations.ConfigRoot ;
import io.quarkus.runtime.annotations.ConfigPhase ;
import java.nio.file.attribute.PosixFilePermission ;
import org.shredzone.acme4j.exception.AcmeException ;
import org.shredzone.acme4j.challenge.Dns01Challenge ;
import org.shredzone.acme4j.challenge.Http01Challenge ;
import org.apache.logging.log4j.core.config.Configurator ;
import org.bouncycastle.jce.provider.BouncyCastleProvider ;

/**
 *
 * @author ryahiaoui
 */

@Singleton
@ConfigRoot( name = "certme" , phase = ConfigPhase.BUILD_TIME )
public class CertMeBuildTime {
    
    /** File name of the User Key Pair.      */
    private static  File USER_KEY_FILE        ;
    
    private static  File USER_KEY_FILE_COPY   ;

    /** File name of the Domain Key Pair.    */
    private static  File DOMAIN_KEY_FILE      ;

    /** File name of the CSR0.               */
    private static  File DOMAIN_CSR_FILE      ;

    /** File name of the signed certificate. */
    private static  File DOMAIN_CHAIN_FILE    ;

    /** RSA key size of generated key pairs. */
    private static final int KEY_SIZE = 4096  ;

    private static String   STAGING   = "DEV" ;

    private enum ChallengeType {  HTTP, DNS   }
       
    /** Challenge type to be used. */
    private static final ChallengeType CHALLENGE_TYPE = ChallengeType.HTTP   ;
  
    private static final Logger LOG = LogManager.getLogger(CertMeBuildTime.class.getName() ) ;
 
    private static boolean      forceGen  = false ;
    
    private static Exception    exception = null  ;
    
    private static VertxServer  server    = null  ;
            
    public CertMeBuildTime() throws Exception     {
       
       genCertificates()   ;
    }
    
    public static void genCertificates() throws Exception    {

       configLogger()                                        ;
       
       exception       = null                                ;
       
       String ignore   = System.getProperty("certme_ignore") ;
       
       if( ignore != null && ! ignore.trim().isEmpty()   )   {
           
           LOG.info( "\nCertMe Let's Encrypt Certificate "   +
                     "Generation IGNORED \n" )               ;
           return                                            ;
       }
       
       String domain                 = System.getProperty("certme_domain")      ;
       String outCertificateFolder   = System.getProperty("certme_out_folder")  ;
       String outCertificateFileName = System.getProperty("certme_file_name")   ;
       String Interface              = System.getProperty("certme_interface")   ;
       String port                   = System.getProperty("certme_port")        ;
       String staging                = System.getProperty("certme_staging")     ;
       String forceGenStr            = System.getProperty("certme_force_gen")   ;

       Integer portNum               = 80                                       ;
       
       if( port != null && ! port.trim().isEmpty() ) portNum = Integer.parseInt( port ) ;

       if( domain == null || domain.trim().isEmpty() ) domain = getDomain()             ;
       
       if( Interface == null || Interface.trim().isEmpty()) Interface = "0.0.0.0"       ;
       
       if(   staging != null          && 
           ! staging.trim().isEmpty() && 
             staging.trim().equalsIgnoreCase( "PROD")) { STAGING = "PROD" ; }
       
       if ( forceGenStr != null ) forceGen = true           ;
       
       String jVersion = System.getProperty("java.version") ;
 
       String dir      = System.getProperty("user.dir")     ;

       if( outCertificateFileName == null || outCertificateFileName.trim().isEmpty() ) 
           outCertificateFileName = "app"  ;
      
       if (outCertificateFolder == null || outCertificateFolder.trim().isEmpty()   )
           outCertificateFolder = dir + File.separator + "certMe" + File.separator ;
        
       if ( ! outCertificateFolder.trim().endsWith( File.separator ) ) {
              outCertificateFolder += File.separator ;
       }
       
       FileUtils.deleteQuietly( new File(outCertificateFolder) ) ;
       FileUtils.forceMkdir(    new File(outCertificateFolder) ) ;
       
       LOG.info( "Java Version      : [ " + jVersion               + " ] " ) ;
       LOG.info( "certme_domain     : [ " + domain                 + " ] " ) ;
       LOG.info( "certme_out_folder : [ " + outCertificateFolder   + " ] " ) ;
       LOG.info( "certme_file_name  : [ " + outCertificateFileName + " ] " ) ;
       LOG.info( "certme_interface  : [ " + Interface              + " ] " ) ;
       LOG.info( "certme_port       : [ " + portNum                + " ] " ) ;
       LOG.info( "certme_staging    : [ " + STAGING                + " ] " ) ;
       LOG.info( "certme_force_gen  : [ " + forceGen               + " ] " ) ;
       
       if( new File( outCertificateFolder + outCertificateFileName + "_domain-chain.crt").exists() && 
           new File( outCertificateFolder + outCertificateFileName + "_domain.key").exists()       &&
           ! forceGen ) {
            
             LOG.info("Certme Certificate Already Exists.. " )       ;
           
             LOG.info( " => Domain-chain  : " + outCertificateFolder + outCertificateFileName + "_domain-chain.crt" ) ;
             LOG.info( " => Domain-ckey   : " + outCertificateFolder + outCertificateFileName + "_domain.key"       ) ;
             LOG.info( " ++ They will be used at Runtime ! "                                                        ) ;
           
             return ; 
       }
       
       try {
           
           System.setProperty("quarkus.http.ssl.certificate.file"     ,   "" ) ;
           System.setProperty("quarkus.http.ssl.certificate.key-file" ,   "" ) ;
           
           CertMeBuildTime.run( domain, outCertificateFolder, outCertificateFileName, portNum, Interface ) ;

       } catch ( Exception ex ) {
           
           exception    = ex                                                    ;

           if ( server != null  ) server.stop()                                 ;
            
            LOG.error( "\n" )                                                   ;
            LOG.error( "Certme Failed to get a certificate for the domain [[ "  + 
                       domain + " ]] \n " +  ex.getMessage() + "\n"           ) ;
            
            FileUtils.deleteQuietly( new File(outCertificateFolder )          ) ;

       }
    }
    
    private static void run( String  domain                       , 
                             String  outCertificateFolder         ,
                             String  outCertificateFileName       ,
                             Integer port                         , 
                             String  Interface ) throws Exception {

        /** File name of the User Key Pair.      */
        USER_KEY_FILE = new File( outCertificateFolder     + outCertificateFileName  + "_user.key"  )       ;

        /** File name of the Domain Key Pair.    */
        DOMAIN_KEY_FILE = new File( outCertificateFolder   + outCertificateFileName  + "_domain.key")       ;

        /** File name of the CSR. */
        DOMAIN_CSR_FILE = new File( outCertificateFolder   + outCertificateFileName  + "_domain.csr")       ;

        /** File name of the signed certificate. */
        DOMAIN_CHAIN_FILE = new File( outCertificateFolder + outCertificateFileName  + "_domain-chain.crt") ;
        
        /** File name of the User Key Pair. COPY **/
        USER_KEY_FILE_COPY = new File(  System.getProperty("user.dir")  + File.separator  +
                                        "." + outCertificateFileName    + "_user.key"  )  ;

        try {
                
            LOG.info("Starting up... " )                                ;

            resolveChallengeAndFetchCert( domain , port , Interface )   ;
            
            if( new File( outCertificateFolder + outCertificateFileName + "_domain-chain.crt").exists() && 
                new File( outCertificateFolder + outCertificateFileName + "_domain.key").exists()   )     {

                LOG.info("Certme Cert Generation Success ! " )                                            ;
                System.getProperty("quarkus.http.ssl.certificate.file"    , 
                                    outCertificateFolder +  outCertificateFileName + "_domain-chain.crt") ;
                System.setProperty( "quarkus.http.ssl.certificate.key-file",
                                     outCertificateFolder + outCertificateFileName + "_domain.key"      ) ;
            } else {

                 System.setProperty("quarkus.http.ssl.certificate.file"     , "" ) ;
                 System.setProperty("quarkus.http.ssl.certificate.key-file" , "" ) ;
                 LOG.warn("Certme Failed Cert Generation ! "                     ) ;
            }
 
       } catch ( Exception ex ) {
            
            throw ex ;
        }
    }
   
    /**
     * Generates a certificate for the given domains
     * Takes care for the registration process.
     *
     * @param domains
     *  Domains to get a common certificate for
     * @param targetChallengeToResolve
     * @throws java.io.IOException
     * @throws org.shredzone.acme4j.exception.AcmeException
     */
    private static void fetchCertificate( String domain , int port, String interfce ) throws Exception {
        
        LOG.info( "Fetch Certificates.... " )             ; 
        /** Load the user key file. If there is no key file, create a new one. */
        KeyPair userKeyPair = loadOrCreateUserKeyPair()    ;

        Session session = null ;
        
        if( STAGING.equalsIgnoreCase("PROD") )             {
           session = new Session("acme://letsencrypt.org") ;
        }
        else {
           session = new Session("acme://letsencrypt.org/staging") ;
        }
        
        LOG.warn( "STAGING           : [ " + STAGING  + " ] "    ) ;

        /** Get the Account 
         If there is no account yet, create a new one. */
        Account acct = findOrRegisterAccount(session, userKeyPair) ;

        /** Load or create a key pair for the domains
            This should not be the userKeyPair! .  */
        KeyPair domainKeyPair = loadOrCreateDomainKeyPair()    ;

        /** Order the certificate. */
        Order order = acct.newOrder().domains(domain).create() ;

        /** Perform all required authorizations. */
        for (Authorization auth : order.getAuthorizations())   {
            authorize(auth , interfce, port  )       ;
        }

        /** Generate a CSR for all of the domains, and sign it 
            with the domain key pair.     */
        CSRBuilder csrb = new CSRBuilder() ;
        csrb.addDomains(domain)            ;
        csrb.sign(domainKeyPair)           ;

        /** Write the CSR to a file, for later use. */
        try (Writer out = new FileWriter(DOMAIN_CSR_FILE)) {
            csrb.write(out) ;
        }

        /** Order the certificate.      */
        order.execute(csrb.getEncoded()) ;

        /** Wait for the order to complete. */
        try {
            
            int attempts = 30 ;
            
            while (order.getStatus() != Status.VALID && attempts-- > 0 )  {
                
                /** Did the order fail ? . */
                if (order.getStatus() == Status.INVALID)                  {
                    LOG.error( "-+> " + order.getError().toString()     ) ;
                    server.stop()                                         ;
                    throw new AcmeException("Order failed... Giving up.") ;
                }

                /** Wait for a few seconds. */
                TimeUnit.SECONDS.sleep( 2 ) ;

                /** Then update the status. */
                order.update()              ;
            }
            
        } catch (InterruptedException ex)       {
            LOG.error( "interrupted", ex )      ;
            Thread.currentThread().interrupt()  ;
        }

        /** Get the certificate. */
        Certificate certificate = order.getCertificate() ;

        if( certificate == null ) {
            server.stop()         ;
            throw new RuntimeException("Exeption when generating Certificate") ;
        }
        
        LOG.info("Success ! The certificate for domain [[ " + domain + " ]] Has Been Generated :-) ") ;
        LOG.info("Certificate URL : " + certificate.getLocation())                                    ;

        /** Write a combined file containing the certificate and chain. */
        try ( FileWriter fw = new FileWriter (  DOMAIN_CHAIN_FILE ) ) {
            certificate.writeCertificate(fw) ;
        }
        
        if ( server != null ) server.stop()  ;
        
        authorizeAccessToAllCert()           ;
    }
    
    /**
     * Loads a user key pair from {@value #USER_KEY_FILE}, If the file does not 
     * exist, a new key pair is generated and saves 
     * Keep this key pair in a safe place! In a production environment, 
     * you will not be able to access your account again if you should lose  
     * the key pair.
     *
     * @return User's {@link KeyPair}.
     */
    private static KeyPair loadOrCreateUserKeyPair( ) throws IOException {

        if ( (USER_KEY_FILE_COPY).exists() )                      {
           LOG.info( "KEY_USER Already Exists. Path : "           + 
                      USER_KEY_FILE_COPY.getAbsolutePath() )      ;
           
           FileUtils.copyFile(USER_KEY_FILE_COPY, USER_KEY_FILE ) ;
        }
        
        if ( (USER_KEY_FILE).exists() )                           {
            /** If there is a key file, read it. */
            try (FileReader fr = new FileReader(USER_KEY_FILE))   {
                return KeyPairUtils.readKeyPair(fr) ;
            }

        } else {
            /** If there is none, create a new key pair and save it.  */
            KeyPair userKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE) ;
            try (FileWriter fw = new FileWriter(USER_KEY_FILE))        {
                 KeyPairUtils.writeKeyPair(userKeyPair, fw)            ;
            }
          
            FileUtils.copyFile( USER_KEY_FILE , USER_KEY_FILE_COPY )   ;
            
            return userKeyPair ;
        }
    }

    /**
     * Loads a domain key pair from {@value #DOMAIN_KEY_FILE},  
     * If the file does not exist, a new key pair is generated
     * and saved.
     *
     * @return Domain {@link KeyPair}.
     */
    private static KeyPair loadOrCreateDomainKeyPair() throws IOException {
        
        if (DOMAIN_KEY_FILE.exists()) {
            try (FileReader fr = new FileReader(DOMAIN_KEY_FILE))         {
                return KeyPairUtils.readKeyPair(fr) ;
            }
        } else {
            KeyPair domainKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE)  ;
            try (FileWriter fw = new FileWriter(DOMAIN_KEY_FILE))         {
                KeyPairUtils.writeKeyPair(domainKeyPair, fw)              ;
            }
            return domainKeyPair ;
        }
    }

    /**
     * Finds your {@link Account} at the ACME server. It will be found by your user's public key. If your key is not
     * known to the server yet, a new account will be created.
     * <p>
     * This is a simple way of finding your {@link Account}. A better way is to get the URL and KeyIdentifier of your
     * new account with {@link Account#getLocation()} {@link Session#getKeyIdentifier()} and store it somewhere. If you
     * need to get access to your account later, reconnect to it via {@link Account#bind(Session, URI)} by using the
     * stored location.
     *
     * @param session
     * {@link Session} to bind with
     * @return {@link Login} that is connected to your account
     */
    private static Account findOrRegisterAccount(Session session, KeyPair accountKey) throws AcmeException {
       
       /** Ask the user to accept the TOS, if server provides us with a link 
        URI tos = session.getMetadata().getTermsOfService();
        if ( tos != null )      { 
           acceptAgreement(tos) ; 
        } .  */

        Account account = new AccountBuilder().agreeToTermsOfService()
                                              .useKeyPair(accountKey )
                                              .create( session )     ;

        LOG.info("Registered a new user, URL : " + account.getLocation()) ;

        return account ;
    }

    /** Authorize a domain, It will be associated with your account, so you will be 
     * able to retrieve a signed certificate for the domain later . 
     *
     * @param auth
     * {@link Authorization} to perform
     */
    private static void authorize(Authorization auth, String interfce, int port ) throws Exception {
    
        LOG.info("Authorization for domain [[ " + auth.getIdentifier().getDomain() + " ]] ") ;

        /** The authorization is already valid, No need to process a challenge . */
        if (auth.getStatus() == Status.VALID) {
            return ;
        }

        /** Find the desired challenge and prepare it. */
        Challenge challenge = null ;
        
        switch ( CHALLENGE_TYPE )  {
            
            case HTTP :
                challenge = httpChallenge(auth )             ;
                try {
                       server = new VertxServer( interfce    , 
                                                 port        , 
                                                 ( Http01Challenge ) challenge ) ;
                       TimeUnit.SECONDS.sleep( 1 )           ;
                       if ( ! server.isStarted() )           {
                           TimeUnit.SECONDS.sleep( 2 )       ; 
                           if( server.getException() != null ) {
                               throw new RuntimeException( server.getException()
                                                                 .getMessage() ) ;
                           }
                       }
                } catch ( InterruptedException | RuntimeException ex   ) {
                    throw new RuntimeException(ex )                      ;
                }
                break                                                    ;

            case DNS :
                challenge = dnsChallenge( auth ) ;
                break                            ;
        }

        if ( challenge == null ) {
             throw new AcmeException("No challenge found") ;
        }

        /** If the challenge is already verified, there's no need to 
         *  execute it again. */
        if ( challenge.getStatus() == Status.VALID ) {
             return ;
        }

        /** Now trigger the challenge. */
        challenge.trigger() ;

        /** Poll for the challenge to complete. */
        try {
            int attempts = 15 ;
            while (challenge.getStatus() != Status.VALID && attempts-- > 0  )  {
                /** Did the authorization fail? . */
                if (challenge.getStatus() == Status.INVALID)                   {
                   LOG.error( "x-> " + challenge.getError().toString()     )   ;
                   throw new AcmeException("Challenge failed ( Giving up ) : " + 
                                           challenge.getError().toString() )   ;
                }

                /** Wait for a few seconds. */
                TimeUnit.SECONDS.sleep(2) ;

                /** Then update the status. */
                challenge.update()        ;
            }
        } catch (InterruptedException ex) {
            LOG.error("interrupted", ex)  ;
            Thread.currentThread().interrupt() ;
        }

        /** All reattempts are used up and there is still no valid authorization ?. */
        if (challenge.getStatus() != Status.VALID) {
            LOG.error( "-> " + challenge.getError().toString()  )               ;
            throw new AcmeException("Failed to pass the challenge for domain "  + 
                         auth.getIdentifier().getDomain() + ", ... Giving up.") ;
        }
    }

    /**
     * Prepares a HTTP challenge.
     * @param auth
     * {@link Authorization} to find the challenge in
     * @return {@link Challenge} to verify
     * @throws java.lang.Exception
     */
    private static Challenge httpChallenge(Authorization auth ) throws Exception {
        
        /** Find a single http-01 challenge. */
        Http01Challenge challenge = auth.findChallenge(Http01Challenge.TYPE ) ;
        if (challenge == null) {
            throw new AcmeException( "Found no " + Http01Challenge.TYPE       +
                                     " challenge, don't know what to do...")  ;
        }

        /** Output the challenge, wait for acknowledge... */
        LOG.info("Please create a file in your web server's base directory.") ;
        LOG.info("It must be reachable at   : http://" + auth.getIdentifier().getDomain()         +
                  "/.well-known/acme-challenge/"  + challenge.getToken())                         ;
        LOG.info("File name                 : "   + challenge.getToken())                         ;
        LOG.info("Content                   : "   + challenge.getAuthorization())                 ;
        LOG.info("The file must not contain any leading or trailing whitespaces or line breaks!") ;
        LOG.info("If you're ready, dismiss the dialog... "   )                                    ;
        
        StringBuilder message = new StringBuilder(1024)      ;
        message.append( "Please create a file in your web server's base directory.\n\n") ;
        message.append( "http://").append(auth.getIdentifier().getDomain())
               .append( "/.well-known/acme-challenge/"     )
               .append( challenge.getToken()).append("\n\n") ;
        message.append( "Content:\n\n"                     ) ;
        message.append( challenge.getAuthorization()       ) ;
       
        return challenge                                     ;
    }

    /**
     * Prepares a DNS challenge.
     * <p>
     * The verification of this challenge expects a TXT record with a certain content. 
     * <p>
     * This example outputs instructions that need to be executed manually. 
     * In a production environment, you would rather configure your DNS automatically.
     *
     * @param auth
     *            {@link Authorization} to find the challenge in
     * @return {@link Challenge} to verify
     * @throws org.shredzone.acme4j.exception.AcmeException
     */
    
    private static Challenge dnsChallenge(Authorization auth) throws AcmeException {
        
        /** Find a single dns-01 challenge. */
        Dns01Challenge challenge = auth.findChallenge(Dns01Challenge.TYPE)   ;
        if (challenge == null)   {
            throw new AcmeException( "Found no " + Dns01Challenge.TYPE +
                                     " challenge, don't know what to do...") ;
        }

        /** Output the challenge, wait for acknowledge... */
        LOG.info( "Please create a TXT record:") ;
        LOG.info( "_acme-challenge." + auth.getIdentifier().getDomain()+ ". IN TXT " 
                  + challenge.getDigest()) ;

        /** LOG.info("If you're ready, dismiss the dialog...") ; . */

        StringBuilder message = new StringBuilder(1024)   ;
        message.append("Please create a TXT record:\n\n") ;
        message.append("_acme-challenge.")
               .append(auth.getIdentifier()
               .getDomain()).append(". IN TXT ")
               .append(challenge.getDigest())             ;
        
        /** acceptChallenge(message.toString()) ; .      */

        return challenge ;
    }
    
    private  static void authorizeAccessToAllCert() throws IOException           {
        
        List<String> files = Arrays.asList( USER_KEY_FILE.getAbsolutePath()      ,
                                            USER_KEY_FILE_COPY.getAbsolutePath() ,
                                            DOMAIN_KEY_FILE.getAbsolutePath()    ,
                                            DOMAIN_CSR_FILE.getAbsolutePath()    ,
                                            DOMAIN_CHAIN_FILE.getAbsolutePath()  ) ;
          
        //Setting file permissions for owner, group and others using PosixFilePermission
          
        HashSet<PosixFilePermission> set = new HashSet<>() ;
             
        //Adding owner's file permissions
             
        set.add(PosixFilePermission.OWNER_EXECUTE) ;
        set.add(PosixFilePermission.OWNER_READ)    ;
        set.add(PosixFilePermission.OWNER_WRITE)   ;
             
        //Adding group's file permissions
             
        set.add(PosixFilePermission.GROUP_EXECUTE) ;
        set.add(PosixFilePermission.GROUP_READ)    ;
        set.add(PosixFilePermission.GROUP_WRITE)   ;
             
        //Adding other's file permissions
             
        set.add(PosixFilePermission.OTHERS_EXECUTE) ;
        set.add(PosixFilePermission.OTHERS_READ)    ;
        set.add(PosixFilePermission.OTHERS_WRITE)   ;
             
        files.forEach( file -> {
                
           try {
                  
                if( ! new File( file).exists() ) {
                    LOG.error( "File : " + file + " -- Doesn't Exists !! " ) ;
                } else {
                    LOG.info( "Authorize Access File : " + file            ) ;
                }
                
                Files.setPosixFilePermissions(Paths.get( file ) , set      ) ;

           } catch ( IOException ex ) {
               LOG.error( ex.getMessage(), ex) ;
           }
         } ) ;
    }
    
    /** @param domain
     *  @param port
     *  @param interfce
     */
    private static void resolveChallengeAndFetchCert( String domain, int port , String interfce ) {
    
        Security.addProvider(new BouncyCastleProvider())  ;
        
        try {
             
            fetchCertificate( domain , port , interfce ) ;
            
        } catch (Exception ex)           {
            throw new RuntimeException(ex)               ;
        }
    }
    
    public static String getDomain() throws Exception  {

        try (final Socket socket = new Socket())       {
            
            socket.connect(new InetSocketAddress("google.com", 80 )) ;
            
            String IP_ADRESS = socket.getLocalAddress()
                                     .toString()
                                     .replace("/", "") ;
            
            InetAddress inetAddr = InetAddress.getByName(IP_ADRESS) ;

	    String adress = inetAddr.getCanonicalHostName()  ;
            
            return adress                                    ;
		
        } catch (Exception ex) {
            throw  ex          ;
        }
    }

    public static Exception getException() {
        return exception ;
    }
    
    private static void configLogger()     {
       
        Level level = CertMeLogger.checkLog( "INFO"  )             ;
        Configurator.setRootLevel( level   )                       ;
        Configurator.setAllLevels( "certMe_configuration", level ) ;
    }
}

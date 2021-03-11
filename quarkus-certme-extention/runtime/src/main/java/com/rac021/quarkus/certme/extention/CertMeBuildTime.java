
package com.rac021.quarkus.certme.extention ;

import java.io.File ;
import java.security.KeyPair ;
import java.security.PublicKey ;
import org.jboss.logging.Logger ;
import java.security.PrivateKey ;
import org.apache.commons.io.FileUtils ;
import java.security.cert.X509Certificate ;
import com.rac021.quarkus.certme.utils.Config ;
import io.quarkus.runtime.annotations.ConfigRoot ;
import com.rac021.quarkus.certme.utils.AcmeClient ;
import io.quarkus.runtime.annotations.ConfigPhase ;
import javax.enterprise.context.ApplicationScoped ;
import com.rac021.quarkus.certme.utils.CertifUtils ;
import org.wildfly.security.x500.cert.acme.AcmeAccount ;
import org.wildfly.security.x500.cert.acme.AcmeException ;
import org.wildfly.security.x500.cert.acme.AcmeAccount.Builder ;
import com.rac021.quarkus.certme.utils.SelfSignedCertGenerator ;
import static com.rac021.quarkus.certme.utils.CertifUtils.writeToFile ;
import org.wildfly.security.x500.cert.X509CertificateChainAndSigningKey ;

/**
 *
 * @author ryahiaoui
 */

@ApplicationScoped
@ConfigRoot( name = "certme" , phase = ConfigPhase.BUILD_TIME )
public class CertMeBuildTime {

    private static final String LETS_ENCRYPT_SERVER         = "https://acme-v02.api.letsencrypt.org/directory"         ;
     
    private static final String LETS_ENCRYPT_STAGING_SERVER = "https://acme-staging-v02.api.letsencrypt.org/directory" ;

    private static final Logger LOG = Logger.getLogger(CertMeBuildTime.class.getName() ) ;

    private final AcmeClient    ACME_CLIENT ;

    private final Config        CONFIG      ;

    static Exception            exception   ; 
    
    public CertMeBuildTime() throws Exception                         {

        LOG.info( "Trying to Generate Let's Encrypt Certificate.." )  ;
        this.CONFIG       = new Config()                              ;
        this.ACME_CLIENT  = new AcmeClient( CONFIG )                  ;
        this.tryGeneratingLetsEncryptCertif ()                        ;
    }
    
    private void tryGeneratingLetsEncryptCertif ( ) throws Exception  {

        try {

            boolean staging               = CONFIG.ENV.equals( "DEV" )            ;
            boolean termsOfServiceAgreed  = CONFIG.TERMS_OF_SERVICE_AGREED        ;

            String  certifPath    = CONFIG.FOLDER  + CONFIG.CERTIF_FILE_NAME      ; 
            String  certifKeyPath = CONFIG.FOLDER  + CONFIG.CERTIF_KEY_FILE_NAME  ;
            
            String  userKeyPath   = CONFIG.FOLDER  + CONFIG.USER_KEY_FILE_NAME    ;
            
            FileUtils.forceMkdir( new File( CONFIG.FOLDER ) )                     ;
            
            /** Build Account **/
            Builder accountBuilder = AcmeAccount.builder()
                                                .setTermsOfServiceAgreed( termsOfServiceAgreed )
                                                .setServerUrl( LETS_ENCRYPT_SERVER )
                                                .setStagingServerUrl( LETS_ENCRYPT_STAGING_SERVER )
                                                .setKeySize(CONFIG.KEY_SIZE ) ;

            /** If Account Private Key Already Exists. **/
            
            if ( new File( userKeyPath ).exists() )  {
                
                LOG.info( "Account Private Key Found : "  + userKeyPath )                         ;
                
                PrivateKey privateKey = CertifUtils.readRSAPrivateKey ( new File ( userKeyPath )) ;
                
                LOG.info( "Generating X509Certificate Using The Private Key " )                   ;
                
                PublicKey publicKeyFromPrivateKey =
                        CertifUtils.getPublicKeyFromPrivateKey(privateKey     )                   ;

                KeyPair keyPair = new KeyPair( publicKeyFromPrivateKey , privateKey );
                        
                final X509Certificate cert = SelfSignedCertGenerator.generate( keyPair            , 
                                                                               "SHA256withRSA"    , 
                                                                               CONFIG.DOMAIN      ,
                                                                               CONFIG.CERTIF_DAYS ) ;
                LOG.info( "Setting The Generated Certificate to the Account  "                    ) ;
                accountBuilder.setKey( cert, privateKey )                                           ;
            }
            
            if( CONFIG.CONTACT != null )    {
                accountBuilder.setContactUrls ( new String[] { CONFIG.CONTACT } )          ;
            }
            
            AcmeAccount acmeAccount  = accountBuilder.build()                              ;

            LOG.info( "Try Creating Account... "      ) ;
            
            boolean isAccountCreated = ACME_CLIENT.createAccount( acmeAccount  , staging ) ;

            if ( isAccountCreated )  {
                LOG.info( "Account Created ! "        ) ;
            } else {
                LOG.info( "Account Already Exists ! " ) ;
            }

            LOG.info( "Obtain CertificateChain..."    ) ;

            X509CertificateChainAndSigningKey certAndSigningKey = ACME_CLIENT.obtainCertificateChain( acmeAccount    , 
                                                                                                      staging        , 
                                                                                                      CONFIG.DOMAIN  ) ;

            X509Certificate[] certificate = certAndSigningKey.getCertificateChain()    ;
            PrivateKey        privateKey  = certAndSigningKey.getSigningKey()          ;
            
            PrivateKey        userKey     = acmeAccount.getPrivateKey()                ; 
                    
            if ( new File(certifPath   ).exists() ) new File(certifPath).delete()      ;
            if ( new File(certifKeyPath).exists() ) new File(certifKeyPath).delete()   ;
            if ( new File(userKeyPath  ).exists() ) new File(userKeyPath).delete()     ;
            
            writeToFile( certifPath    , CertifUtils.x509CertificateToPem( certificate ) ) ;
            writeToFile( certifKeyPath , CertifUtils.getPrivateKeyAsString( privateKey ) ) ;
            writeToFile( userKeyPath   , CertifUtils.getPrivateKeyAsString( userKey    ) ) ;
            
        } catch( IllegalArgumentException | AcmeException ex ) {
            this.exception = ex           ;
            LOG.warn(ex.getMessage() )    ;
            LOG.info( " A Self-Signed Certificate Will Be Generated At Runtime " ) ;
        }
    }
    
}

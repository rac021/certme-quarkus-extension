
package com.rac021.quarkus.certme.extention ;

/**
 *
 * @author ryahiaoui
 */

import java.io.File ;
import java.security.KeyPair ;
import org.jboss.logging.Logger ;
import java.security.KeyPairGenerator ;
import org.apache.commons.io.FileUtils ;
import java.security.cert.X509Certificate ;
import com.rac021.quarkus.certme.utils.Config ;
import io.quarkus.runtime.annotations.Recorder ;
import javax.enterprise.context.ApplicationScoped ;
import com.rac021.quarkus.certme.utils.CertifUtils ;
import com.rac021.quarkus.certme.utils.SelfSignedCertGenerator ;
import static com.rac021.quarkus.certme.utils.CertifUtils.writeToFile ;

/**
 *
 * @author ryahiaoui
 */

@Recorder
@ApplicationScoped
public class CertMeRuntime      {
    
    private static final Logger LOG = Logger.getLogger(CertMeRuntime.class.getName() ) ;
  
    public void runtimeVerifyCertificates() throws Exception                    { 
       
       Config config = new Config()                                             ;
       
       LOG.info( " "                                                          ) ;
       LOG.info( "######################################################### " ) ;
       LOG.info( "CertMe - Let's Encrypt / SelfSigned Certificate "             +
                 "Generator "                                                 ) ;
       LOG.info( "######################################################### " ) ;
       
       if( config.IGNORE                                                      ) {
           
         LOG.info( "\nCertMe - Certificate Generation IGNORED \n"             ) ;
         return                                                                 ;
       }
       
       String outCertificateFolder   = config.FOLDER               ; 
       String outCertificateFileName = config.CERTIF_FILE_NAME     ;
       String outCertificateFileKey  = config.CERTIF_KEY_FILE_NAME ;
       
       if ( ! outCertificateFolder.trim().endsWith( File.separator ) ) {
              outCertificateFolder += File.separator ;
       }
       
       if ( config.FORCE_GEN )  {
       
           // Force to Gen Certificates at Runtime
           // Because the Let's Encrypt http challenge must be resolved on the port 80
           // You have to be root in order to be able to start server on this port
           
           LOG.info( "Force Let's Encrypt Certificate Generation    : ENABLED "  ) ;

           CertMeBuildTime buildTime = new  CertMeBuildTime() ;
       
       } else {
           
           LOG.info( "Force Let's Encrypt Certificate Generation    : DISABLED " ) ;
       }
       
       LOG.info( "Check for Existing Certificates in the Folder : " + 
                 outCertificateFolder                             ) ;
        
       String certFileName    = outCertificateFolder + outCertificateFileName ;
       String certKeyFileName = outCertificateFolder + outCertificateFileKey  ;
       
       // If Let's Encrypt Certificate Was Successfully Generated 
       
       if( new File( certFileName ).exists() &&  new File( certKeyFileName ).exists()   ) {
            
           LOG.info("Certme - Let's Encrypt Certificate Already Exists.. "              ) ;

           LOG.info( " => Domain-chain : " + certFileName      ) ;
           LOG.info( " => Domain-ckey  : " + certKeyFileName   ) ;

           System.setProperty( "quarkus.http.ssl.certificate.file"    , certFileName)     ;
           System.setProperty( "quarkus.http.ssl.certificate.key-file", certKeyFileName ) ;
           
       } else {
           
           // No Let's Encrypt Certificate 
               
           LOG.warn("Certme - No Let's Encrypt Certificate Found !" )         ;
           
           // Try tu Generate a SelfSigned Cerficiate 

           if ( ! new File(outCertificateFolder).exists()           )         {
               
               FileUtils.forceMkdir( new File(outCertificateFolder) )         ;
           }
           
           String certFile    = outCertificateFolder + "self-SignedCert.crt"  ;
           String certKeyFile = outCertificateFolder + "self-SignedCert.key"  ;
         
           if ( new File(certFile).exists() && new File(certKeyFile).exists() &&
                ! config.FORCE_GEN ) {
               
               LOG.info( "Certme - SelfSigned Certificate Already Exists.. "         ) ;

           } else {
               
               LOG.info( "++ Generate a SelfSigned Certificate... "                  ) ;
               
               KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA") ;
               KeyPair keyPair = keyPairGenerator.generateKeyPair()                    ;
               
               final X509Certificate cert = SelfSignedCertGenerator.generate( keyPair            ,
                                                                              "SHA256withRSA"    , 
                                                                              "localhost"        ,
                                                                              config.CERTIF_DAYS ) ;

               writeToFile( certFile    , CertifUtils.x509CertificateToPem(  cert   ) ) ;
               writeToFile( certKeyFile , CertifUtils.getPrivateKeyAsString( keyPair) ) ;
           }
           
           if ( new File(certFile).exists() && new File(certKeyFile).exists() ) {
               
               LOG.info( " => SelfSigned Certificate : " + certFile           ) ;
               LOG.info( " => SelfSigned Private kEY : " + certKeyFile        ) ;
               
               System.setProperty( "quarkus.http.ssl.certificate.file"     , certFile    ) ;
               System.setProperty( "quarkus.http.ssl.certificate.key-file" , certKeyFile ) ;
           }
       }
    }

}


package com.rac021.quarkus.certme.extention ;

/**
 *
 * @author ryahiaoui
 */

import java.io.File ;
import java.io.FileWriter ;
import java.io.IOException ;
import java.security.KeyPair ;
import org.apache.logging.log4j.Level ;
import java.security.KeyPairGenerator ;
import org.apache.commons.io.FileUtils ;
import org.apache.logging.log4j.Logger ;
import java.security.cert.X509Certificate ;
import org.apache.logging.log4j.LogManager ;
import io.quarkus.runtime.annotations.Recorder ;
import javax.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.core.config.Configurator ;

/**
 *
 * @author ryahiaoui
 */

@Recorder
@ApplicationScoped
public class CertMeRuntime {
    
    private static final Logger LOG = LogManager.getLogger(CertMeRuntime.class.getName() ) ;
  
    public void runtimeVerifyCertificates() throws Exception        {
        
       Level level = CertMeLogger.checkLog( "INFO" )                ;
       Configurator.setRootLevel( level )                           ;
       Configurator.setAllLevels( "certMe_configuration", level   ) ;
     
       String ignore   = System.getProperty("certme_ignore"       ) ;
       
       if( ignore != null                                         ) {
           
         LOG.info( "\nCertMe - Certificate Generation IGNORED \n" ) ;
         return                                                     ;
       }
       
       String outCertificateFolder   = System.getProperty("certme_out_folder")  ;
       String outCertificateFileName = System.getProperty("certme_file_name" )  ;
           
       String dir = System.getProperty("user.dir") ;

       if( outCertificateFileName == null || outCertificateFileName.trim().isEmpty() ) {
           outCertificateFileName = "app"          ;
       }
       
       if (outCertificateFolder == null || outCertificateFolder.trim().isEmpty() ) {
           outCertificateFolder = dir + File.separator + "certMe" + File.separator ;
       }
       if ( ! outCertificateFolder.trim().endsWith( File.separator ) )   {
              outCertificateFolder += File.separator ;
       }
       
       boolean forceGen = System.getProperty("certme_force_gen") != null ;
       
       if ( forceGen )  {
       
           // Force to Gen Certificates at Runtime
           // Because the Let's Encrypt http challenge must be resolved on the port 80
           // You have to be root in order to be able to start server on this port
           
           LOG.info( "Force Let's Encrypt Certificate Generation ENABLE ! "  ) ;
           CertMeBuildTime.genCertificates()                                   ;
       
       } else {
           
           LOG.info( "Force Let's Encrypt Certificate Generation DISABLE ! " ) ;
       }
       
       LOG.info( "Check for Existing Certificates in the Folder : " + outCertificateFolder )        ;
        
       String certFileName    = outCertificateFolder + outCertificateFileName + "_domain-chain.crt" ;
       String certKeyFileName = outCertificateFolder + outCertificateFileName + "_domain.key"       ;
       
       // If Let's Encrypt Certificate Was Successfully Generated 
       
       if( new File( certFileName ).exists() &&  new File( certKeyFileName ).exists()  ) {
            
           LOG.info("Certme - Let's Encrypt Certificate Already Exists.. "             ) ;

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
                ! forceGen ) {
               
               LOG.info( "Certme - SelfSigned Certificate Already Exists.. "         ) ;

           } else {
               
               LOG.info( "++ Generate a SelfSigned Certificate... "                  ) ;
               
               KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA") ;
               keyPairGenerator.initialize( 4096 )                                     ;
               KeyPair keyPair = keyPairGenerator.generateKeyPair()                    ;
               
               final X509Certificate cert = SelfSignedCertGenerator.generate( keyPair         , 
                                                                              "SHA256withRSA" , 
                                                                              "localhost"     ,
                                                                              365           ) ;

               String x509Cert = SelfSignedCertGenerator.x509CertificateToPem(cert)     ;
               String Certkey  = SelfSignedCertGenerator.getPrivateKeyAsString(keyPair) ;

               writeToFile( certFile    , x509Cert ) ;
               writeToFile( certKeyFile , Certkey  ) ;
           
           }
           
           if ( new File(certFile).exists() && new File(certKeyFile).exists() ) {
               
               LOG.info( " => SelfSigned Certificate : " + certFile           ) ;
               LOG.info( " => SelfSigned kEY         : " + certKeyFile        ) ;
               
               System.setProperty( "quarkus.http.ssl.certificate.file"     , certFile    ) ;
               System.setProperty( "quarkus.http.ssl.certificate.key-file" , certKeyFile ) ;
           }
       }
    }
    
    private void writeToFile( String fileName, String content ) {
        
        try( FileWriter writer = new FileWriter( fileName )   ) {
        
            writer.write(content ) ; 
        
        } catch( IOException e   ) {
            throw new RuntimeException( e ) ;
        }
    }
}

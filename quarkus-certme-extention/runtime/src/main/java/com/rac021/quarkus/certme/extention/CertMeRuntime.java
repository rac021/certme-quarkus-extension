
package com.rac021.quarkus.certme.extention ;

/**
 *
 * @author ryahiaoui
 */

import java.io.File ;
import javax.inject.Singleton ;
import org.apache.logging.log4j.Level ;
import org.apache.logging.log4j.Logger ;
import org.apache.logging.log4j.LogManager ;
import io.quarkus.runtime.annotations.Recorder ;
import org.apache.logging.log4j.core.config.Configurator ;

/**
 *
 * @author ryahiaoui
 */

@Singleton
@Recorder
public class CertMeRuntime {
    
    private static final Logger LOG = LogManager.getLogger(CertMeRuntime.class.getName() ) ;
  
    public void runtimeVerifyCertificates() throws Exception      {
        
       Level level = CertMeLogger.checkLog( "INFO"   )            ;
       Configurator.setRootLevel( level )                         ;
       Configurator.setAllLevels( "certMe_configuration", level ) ;
     
       String outCertificateFolder   = System.getProperty("certme_out_folder")  ;
       String outCertificateFileName = System.getProperty("certme_file_name")   ;
           
       String dir = System.getProperty("user.dir") ;

       if( outCertificateFileName == null || outCertificateFileName.trim().isEmpty() ) {
           outCertificateFileName = "app"          ;
       }
       
       if (outCertificateFolder == null || outCertificateFolder.trim().isEmpty() ) {
           outCertificateFolder = dir + File.separator + "certMe" + File.separator ;
       }
       if ( ! outCertificateFolder.trim().endsWith( File.separator ) ) {
              outCertificateFolder += File.separator ;
       }
       
       if ( System.getProperty("certme_force_gen") != null ) {
       
           // Force to Gen Certificates at Runtime
           // Because the Let's Encrypt http challenge must be resolved on the port 80
           // You have to be root in order to be able to start server on this port
           
           LOG.info( "Force Let's Encrypt Certificate Generation ENABLE ! "  ) ;
           CertMeBuildTime.genCertificates()                                   ;
       
       } else {
           
           LOG.info( "Force Let's Encrypt Certificate Generation DISABLE ! " ) ;
       } 
       
       LOG.info( "Check for existing certificates in the folder : " + outCertificateFolder )       ;
           
       if( new File( outCertificateFolder + outCertificateFileName + "_domain-chain.crt").exists() && 
           new File( outCertificateFolder + outCertificateFileName + "_domain.key").exists()      ) {
            
            LOG.info("Certme Certificate Already Exists.. "                                       ) ;

            LOG.info( " => Domain-chain  : " + outCertificateFolder + outCertificateFileName + "_domain-chain.crt" ) ;
            LOG.info( " => Domain-ckey   : " + outCertificateFolder + outCertificateFileName + "_domain.key"       ) ;

            System.setProperty( "quarkus.http.ssl.certificate.file"   , 
                                outCertificateFolder + outCertificateFileName + "_domain-chain.crt") ;
            System.setProperty( "quarkus.http.ssl.certificate.key-file",
                                outCertificateFolder + outCertificateFileName + "_domain.key"      ) ;
           
       } else {
               
           LOG.warn("Certme - No Certificate Found !"  ) ;
       }
       
    }
}

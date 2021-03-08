
package com.rac021.certme.extension.deployment ;

import java.io.File ;
import org.jboss.logging.Logger ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.Order ;
import org.apache.commons.io.FileUtils ;
import org.junit.jupiter.api.BeforeAll ;
import io.quarkus.test.QuarkusUnitTest ;
import io.quarkus.test.junit.QuarkusTest ;
import org.jboss.shrinkwrap.api.ShrinkWrap ;
import static org.junit.jupiter.api.Assertions.* ;
import org.jboss.shrinkwrap.api.asset.EmptyAsset ;
import org.jboss.shrinkwrap.api.spec.JavaArchive ;
import static org.hamcrest.MatcherAssert.assertThat ;
import org.junit.jupiter.api.extension.RegisterExtension ;
import com.rac021.quarkus.certme.extention.CertMeRuntime ;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase ;

/**
 *
 * @author ryahiaoui
 */
@QuarkusTest
public class CertmeExtensionProcessorTest {
    
  private static final Logger LOG = Logger.getLogger( CertmeExtensionProcessorTest.class.getName() ) ;
    
  @RegisterExtension                                                                  
  static final QuarkusUnitTest config = new QuarkusUnitTest().setArchiveProducer(() ->
                              ShrinkWrap.create(JavaArchive.class)                                
                                        .addClasses(CertmeExtensionProcessor.class)
                                        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
  ) ;

  @BeforeAll
  public static void setUpClass() { 
    
       LOG.info( " "                                      ) ;
       LOG.info( "===================================== " ) ;
       LOG.info( "CertMe - CertmeExtensionProcessorTest " ) ;
       LOG.info( "===================================== " ) ;
  }
  
  @Test
  @Order(10)
  public void testDeployementFailedGenCertif() throws Exception {
      
        LOG.info( "Test CertMe localhost.com : 6981  "          +
                  " ( testDeployementFailedGenCertif ) " )      ;
        
        String domain    = "localhost.com"                      ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "6981"                               ;
        String env       = "dev"                                ;
        
        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_env"         , env        ) ;
        // Force Generate Certificates even if it exists
        System.setProperty( "certme_force_gen"   , "true "    ) ;
        
        String dir = System.getProperty("user.dir")             ;
        
        System.setProperty("quarkus.http.ssl.certificate.file"     , "certfFile" ) ;
        System.setProperty("quarkus.http.ssl.certificate.key-file" , "certifKey" ) ;
            
        String outCertifFolder = dir + File.separator + folder  ;
        
        FileUtils.deleteQuietly( new File(outCertifFolder) )    ;
        FileUtils.forceMkdir   ( new File(outCertifFolder) )    ;
       
        CertMeRuntime instance = new CertMeRuntime()            ;
        instance.runtimeVerifyCertificates()                    ;
       
        boolean exist = new File( outCertifFolder ).exists()    ;
        
        assertTrue( exist )                                     ;
        
        String certificateFile = System.getProperty( "quarkus.http.ssl.certificate.file"     ) ;
        String certificateKey  = System.getProperty( "quarkus.http.ssl.certificate.key-file" ) ;
        
        assertThat( certificateFile , containsStringIgnoringCase("self-SignedCert.crt" ) ) ;
        assertThat( certificateKey  , containsStringIgnoringCase("self-SignedCert.key" ) ) ;
  }
    
}

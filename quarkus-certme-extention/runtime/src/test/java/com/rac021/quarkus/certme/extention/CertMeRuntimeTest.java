
package com.rac021.quarkus.certme.extention ;

import java.io.File ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.Order ;
import org.junit.jupiter.api.AfterAll ;
import org.junit.jupiter.api.AfterEach ;
import org.junit.jupiter.api.BeforeAll ;
import org.apache.commons.io.FileUtils ;
import org.junit.jupiter.api.BeforeEach ;
import org.junit.jupiter.api.MethodOrderer ;
import org.junit.jupiter.api.TestMethodOrder ;
import static org.junit.jupiter.api.Assertions.* ;

/**
 *
 * @author ryahiaoui
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CertMeRuntimeTest {
    
    public CertMeRuntimeTest() { }
    
    @BeforeAll
    public static void setUpClass()    { }
    
    @AfterAll
    public static void tearDownClass() { }
    
    @BeforeEach
    public void setUp()    { }
    
    @AfterEach
    public void tearDown() { }

    /**
     * Test of runtimeVerifyCertificates method, of class CertMeRuntime.
     * @throws java.lang.Exception
     */
    @Test
    @Order(7)
    public void testRuntimeVerifyCertificates() throws Exception {
        
        System.out.println( "\nRuntimeVerifyCertificates Test" ) ;
        
        String domain    = "localhost.fr"                       ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "6981"                               ;
        String staging   = "dev"                                ;
        
        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_staging"     , staging    ) ;
        // Force Generate Certificates even if it exists
        System.setProperty( "certme_force_gen"   , "true"     ) ;
        
        String dir = System.getProperty("user.dir")             ;
        
        System.setProperty("quarkus.http.ssl.certificate.file"     , "certfFile" ) ;
        System.setProperty("quarkus.http.ssl.certificate.key-file" , "certifKey" ) ;
            
        String outCertifFolder = dir + File.separator + folder  ;
        
        FileUtils.deleteQuietly( new File(outCertifFolder) )    ;

        CertMeRuntime instance = new CertMeRuntime()            ;
        instance.runtimeVerifyCertificates()                    ;
       
        boolean exist = new File( outCertifFolder ).exists()    ;
        
        assertFalse( exist )                                    ;
        
        String certificateFile = System.getProperty( "quarkus.http.ssl.certificate.file"     ) ;
        String certificateKey  = System.getProperty( "quarkus.http.ssl.certificate.key-file" ) ;
        
        assertEquals( certificateFile , ""  ) ;
        assertEquals( certificateKey  , ""  ) ;

    }
    
}

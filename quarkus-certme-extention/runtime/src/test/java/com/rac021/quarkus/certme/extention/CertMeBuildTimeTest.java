
package com.rac021.quarkus.certme.extention ;

import java.io.File ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.Order ;
import org.junit.jupiter.api.AfterAll ;
import org.junit.jupiter.api.AfterEach ;
import org.junit.jupiter.api.BeforeAll ;
import org.apache.commons.io.FileUtils ;
import org.junit.jupiter.api.BeforeEach ;
import org.junit.jupiter.api.TestMethodOrder ;
import static org.hamcrest.MatcherAssert.assertThat ;
import static org.hamcrest.CoreMatchers.containsString ;
import static org.junit.jupiter.api.Assertions.assertTrue ;
import static org.junit.jupiter.api.Assertions.assertFalse ;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation ;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase ;

/**
 *
 * @author ryahiaoui
 */

@TestMethodOrder(OrderAnnotation.class)
public class CertMeBuildTimeTest {

    public CertMeBuildTimeTest() { }

    @BeforeAll
    public static void setUpClass()    { }
    
    @AfterAll
    public static void tearDownClass() { }

    @BeforeEach
    public void setUp()    { }

    @AfterEach
    public void tearDown() { }

    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Order(1)
    public void testGenCertificatesLocalHost80Fail() throws Exception {

        System.out.println( "\nGenCertificates Test Localhost - " + 
                            "certme_port : 80"                  ) ;

        String domain    = "localhost"                          ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "80"                                 ;
        String staging   = "dev"                                ;
        
        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_staging"     , staging    ) ;

        String expectedMessage = "Cannot issue for \"" + domain + 
                                 "\": Domain name needs at least one dot" ;
        
        CertMeBuildTime.genCertificates()                       ;
       
        Exception exception = CertMeBuildTime.getException()    ;
        
        assertThat( exception.getMessage() , containsString(expectedMessage ) ) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Order(2)
    public void testGenCertificatesLocalHostCom80ComFail() throws Exception {

        String domain    = "localhost.com"                      ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "80"                                 ;
        String staging   = "dev"                                ;
        
        System.out.println( "GenCertificates Test " + domain    +
                            " - certme_port : " + port        ) ;

        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_staging"     , staging    ) ;

        String expectedMessage1 = "Permission non accordée"     ;
        String expectedMessage2 = "Permission Denied"           ;
        String expectedMessage3 = "Adresse déjà utilisée"       ;
        String expectedMessage4 = "Address already in use"      ;
        String expectedMessage5 = "Challenge failed ( Giving up ) : Fetching http://" + 
                                   domain + "/.well-known/acme-challenge/"            ;
        
        CertMeBuildTime.genCertificates()                       ;
       
        Exception exception = CertMeBuildTime.getException()    ;
        
        assertThat( exception.getMessage() ,
                    org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                 containsStringIgnoringCase(expectedMessage2 ) ,
                                                 containsStringIgnoringCase(expectedMessage3 ) , 
                                                 containsStringIgnoringCase(expectedMessage4 ) , 
                                                 containsStringIgnoringCase(expectedMessage5 ))) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Order(3)
    public void testGenCertificatesGoogle6981Fail() throws Exception {

        String domain    = "google.com"                         ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "6981"                               ;
        String staging   = "dev"                                ;
       
        System.out.println( "GenCertificates Test google.com "  + 
                            "certme_port : " + port           ) ;

        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_staging"     , staging    ) ;

        String expectedMessage = "The ACME server refuses to issue a certificate "         +
                                 "for this domain name, because it is forbidden by policy" ;
        
        CertMeBuildTime.genCertificates()                       ;
       
        Exception exception = CertMeBuildTime.getException()    ;
        
        assertThat( exception.getMessage() , containsString(expectedMessage ) ) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Order(4)
    public void testGenCertificatesInriaDomain6981Fail() throws Exception {

        String domain    = "www.inria.fr"                       ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "6981"                               ;
        String staging   = "dev"                                ;
        
        System.out.println( "GenCertificates Test www.inria.fr" +
                            " - certme_port : " + port        ) ;

        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_staging"     , staging    ) ;
        
        String expectedMessage = "Challenge failed ( Giving up ) : Invalid response from https://" + 
                                  domain +"/.well-known/acme-challenge/"                           ;
        
        CertMeBuildTime.genCertificates()                       ;
       
        Exception exception = CertMeBuildTime.getException()    ;
        
        assertThat( exception.getMessage() , containsString( expectedMessage) ) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Order(5)
    public void testGenCertificatesInraeDomain80Fail() throws Exception {

        String domain    = "www.inrae.fr"                       ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "80"                                 ;
        String staging   = "dev"                                ;
        
        System.out.println( "GenCertificates Test " + domain    +
                            "- certme_port : " + port         ) ;

        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_staging"     , staging    ) ;
        
        String expectedMessage1 = "Permission non accordée"     ;
        String expectedMessage2 = "Permission Denied"           ;
        String expectedMessage3 = "Adresse déjà utilisée"       ;
        String expectedMessage4 = "Address already in use"      ;
        String expectedMessage5 = "Challenge failed ( Giving "  +
                                  "up ) : Invalid response "    +
                                  "from https://" + domain      ;
      
        CertMeBuildTime.genCertificates()                       ;
       
        Exception exception = CertMeBuildTime.getException()    ;
        
        assertThat( exception.getMessage() ,
                    org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                 containsStringIgnoringCase(expectedMessage2 ) ,
                                                 containsStringIgnoringCase(expectedMessage3 ) ,
                                                 containsStringIgnoringCase(expectedMessage4 ) ,
                                                 containsStringIgnoringCase(expectedMessage5 ) )) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    @Order(6)
    public void testGenCertificatesCurrentDomain80() throws Exception {

        String domain    =  CertMeBuildTime.getDomain()         ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "80"                                 ;
        String staging   = "dev"                                ;
        String folder    = "certMe"                             ;
        
        System.out.println( "GenCertificates Test " + domain    +
                            " - certme_port : " + port        ) ;

        System.setProperty( "certme_domain"      , domain     ) ;
        System.setProperty( "certme_out_folder"  , folder     ) ;
        System.setProperty( "certme_file_name"   , appName    ) ;
        System.setProperty( "certme_interface"   , interFace  ) ;
        System.setProperty( "certme_port"        , port       ) ;
        System.setProperty( "certme_staging"     , staging    ) ;
        
        String dir = System.getProperty("user.dir")             ;
        
        FileUtils.deleteQuietly( new File( dir + File.separator +
                                           folder ) )           ;
        
        CertMeBuildTime.genCertificates()                       ;
       
        Exception exception = CertMeBuildTime.getException()    ;
        
        if ( exception == null ) {

             boolean exist = new File( dir + File.separator + folder )
                                                 .exists()           ;
             assertTrue( exist   )                                   ;
             FileUtils.deleteQuietly( new File(folder ) )            ;
        }
        
        else {
            
            String expectedMessage1 = "Permission non accordée"     ;
            String expectedMessage2 = "Permission Denied"           ;
            String expectedMessage3 = "Adresse déjà utilisée"       ;
            String expectedMessage4 = "Address already in use"      ;
            String expectedMessage5 = "The ACME server can not "    +
                                      "issue a certificate for an " +
                                      "IP address"                  ;

            boolean exist = new File( dir + File.separator + folder )
                                                .exists()           ;
            assertFalse( exist )                                    ;
             
            assertThat( exception.getMessage() ,
                        org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                     containsStringIgnoringCase(expectedMessage2 ) ,
                                                     containsStringIgnoringCase(expectedMessage3 ) ,
                                                     containsStringIgnoringCase(expectedMessage4 ) ,
                                                     containsStringIgnoringCase(expectedMessage5 ) )) ;
        }
    }

}

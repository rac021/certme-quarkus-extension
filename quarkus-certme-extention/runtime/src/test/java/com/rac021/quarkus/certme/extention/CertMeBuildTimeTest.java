
package com.rac021.quarkus.certme.extention ;

import java.io.File ;
import org.jboss.logging.Logger ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.AfterAll ;
import org.junit.jupiter.api.AfterEach ;
import org.junit.jupiter.api.BeforeAll ;
import org.apache.commons.io.FileUtils ;
import org.junit.jupiter.api.BeforeEach ;
import io.quarkus.test.junit.QuarkusTest ;
import static org.hamcrest.CoreMatchers.is ;
import static org.hamcrest.MatcherAssert.assertThat ;
import com.rac021.quarkus.certme.utils.DomainResolver ;
import static org.junit.jupiter.api.Assertions.assertTrue ;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase ;

/**
 *
 * @author ryahiaoui
 */

@QuarkusTest
public class CertMeBuildTimeTest {

    private static final Logger LOG = Logger.getLogger( CertMeBuildTimeTest.class.getName() ) ;
  
    public CertMeBuildTimeTest() {  }

    @BeforeAll
    public static void setUpClass() { 

       LOG.info( " "                                 ) ;
       LOG.info( "================================ " ) ;
       LOG.info( "CertMe - CertMeBuildTimeTest     " ) ;
       LOG.info( "================================ " ) ;
    }
    
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
    public void testGenCertificatesLocalHost80Fail() throws Exception {

        LOG.info( "\nGenCertificates Test Localhost - "             + 
                   "certme_port : 80"                               ) ;

        String domain    = "localhost"                                ;
        String folder    = "certMe"                                   ;
        String appName   = "app"                                      ;
        String interFace = "0.0.0.0"                                  ;
        String port      = "80"                                       ;
        String env       = "dev"                                      ;
        
        System.setProperty( "certme_domain"            , domain     ) ;
        System.setProperty( "certme_folder"            , folder     ) ;
        System.setProperty( "certme_file_name"         , appName    ) ;
        System.setProperty( "certme_binding_interface" , interFace  ) ;
        System.setProperty( "certme_port"              , port       ) ;
        System.setProperty( "certme_env"               , env        ) ;

        String expectedMessage1 = "Error creating new order :: Cannot issue for \"" +
                                  domain +"\": Domain name needs at least one dot"  ;
        
        String expectedMessage2 = "Rate limit has been exceeded" ;
        
        CertMeBuildTime certBuildTime = new CertMeBuildTime()    ;
       
        Exception exception = certBuildTime.exception            ;
        
        assertThat( exception.getMessage() ,
                    org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                 containsStringIgnoringCase(expectedMessage2 ))) ;
   }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenCertificatesLocalHostCom80ComFail() throws Exception {

        String domain    = "localhost.com"                      ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "80"                                 ;
        String env       = "dev"                                ;
        
        System.out.println( "GenCertificates Test " + domain    +
                            " - certme_port : "     + port    ) ;

        System.setProperty( "certme_domain"            , domain     ) ;
        System.setProperty( "certme_folder"            , folder     ) ;
        System.setProperty( "certme_file_name"         , appName    ) ;
        System.setProperty( "certme_binding_interface" , interFace  ) ;
        System.setProperty( "certme_port"              , port       ) ;
        System.setProperty( "certme_env"               , env        ) ;

        String expectedMessage1 = "Permission non accordée"     ;
        String expectedMessage2 = "Permission Denied"           ;
        String expectedMessage3 = "Adresse déjà utilisée"       ;
        String expectedMessage4 = "Address already in use"      ;
        String expectedMessage5 = "Challenge failed ( Giving up ) : Fetching http://" + 
                                   domain + "/.well-known/acme-challenge/"            ;
        
        String expectedMessage6 = "Rate limit has been exceeded" ;
        
        CertMeBuildTime certBuildTime = new CertMeBuildTime()    ;
       
        Exception exception = certBuildTime.exception            ;
         
        assertThat( exception.getMessage() ,
                    org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                 containsStringIgnoringCase(expectedMessage2 ) ,
                                                 containsStringIgnoringCase(expectedMessage3 ) , 
                                                 containsStringIgnoringCase(expectedMessage4 ) , 
                                                 containsStringIgnoringCase(expectedMessage5 ) , 
                                                 containsStringIgnoringCase(expectedMessage6 ))) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenCertificatesGoogle6981Fail() throws Exception {

        String domain    = "google.com"                         ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "6981"                               ;
        String env       = "dev"                                ;
       
        System.out.println( "GenCertificates Test google.com "  + 
                            "certme_port : " + port           ) ;

        System.setProperty( "certme_domain"            , domain     ) ;
        System.setProperty( "certme_folder"            , folder     ) ;
        System.setProperty( "certme_file_name"         , appName    ) ;
        System.setProperty( "certme_binding_interface" , interFace  ) ;
        System.setProperty( "certme_port"              , port       ) ;
        System.setProperty( "certme_env"               , env        ) ;

        String expectedMessage1 = "The ACME server refuses to issue a certificate "         +
                                  "for this domain name, because it is forbidden by policy" ;
        
        String expectedMessage2 = "Rate limit has been exceeded" ;
        
        CertMeBuildTime certBuildTime = new CertMeBuildTime()    ;
       
        Exception exception = certBuildTime.exception            ;
        
        assertThat( exception.getMessage() ,
                    org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                 containsStringIgnoringCase(expectedMessage2 ))) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenCertificatesInriaDomain6981Fail() throws Exception {

        String domain    = "www.inria.fr"                       ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "6981"                               ;
        String env       = "dev"                                ;
        
        System.out.println( "GenCertificates Test www.inria.fr" +
                            " - certme_port : " + port        ) ;

        System.setProperty( "certme_domain"            , domain     ) ;
        System.setProperty( "certme_folder"            , folder     ) ;
        System.setProperty( "certme_file_name"         , appName    ) ;
        System.setProperty( "certme_binding_interface" , interFace  ) ;
        System.setProperty( "certme_port"              , port       ) ;
        System.setProperty( "certme_env"               , env        ) ;
        
        String expectedMessage1 = "Challenge failed ( Giving up ) : Invalid response from https://" + 
                                   domain +"/.well-known/acme-challenge/"                           ;
        
        String expectedMessage2 = "Rate limit has been exceeded" ;
        
        CertMeBuildTime certBuildTime = new CertMeBuildTime()    ;
       
        Exception exception = certBuildTime.exception            ;
        
        assertThat( exception.getMessage() ,
                    org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                 containsStringIgnoringCase(expectedMessage2 ))) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenCertificatesInraeDomain80Fail() throws Exception {

        String domain    = "www.inrae.fr"                       ;
        String folder    = "certMe"                             ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "80"                                 ;
        String env       = "dev"                                ;
        
        System.out.println( "GenCertificates Test " + domain    +
                            "- certme_port : "      + port    ) ;

        System.setProperty( "certme_domain"            , domain     ) ;
        System.setProperty( "certme_folder"            , folder     ) ;
        System.setProperty( "certme_file_name"         , appName    ) ;
        System.setProperty( "certme_binding_interface" , interFace  ) ;
        System.setProperty( "certme_port"              , port       ) ;
        System.setProperty( "certme_env"               , env        ) ;
        
        String expectedMessage1 = "Permission non accordée"     ;
        String expectedMessage2 = "Permission Denied"           ;
        String expectedMessage3 = "Adresse déjà utilisée"       ;
        String expectedMessage4 = "Address already in use"      ;
        String expectedMessage5 = "Challenge failed ( Giving "  +
                                  "up ) : Invalid response "    +
                                  "from https://" + domain      ;
      
        String expectedMessage6 = "Rate limit has been exceeded" ;
        
        CertMeBuildTime certBuildTime = new CertMeBuildTime()    ;
       
        Exception exception = certBuildTime.exception            ;
       
        assertThat( exception.getMessage() ,
                    org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                 containsStringIgnoringCase(expectedMessage2 ) ,
                                                 containsStringIgnoringCase(expectedMessage3 ) ,
                                                 containsStringIgnoringCase(expectedMessage4 ) ,
                                                 containsStringIgnoringCase(expectedMessage5 ) ,
                                                 containsStringIgnoringCase(expectedMessage6 ) )) ;
    }
    
    /**
     * Test of genCertificates method, of class CertMeBuildTime.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGenCertificatesCurrentDomain80() throws Exception {

        String domain    =  DomainResolver.resolve()            ;
        String appName   = "app"                                ;
        String interFace = "0.0.0.0"                            ;
        String port      = "80"                                 ;
        String env       = "dev"                                ;
        String folder    = "certMe"                             ;
        
        System.out.println( "GenCertificates Test " + domain    +
                            " - certme_port : " + port        ) ;

        System.setProperty( "certme_domain"            , domain     ) ;
        System.setProperty( "certme_folder"            , folder     ) ;
        System.setProperty( "certme_file_name"         , appName    ) ;
        System.setProperty( "certme_binding_interface" , interFace  ) ;
        System.setProperty( "certme_port"              , port       ) ;
        System.setProperty( "certme_env"               , env        ) ;
        
        String dir = System.getProperty("user.dir")             ;
        
        FileUtils.deleteQuietly( new File( dir + File.separator +
                                           folder ) )           ;
        
        CertMeBuildTime certBuildTime = new CertMeBuildTime()   ;
       
        Exception exception = certBuildTime.exception           ;
        
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
            
            String expectedMessage6 = "Rate limit has been exceeded" ;

            boolean existDir = new File( dir + File.separator + folder )
                                                   .exists()           ;
            assertTrue( existDir )                                     ;
          
            int totFiles =  new File( dir + File.separator + folder).list().length ;
            
            assertThat( totFiles, is(0) )                                          ;
                     
            assertThat( exception.getMessage() ,
                        org.hamcrest.Matchers.anyOf( containsStringIgnoringCase(expectedMessage1 ) ,
                                                     containsStringIgnoringCase(expectedMessage2 ) ,
                                                     containsStringIgnoringCase(expectedMessage3 ) ,
                                                     containsStringIgnoringCase(expectedMessage4 ) ,
                                                     containsStringIgnoringCase(expectedMessage5 ) ,
                                                     containsStringIgnoringCase(expectedMessage6 ) )) ;
        }
    }

}

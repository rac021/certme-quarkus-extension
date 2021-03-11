
package com.rac021.quarkus.certme.extention ;

import java.security.KeyPair ;
import org.jboss.logging.Logger ;
import org.junit.jupiter.api.Test ;
import java.security.KeyPairGenerator ;
import org.junit.jupiter.api.BeforeAll ;
import io.quarkus.test.junit.QuarkusTest ;
import java.security.cert.X509Certificate ;
import org.bouncycastle.cert.CertIOException ;
import java.security.NoSuchAlgorithmException ;
import java.security.cert.CertificateException ;
import static org.junit.jupiter.api.Assertions.* ;
import static org.hamcrest.MatcherAssert.assertThat ;
import static org.hamcrest.Matchers.equalToIgnoringCase ;
import org.bouncycastle.operator.OperatorCreationException ;
import com.rac021.quarkus.certme.utils.SelfSignedCertGenerator ;
import static com.rac021.quarkus.certme.utils.CertifUtils.x509CertificateToPem ;

/**
 *
 * @author ryahiaoui
 */
@QuarkusTest
public class SelfSignedCertGeneratorTest {
    
    private static final Logger LOG = Logger.getLogger( CertMeBuildTimeTest.class.getName() ) ;
  
    public SelfSignedCertGeneratorTest() { }
   
    @BeforeAll
    public static void setUpClass() {
        
       LOG.info( " "                                      ) ;
       LOG.info( "===================================== " ) ;
       LOG.info( "CertMe - SelfSignedCertGeneratorTest  " ) ;
       LOG.info( "===================================== " ) ;
    }
    
    @Test
    public void createSelfSignedCertificate() throws CertificateException     , CertIOException          ,
                                                     OperatorCreationException, NoSuchAlgorithmException , 
                                                     Exception                {
      
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA") ;
      keyPairGenerator.initialize( 4096 )                  ;
      KeyPair keyPair = keyPairGenerator.generateKeyPair() ;
      
      final X509Certificate cert = SelfSignedCertGenerator.generate(keyPair, "SHA256withRSA", "localhost", 730 ) ;
  
      String x509CertificateToPem = x509CertificateToPem(cert)                         ;
      System.out.println( "x509CertificateToPem = " + x509CertificateToPem )           ;
              
      assertNull(   cert.getKeyUsage()                                               ) ;
      assertNull(   cert.getExtendedKeyUsage()                                       ) ;
      assertEquals( "X.509"              , cert.getType()                            ) ;
      assertEquals( "CN=localhost"       , cert.getSubjectDN().getName()             ) ;
      assertEquals( cert.getSubjectDN()  , cert.getIssuerDN()                        ) ;
      assertEquals( 3                    , cert.getVersion()                         ) ;
      assertThat( cert.getSigAlgName()   , equalToIgnoringCase("SHA256withRSA"     ) ) ;
      
    }
}


package com.rac021.quarkus.certme.extention ;

import java.security.KeyPair ;
import org.junit.jupiter.api.Test ;
import java.security.KeyPairGenerator ;
import java.security.cert.X509Certificate ;
import org.bouncycastle.cert.CertIOException ;
import java.security.NoSuchAlgorithmException ;
import java.security.cert.CertificateException ;
import static org.junit.jupiter.api.Assertions.* ;
import org.bouncycastle.operator.OperatorCreationException ;

/**
 *
 * @author ryahiaoui
 */
public class SelfSignedCertGeneratorTest {
    
    public SelfSignedCertGeneratorTest() {
    }
    
    @Test
    public void createSelfSignedCertificate() throws CertificateException     , CertIOException          ,
                                                     OperatorCreationException, NoSuchAlgorithmException , 
                                                     Exception                {
      
      KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA") ;
      keyPairGenerator.initialize( 4096 )                  ;
      KeyPair keyPair = keyPairGenerator.generateKeyPair() ;
      
      final X509Certificate cert = SelfSignedCertGenerator.generate(keyPair, "SHA256withRSA", "localhost", 730 ) ;
  
      String x509CertificateToPem = SelfSignedCertGenerator.x509CertificateToPem(cert) ;
      System.out.println( "x509CertificateToPem = " + x509CertificateToPem )           ;
              
      assertNull(cert.getKeyUsage()                              ) ;
      assertNull(cert.getExtendedKeyUsage()                      ) ;
      assertEquals("X.509", cert.getType()                       ) ;
      assertEquals("CN=localhost", cert.getSubjectDN().getName() ) ;
      assertEquals(cert.getSubjectDN(), cert.getIssuerDN()       ) ;
      assertEquals("SHA256withRSA", cert.getSigAlgName()         ) ;
      assertEquals(3, cert.getVersion()                          ) ;
      
    }
}

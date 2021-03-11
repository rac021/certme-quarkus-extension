
package com.rac021.quarkus.certme.utils ;

import java.util.Base64 ;
import java.io.FileWriter ;
import java.io.IOException ;
import java.io.StringWriter ;
import java.security.KeyPair ;
import java.security.PrivateKey;
import java.security.cert.X509Certificate ;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter ;

/**
 *
 * @author ryahiaoui
 */

public class CertifUtils {
    
    
  public static void writeToFile( String fileName, String content ) {
        
        try( FileWriter writer = new FileWriter( fileName, true   ) ) {
        
            writer.write(content ) ; 
        
        } catch( IOException e   ) {
            throw new RuntimeException( e ) ;
        }
    }
     
  public static String x509CertificateToPem( final X509Certificate cert ) throws IOException  {

    final StringWriter writer   = new StringWriter()        ;
    try (JcaPEMWriter pemWriter = new JcaPEMWriter(writer)) {
          pemWriter.writeObject(cert) ;
          pemWriter.flush()           ;
    }
    return writer.toString()          ;
  }
  
  public static String x509CertificateToPem( final X509Certificate[] certs ) throws IOException {

    String certsToPem = ""     ;
    
    for ( X509Certificate cert : certs )           {
          certsToPem += x509CertificateToPem( cert ) + "\n" ;
    }
   
    return certsToPem          ;
  
  }
  
  public static String getPrivateKeyAsString( final KeyPair keyPair) throws IOException {
      
    return "-----BEGIN PRIVATE KEY-----\n"                                       +
           Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()) + 
           "\n-----END PRIVATE KEY-----\n"                                       ;
  }
  
  public static String getPrivateKeyAsString( final PrivateKey key) throws IOException {
      
    return "-----BEGIN PRIVATE KEY-----\n"                                       +
           Base64.getEncoder().encodeToString(key.getEncoded())                  + 
           "\n-----END PRIVATE KEY-----\n"                                       ;
  }
    
}

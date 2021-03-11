
package com.rac021.quarkus.certme.utils ;

import java.io.File;
import java.util.Base64 ;
import java.io.FileReader ;
import java.io.FileWriter ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.io.StringWriter ;
import java.security.KeyPair ;
import java.security.PublicKey;
import java.nio.charset.Charset ;
import java.security.PrivateKey ;
import java.security.KeyFactory ;
import java.security.cert.X509Certificate ;
import org.bouncycastle.openssl.PEMReader ;
import java.security.spec.RSAPublicKeySpec ;
import java.security.spec.PKCS8EncodedKeySpec ;
import java.security.interfaces.RSAPrivateCrtKey ;
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

    final StringWriter writer    = new StringWriter()       ;
    try ( JcaPEMWriter pemWriter = new JcaPEMWriter(writer) ) {
          pemWriter.writeObject( cert ) ;
          pemWriter.flush()             ;
    }
    return writer.toString()            ;
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
  
  public static KeyPair readKeyPair( File keyPairPath ) throws IOException {
       
      FileReader fileReader = new FileReader( keyPairPath ) ;
      PEMReader  r          = new PEMReader( fileReader )   ;
     
      try {
            return (KeyPair) r.readObject() ;
        } catch (IOException ex) {
            throw new IOException("The private key could not be decrypted", ex);
        } finally   {
            r.close()          ;
            fileReader.close() ;
        }
  }
  
  public static PrivateKey readRSAPrivateKey( File file ) throws Exception {
    
    String key = new String( Files.readAllBytes( file.toPath() ) ,
                             Charset.defaultCharset()          ) ;

    String privateKeyPEM = key.replace("-----BEGIN PRIVATE KEY-----", ""  )
                              .replaceAll(System.lineSeparator()    , ""  )
                              .replace("-----END PRIVATE KEY-----"  , "") ;

    byte[] encoded = Base64.getDecoder().decode(privateKeyPEM)            ;

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded)        ;
    return keyFactory.generatePrivate(keySpec)                            ;
  }
  
  public static PublicKey getPublicKeyFromPrivateKey( PrivateKey privateKey ) throws Exception {
      
    RSAPrivateCrtKey privk = (RSAPrivateCrtKey)privateKey ;
    RSAPublicKeySpec publicKeySpec = new java.security.spec 
                                             .RSAPublicKeySpec( privk.getModulus() , 
                                                                privk.getPublicExponent() ) ;
    
    KeyFactory keyFactory = KeyFactory.getInstance("RSA") ;
    return keyFactory.generatePublic(publicKeySpec)       ;
              
  }
    
}

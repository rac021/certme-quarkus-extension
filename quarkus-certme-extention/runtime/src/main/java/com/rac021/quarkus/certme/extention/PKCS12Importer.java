
package com.rac021.quarkus.certme.extention ;

/**
 *
 * @author ryahiaoui
 */


import java.io.File ;
import java.util.UUID ;
import java.security.Key ;
import java.io.IOException ;
import java.io.OutputStream ;
import java.util.Enumeration ;
import java.security.KeyStore ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.security.cert.Certificate ;
import org.apache.logging.log4j.Logger ;
import java.security.KeyStoreException ;
import org.apache.logging.log4j.LogManager ;
import java.security.NoSuchAlgorithmException ;
import java.security.UnrecoverableKeyException ;
import java.security.cert.CertificateException ;

/**
 * This class can be used to import a key/certificate pair from a pkcs12 file
 * into a regular JKS format keystore for use with java based SSL applications, etc. 
/**
 * @author ryahiaoui
 */

public class PKCS12Importer {
    
    private static final Logger LOG = LogManager.getLogger( PKCS12Importer.class.getName() ) ;
   
     public static boolean imports( String _pkcs12In      , 
                                    String pkcs12Password ,
                                    String _jksOut        , 
                                    String jksPhrase    ) {

      LOG.info ( "usage: java PKCS12Import {pkcs12_file} [new-jks_file]") ;

      File pkcs12 = new File(_pkcs12In) ;
      File jksOut                    ;
      if (_jksOut != null )          {
         jksOut = new File(_jksOut) ;
      } else {
         jksOut = new File("APP.jks") ;
      }

      if (! pkcs12.canRead() ) {
         LOG.error( "Unable to access input keystore: " + pkcs12.getPath()) ;
         System.exit(2) ;
      }

      if ( jksOut.exists() && !jksOut.canWrite() ) {
         LOG.error( "Output file is not writable: " + jksOut.getPath() ) ;
         System.exit(2) ;
      }

      try {
          
        if( jksPhrase == null ) {
            LOG.warn(" No Phrase was provided"       ) ;
            LOG.warn(" Generate a Random Phrase"     ) ;
            LOG.warn(" ------------------- "         ) ;
            jksPhrase  =  UUID.randomUUID().toString() ;
            LOG.warn(" Jks Phrase : " + jksPhrase    ) ;
            LOG.warn(" -------------------"          ) ;
        }
        
        KeyStore kspkcs12            = KeyStore.getInstance("pkcs12" )    ;
        
        KeyStore ksjks               = KeyStore.getInstance("jks")        ;

        char[]   pkcs12PasswordArray = pkcs12Password.toCharArray()       ;

        char[]   jksPhraseArray      = jksPhrase.toCharArray()            ;

        kspkcs12.load( new FileInputStream(pkcs12), pkcs12PasswordArray ) ;

        ksjks.load ( (jksOut.exists()) ? new FileInputStream(jksOut) : null, jksPhraseArray ) ;

        Enumeration eAliases = kspkcs12.aliases() ;

        while (eAliases.hasMoreElements())        {

           String strAlias = (String)eAliases.nextElement() ;

           if (kspkcs12.isKeyEntry(strAlias))               {

              LOG.info("Adding JKS key with Alias [[ " + strAlias + " ]]        " ) ;

              Key key             = kspkcs12.getKey(strAlias, pkcs12PasswordArray ) ;

              Certificate[] chain = kspkcs12.getCertificateChain(strAlias         ) ;

              ksjks.setKeyEntry(strAlias, key, jksPhraseArray, chain              ) ;
           }
        }

        try ( OutputStream out = new FileOutputStream(jksOut) ) {
              ksjks.store(out, jksPhraseArray                 ) ;
        }
        
        return true ;
        
      } catch( IOException | KeyStoreException | 
               NoSuchAlgorithmException        |
               UnrecoverableKeyException       |
               CertificateException ex )       {
          
          LOG.error( ex.getMessage(), ex ) ;
          System.exit( 7 ) ;
      }
      
      return false ;
      
   }

}

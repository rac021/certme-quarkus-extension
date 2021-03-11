
package com.rac021.quarkus.certme.utils ;

import java.io.File ;

/**
 *
 * @author ryahiaoui
 */

public class Config   {

   private final String KEY_PORT          = "certme_port"         ;
   private final String KEY_ADRESS        = "certme_adress"       ;
   private final String KEY_DOMAIN        = "certme_domain"       ;
   private final String KEY_IGNORE        = "certme_ignore"       ;
   private final String KEY_FOLDER        = "certme_folder"       ;
   private final String KEY_CERTIF_NAME   = "certme_file_name"    ;
   private final String KEY_CERTIF_KEY    = "certme_key_name"     ;
   private final String KEY_USER_KEY      = "certme_user_key"     ;
   private final String KEY_FORCE_GEN     = "certme_force_gen"    ;
   private final String KEY_ENV           = "certme_env"          ; 
   private final String KEY_TERMS_AGREED  = "certme_terms_agreed" ; 

   public final Integer HTTP_PORT_CHALLENGE        ;

   public final String  HTTP_ADRESS_CHALLENGE      ;

   public final String  DOMAIN                     ;

   public final Boolean IGNORE                     ;

   public       String  FOLDER                     ;
   
   public final String  CERTIF_FILE_NAME           ;
   
   public final String  CERTIF_KEY_FILE_NAME       ;
   
   public final String  CERTIF_USER_KEY_FILE_NAME  ;
   
   public final Boolean FORCE_GEN                  ;
   
   public final String  ENV                        ;
   
   public final boolean TERMS_OF_SERVICE_AGREED    ;
   
   public Config() throws Exception {
      
     String dir = System.getProperty("user.dir"    )                                                  ;
       
     this.HTTP_PORT_CHALLENGE        = toInt( getOrDefault( KEY_PORT    , "80"       ) )              ;
     this.HTTP_ADRESS_CHALLENGE      = ( getOrDefault( KEY_ADRESS       , "0.0.0.0"  ) )              ;
     this.DOMAIN                     = ( getOrDefault( KEY_DOMAIN       , DomainResolver.resolve()) ) ;
     this.IGNORE                     = ( getOrDefault( KEY_IGNORE       , false      ) )              ; 
     this.CERTIF_FILE_NAME           = ( getOrDefault( KEY_CERTIF_NAME  , "app.crt"  ) )              ;
     this.CERTIF_KEY_FILE_NAME       = ( getOrDefault( KEY_CERTIF_KEY   , "app.key"  ) )              ;
     this.CERTIF_USER_KEY_FILE_NAME  = ( getOrDefault( KEY_USER_KEY     , "user.key" ) )              ;
     this.FORCE_GEN                  = ( getOrDefault( KEY_FORCE_GEN    , false      ) )              ;
     
     this.ENV                        = ( getOrDefault( KEY_ENV          , "DEV"      ) )              ;
     
     this.TERMS_OF_SERVICE_AGREED    = ( getOrDefault( KEY_TERMS_AGREED ,  true      ) )              ;
     
     this.FOLDER                     = ( getOrDefault( KEY_FOLDER      ,  dir + File.separator       +
                                                                          ".certMe" + File.separator ) ) ;
     
     if ( ! this.FOLDER.trim().endsWith( File.separator )) this.FOLDER += File.separator ;

   }
    
   private String getOrDefault( String key, String defValue )  {
       String value  = System.getProperty( key )    ;
       return ( value == null || 
                value.trim().isEmpty() ) ? defValue : value    ;
   }

    private Boolean getOrDefault( String key, boolean defValue ) {
       String value = System.getProperty( key )    ;
       return value != null && 
            ! value.trim().isEmpty() ? true : defValue ;
    }

    private Integer toInt( String value ) {
      return Integer.parseInt( value)     ;
    }
}

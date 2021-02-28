
package com.rac021.quarkus.certme.extention ;

import org.apache.logging.log4j.Level ;

/**
 *
 * @author ryahiaoui
 */
public class CertMeLogger {
    
    public static Level checkLog( String level )           {

       Level toLevel = Level.toLevel( level.toUpperCase() ) ;
       System.out.println( "\nRetained LOG LEVEL  "         + 
                           toLevel.name()                 ) ;
       return toLevel                                       ;
    }
}

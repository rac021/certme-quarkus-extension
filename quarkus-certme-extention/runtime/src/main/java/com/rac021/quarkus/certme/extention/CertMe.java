
package com.rac021.quarkus.certme.extention ;

import com.google.inject.Singleton ;

/**
 *
 * @author ryahiaoui
 */
@Singleton
public class CertMe {

    public CertMe() {
        System.out.println( "CertMe Constructor !" ) ;
    }
}

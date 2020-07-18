
package com.rac021.quarkus.certme.extention ;

import io.quarkus.arc.runtime.BeanContainer ;
import io.quarkus.runtime.annotations.Recorder ;

/**
 *
 * @author ryahiaoui
 */
@Recorder
public class CertMeRecorder {
    
     public void initCertMeContext( BeanContainer beanContainer) {
        beanContainer.instance( CertMe.class ) ;
    }
}

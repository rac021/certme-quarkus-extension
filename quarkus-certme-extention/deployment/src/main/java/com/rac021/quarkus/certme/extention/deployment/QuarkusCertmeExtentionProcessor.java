
package com.rac021.quarkus.certme.extention.deployment ;

import io.quarkus.deployment.annotations.Record ;
import io.quarkus.deployment.annotations.BuildStep ;
import io.quarkus.deployment.annotations.ExecutionTime ;
import io.quarkus.arc.deployment.BeanContainerBuildItem ;
import io.quarkus.deployment.builditem.FeatureBuildItem ;
import com.rac021.quarkus.certme.extention.CertMeRecorder ;

class QuarkusCertmeExtentionProcessor {

    private static final String FEATURE = "quarkus-certme-extention";

    @BuildStep
    FeatureBuildItem feature() {
       System.out.println( "CertMe Extension from Deployment - FeatureBuildItem " ) ;
       return new FeatureBuildItem(FEATURE) ;
    }
    @BuildStep
    @Record( ExecutionTime.RUNTIME_INIT)
    void recordRuntimeInit( CertMeRecorder certMeRecorder, BeanContainerBuildItem beanContainerBuildItem ) {
       System.out.println( "CertMe Extension from Deployment - RecordRuntimeInit " ) ;
       certMeRecorder.initCertMeContext( beanContainerBuildItem.getValue() ) ;
    }
}

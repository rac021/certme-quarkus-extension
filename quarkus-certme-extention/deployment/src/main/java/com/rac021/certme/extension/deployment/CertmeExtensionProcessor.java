
package com.rac021.certme.extension.deployment ;

import io.quarkus.deployment.annotations.Record ;
import io.quarkus.deployment.annotations.BuildStep ;
import io.quarkus.deployment.annotations.ExecutionTime ;
import com.rac021.quarkus.certme.extention.CertMeRuntime ;

class CertmeExtensionProcessor {

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    public void genLetsEncryptCertificate( CertMeRuntime certMe) throws Exception {
       
        certMe.runtimeVerifyCertificates() ;
    }
}

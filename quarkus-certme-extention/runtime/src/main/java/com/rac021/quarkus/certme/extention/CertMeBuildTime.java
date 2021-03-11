
package com.rac021.quarkus.certme.extention ;

import java.io.File ;
import org.jboss.logging.Logger ;
import java.security.PrivateKey ;
import org.apache.commons.io.FileUtils ;
import java.security.cert.X509Certificate ;
import com.rac021.quarkus.certme.utils.Config ;
import io.quarkus.runtime.annotations.ConfigRoot ;
import com.rac021.quarkus.certme.utils.AcmeClient ;
import io.quarkus.runtime.annotations.ConfigPhase ;
import javax.enterprise.context.ApplicationScoped ;
import com.rac021.quarkus.certme.utils.CertifUtils ;
import org.wildfly.security.x500.cert.acme.AcmeAccount ;
import org.wildfly.security.x500.cert.acme.AcmeException ;
import static com.rac021.quarkus.certme.utils.CertifUtils.writeToFile ;
import org.wildfly.security.x500.cert.X509CertificateChainAndSigningKey ;

/**
 *
 * @author ryahiaoui
 */

@ApplicationScoped
@ConfigRoot( name = "certme" , phase = ConfigPhase.BUILD_TIME )
public class CertMeBuildTime {

    private static final String LETS_ENCRYPT_SERVER         = "https://acme-v02.api.letsencrypt.org/directory"         ;
     
    private static final String LETS_ENCRYPT_STAGING_SERVER = "https://acme-staging-v02.api.letsencrypt.org/directory" ;

    private static final Logger LOG = Logger.getLogger(CertMeBuildTime.class.getName() ) ;

    private final AcmeClient    ACME_CLIENT ;

    private final Config        CONFIG      ;

    public CertMeBuildTime() throws Exception                         {

        LOG.info( "Trying to Generate Let's Encrypt Certificate.." )  ;
        this.CONFIG       = new Config()                              ;
        this.ACME_CLIENT  = new AcmeClient( CONFIG )                  ;
        this.tryGeneratingLetsEncryptCertif ()                        ;
    }
    
    private void tryGeneratingLetsEncryptCertif ( ) throws Exception  {

        try {
            
            boolean staging               = CONFIG.ENV.equals( "DEV")      ;
            boolean termsOfServiceAgreed  = CONFIG.TERMS_OF_SERVICE_AGREED ;

            AcmeAccount acmeAccount = AcmeAccount.builder()
                                                 .setTermsOfServiceAgreed( termsOfServiceAgreed )
                                                 .setServerUrl( LETS_ENCRYPT_SERVER )
                                                 .setStagingServerUrl( LETS_ENCRYPT_STAGING_SERVER  )
                                                 //.setContactUrls( new String[]{ "mailto:admin@example.com" } )
                                                 .build()                                ;

            boolean isAccountCreated = ACME_CLIENT.createAccount( acmeAccount, staging ) ;

            if ( isAccountCreated ) {

            } else {

            }

            X509CertificateChainAndSigningKey certAndSigningKey = ACME_CLIENT.obtainCertificateChain( acmeAccount    , 
                                                                                                      staging        , 
                                                                                                      CONFIG.DOMAIN  ) ;

            X509Certificate[] certificate = certAndSigningKey.getCertificateChain()    ;
            PrivateKey        privateKey  = certAndSigningKey.getSigningKey()          ;
            
            FileUtils.forceMkdir( new File( CONFIG.FOLDER ) ) ;
            
            String certifPath    = CONFIG.FOLDER + CONFIG.CERTIF_FILE_NAME             ; 
            String certifKeyPath = CONFIG.FOLDER + CONFIG.CERTIF_KEY_FILE_NAME         ;
            
            if ( new File(certifPath).exists()    ) new File(certifPath).delete()      ;
            if ( new File(certifKeyPath).exists() ) new File(certifKeyPath).delete()   ;
            
            writeToFile( certifPath    , CertifUtils.x509CertificateToPem( certificate ) ) ;
            writeToFile( certifKeyPath , CertifUtils.getPrivateKeyAsString( privateKey ) ) ;
            
        } catch( IllegalArgumentException | AcmeException ex ) {
            LOG.warn(ex.getMessage() )    ;
            LOG.info( " A Self-Signed Certificate Will Be Generated At Runtime " ) ;
        }
        
    }
}

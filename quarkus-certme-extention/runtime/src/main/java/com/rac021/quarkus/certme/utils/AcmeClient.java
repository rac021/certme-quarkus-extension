
package com.rac021.quarkus.certme.utils ;

import java.util.List ;
import org.wildfly.common.Assert ;
import org.wildfly.security.x500.cert.acme.AcmeAccount ;
import org.wildfly.security.x500.cert.acme.AcmeChallenge ;
import org.wildfly.security.x500.cert.acme.AcmeClientSpi ;
import org.wildfly.security.x500.cert.acme.AcmeException ;
import org.wildfly.extension.elytron._private.ElytronSubsystemMessages ;

/**
 *
 * @author ryahiaoui
 */

public class AcmeClient extends AcmeClientSpi  {
    
    private static final String TOKEN_REGEX = "[A-Za-z0-9_-]+" ;

    Config      config      ;

    VertxServer vertxServer ;

    public AcmeClient( Config config ) {
      this.config = config  ;
    }
        
    @Override
    public AcmeChallenge proveIdentifierControl( AcmeAccount account            ,
                                                 List<AcmeChallenge> challenges ) throws AcmeException {
        
        Assert.checkNotNullParam( "account"   , account       )      ;
        Assert.checkNotNullParam( "challenges", challenges    )      ;
        
        AcmeChallenge selectedChallenge = null                       ;
        
        for ( AcmeChallenge challenge : challenges )                 {

            if ( challenge.getType() == AcmeChallenge.Type.HTTP_01 ) {
                  selectedChallenge   = challenge                    ;
                  // challenge.getToken()                            ; 
                  // challenge.getIdentifierType()                   ; 
                  //challenge.getKeyAuthorization( account )         ;
                  break                                              ;
            }
        }
        
        String token            = selectedChallenge.getToken()                     ;
        String keyAuthorization = selectedChallenge.getKeyAuthorization( account ) ;
        
        vertxServer = new VertxServer( config.BINDING_INTERFACE     , 
                                       config.HTTP_PORT_CHALLENGE   , 
                                       token                        , 
                                       keyAuthorization           ) ;

        if ( !token.matches( TOKEN_REGEX ) )  {
            throw ElytronSubsystemMessages.ROOT_LOGGER
                                          .invalidCertificateAuthorityChallenge()  ;
        }
        
        return selectedChallenge ;
    }

    @Override
    public void cleanupAfterChallenge( AcmeAccount account     ,
                                       AcmeChallenge challenge ) throws AcmeException {
        System.out.println( "CleanupAfterChallenge " )         ;
        vertxServer.stop()                                     ;
    }
}



package com.rac021.quarkus.certme.utils ;

/**
 *
 * @author ryahiaoui
 */

import io.vertx.core.Vertx ;
import io.vertx.ext.web.Router ;
import org.jboss.logging.Logger ;
import org.wildfly.common.Assert ;
import io.vertx.core.http.HttpServerResponse ;

/**
 *
 * @author ryahiaoui
 */

public class VertxServer                                                       {

  private final String  ACME_CHALLENGE_PREFIX = "/.well-known/acme-challenge/" ;
    
  private final Logger  LOG = Logger.getLogger( VertxServer.class.getName()  ) ;
  
  private Vertx         vertx              ;
  
  private Throwable     exception          ; 
  
  private boolean       isStarted  = false ;
  
  public VertxServer( String host , int port ,String token , String athorization ) {
   
       Assert.checkNotNullParam( "host"         , host         ) ;
       Assert.checkNotNullParam( "port"         , port         ) ;
       Assert.checkNotNullParam( "token"        , token        ) ;
       Assert.checkNotNullParam( "athorization" , athorization ) ;
       
       run( port, host, token ,  athorization                  ) ;
  }

  private void run( int port, String host , String token , String authorization ) {
      
      LOG.info( " Try Starting The Server On Port ( " + host + ":" + port       +
                " ) Using HTTP "                                              ) ;
        
      vertx = Vertx.vertx()                  ;
      
      Router router = Router.router( vertx ) ;
      
      router.route( ACME_CHALLENGE_PREFIX + token ).handler(routingContext ->  {
          
          HttpServerResponse response = routingContext.response()              ;
          
          response.putHeader("content-type", "text/html").end( authorization ) ;
          
          LOG.info("Server Called ! ( Probably By Let's Encrypt ) "          ) ;
          
      } ) ;
      
      vertx.createHttpServer()
           .requestHandler(router)
           .listen( port, host, http ->     {
                  if (http.succeeded())     {
                      LOG.info("Http server started on port : " + port ) ;
                      isStarted = true      ;
                  } else if (http.failed()) {
                      LOG.fatal("HTTP server Failed : " + http.cause().getMessage() ) ;
                      exception = http.cause() ;
                  }
      } ) ;
  }
  
  public void stop()  {

    if ( isStarted )  {
         LOG.info("Stoping Vertx Server..." ) ;
         isStarted = false                    ;
         vertx.close()                        ;
    }
  }
  
  public Throwable getException() {
    return exception              ;
  }
  
  public boolean  isStarted()     {
    return isStarted              ;
  }
}
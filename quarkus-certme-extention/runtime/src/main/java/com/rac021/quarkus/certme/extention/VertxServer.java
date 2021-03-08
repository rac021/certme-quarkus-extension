
package com.rac021.quarkus.certme.extention ;

import io.vertx.core.Vertx ;
import io.vertx.ext.web.Router ;
import org.jboss.logging.Logger ;
import io.vertx.core.http.HttpServerResponse ;
import org.shredzone.acme4j.challenge.Http01Challenge ;

/**
 *
 * @author ryahiaoui
 */

public class VertxServer            {

  private Vertx          vertx      ;
  
  private Throwable      exception  ; 
  
  private static final   Logger LOG = Logger.getLogger( VertxServer.class.getName() ) ;
 
  private static boolean isStarted  = false                                           ;
  
  public VertxServer( String host, int port, Http01Challenge challenge ) {
   
       if ( host == null || host.trim().isEmpty() ) throw new RuntimeException("Host can't be null") ;
      
       if ( challenge == null ) throw new RuntimeException("Http01Challenge can't be null         ") ;
   
       run( port, host, challenge )                                      ;
  }

  private void run( int port, String host , Http01Challenge challenge )  {
      
      LOG.info(" Try Starting The Server on Port ( " + host + ":" + port + " ) Using HTTP        ") ;
        
      vertx = Vertx.vertx() ;
      
      // 1: Create a router object.
      Router router = Router.router( vertx ) ;
      
      // 2: Create a route and the associated response
      router.route("/.well-known/acme-challenge/" + challenge.getToken() ).handler(routingContext -> {
          
          HttpServerResponse response = routingContext.response()        ;
          
          response.putHeader("content-type", "text/html")
                  .end(challenge.getAuthorization() )                    ;
          
          LOG.info("Server Called ! ( Probably By Let's Encrypt ) "    ) ;
          
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
  
  public void stop() {

   if ( isStarted )  {
        LOG.info("Stoping Vertx Server..." ) ;
        isStarted = false                    ;
        vertx.close()                        ;
   }
    
  }
  
  public Throwable getException() {
      return exception            ;
  }
  
  public boolean  isStarted()     {
      return isStarted            ;
  }
}

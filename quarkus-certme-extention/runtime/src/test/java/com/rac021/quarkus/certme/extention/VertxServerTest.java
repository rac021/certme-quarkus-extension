
package com.rac021.quarkus.certme.extention ;

import org.jboss.logging.Logger ;
import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.Order ;
import org.junit.jupiter.api.AfterAll ;
import org.junit.jupiter.api.AfterEach ;
import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.BeforeEach ;
import io.quarkus.test.junit.QuarkusTest ;
import org.junit.jupiter.api.MethodOrderer ;
import org.junit.jupiter.api.TestMethodOrder ;
import static org.junit.jupiter.api.Assertions.* ;

/**
 *
 * @author ryahiaoui
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VertxServerTest {
    
    private static final Logger LOG = Logger.getLogger( VertxServerTest.class.getName() ) ;
  
    public VertxServerTest() { }
    
    @BeforeAll
    public static void setUpClass() {
    
       LOG.info( " "                                 ) ;
       LOG.info( "================================ " ) ;
       LOG.info( "CertMe - VertxServerTest         " ) ;
       LOG.info( "================================ " ) ;
    }
    
    @AfterAll
    public static void tearDownClass() { }
    
    @BeforeEach
    public void setUp() { }
    
    @AfterEach
    public void tearDown() { }

    /**
     * Test of getException method, of class VertxServer.
     */
    @Test
    @Order(9)
    public void testGetException() {
        
        LOG.info( "Http Server Vert.x Exception Test" ) ;
        
        RuntimeException assertThrows = assertThrows( RuntimeException.class, () -> {
         
             VertxServer vertxServer = new VertxServer( null, 80, null   ) ;
            
        } ) ;
        
        LOG.info( "Http Server Vert.x Exception : "   + 
                   assertThrows.getMessage() + "\n")  ;
    }

}

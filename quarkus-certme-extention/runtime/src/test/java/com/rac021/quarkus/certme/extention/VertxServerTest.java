
package com.rac021.quarkus.certme.extention ;

import org.junit.jupiter.api.Test ;
import org.junit.jupiter.api.Order ;
import org.junit.jupiter.api.AfterAll ;
import org.junit.jupiter.api.AfterEach ;
import org.junit.jupiter.api.BeforeAll ;
import org.junit.jupiter.api.BeforeEach ;
import org.junit.jupiter.api.MethodOrderer ;
import org.junit.jupiter.api.TestMethodOrder ;
import static org.junit.jupiter.api.Assertions.* ;

/**
 *
 * @author ryahiaoui
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VertxServerTest {
    
    public VertxServerTest() { }
    
    @BeforeAll
    public static void setUpClass() { }
    
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
    @Order(8)
    public void testGetException() {
        
        System.out.println( "\nHttp Server Vert.x Exception Test"         ) ;
        
        RuntimeException assertThrows = assertThrows( RuntimeException.class, () -> {
              VertxServer vertxServer = new VertxServer( null, 80, null   ) ;
            
        } ) ;
        
        System.out.println( "Http Server Vert.x Exception : "  + 
                             assertThrows.getMessage() + "\n") ;
    }

}

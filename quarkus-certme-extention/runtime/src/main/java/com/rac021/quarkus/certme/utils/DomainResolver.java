
package com.rac021.quarkus.certme.utils ;

import java.net.Socket ;
import java.net.InetAddress ;
import java.net.InetSocketAddress ;

/**
 *
 * @author ryahiaoui
 */

public class DomainResolver    {
       
   public static String resolve() throws Exception   {

       try (final Socket socket = new Socket())      {
            
           socket.connect(new InetSocketAddress("google.com", 80 )) ;
            
           String IP_ADRESS = socket.getLocalAddress()
                                    .toString()
                                    .replace("/", "") ;
            
           InetAddress inetAddr = InetAddress.getByName(IP_ADRESS) ;

           return inetAddr.getCanonicalHostName()  ;
            
       } catch (Exception ex)  {
            throw  ex          ;
       }
   }
    
}

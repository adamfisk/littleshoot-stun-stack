package org.littleshoot.stun.stack;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * Constants for STUN, such as the default STUN port.
 */
public class StunConstants {

    /**
     * The default port for STUN.
     */
    public static final int STUN_PORT = 3478;
    
    public static InetSocketAddress[] SERVERS_ARRAY = {
        //new InetSocketAddress("stun01.sipphone.com", StunConstants.STUN_PORT),
        //new InetSocketAddress("stun.softjoys.com", StunConstants.STUN_PORT),
        
        //new InetSocketAddress("stun.ideasip.com", StunConstants.STUN_PORT),
        
        //new InetSocketAddress("stun.voipbuster.com", StunConstants.STUN_PORT),
        //new InetSocketAddress("stun.voxgratia.org", StunConstants.STUN_PORT),
        //new InetSocketAddress("stun.xten.com", StunConstants.STUN_PORT),
        //new InetSocketAddress("stun.sipgate.net", 10000),
        //new InetSocketAddress("numb.viagenie.ca", StunConstants.STUN_PORT)

        new InetSocketAddress("stun.l.google.com", 19302), 
        new InetSocketAddress("stun.l.google.com", 19302), 
        new InetSocketAddress("alt4.stun.l.google.com", 19302), 
        new InetSocketAddress("alt4.stun.l.google.com", 19302), 
        new InetSocketAddress("alt3.stun.l.google.com", 19302), 
        new InetSocketAddress("alt3.stun.l.google.com", 19302), 
        new InetSocketAddress("alt1.stun.l.google.com", 19302), 
        new InetSocketAddress("alt1.stun.l.google.com", 19302), 
        new InetSocketAddress("alt2.stun.l.google.com", 19302), 
        new InetSocketAddress("alt2.stun.l.google.com", 19302)
    };
    
    public static List<InetSocketAddress> SERVERS = Arrays.asList(SERVERS_ARRAY);
}

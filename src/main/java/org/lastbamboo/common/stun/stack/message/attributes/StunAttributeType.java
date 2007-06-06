package org.lastbamboo.common.stun.stack.message.attributes;

import java.util.Map;


/**
 * STUN attribute types.  We don't use an enum here becuase an enum would
 * require unnecessary conversion from an int read from the network to the
 * corresponding enum value, requiring an extra {@link Map} lookup.  This
 * is also simpler.
 */
public class StunAttributeType 
    {
    
    private StunAttributeType()
        {
        // Should never be constructed.
        }
    
    /**
     * The mapped address attribute.
     */
    public static final int MAPPED_ADDRESS = 0x0001; 
    
    /**
     * The username attribute.
     */
    public static final int USERNAME = 0x0006;
    
    /**
     * The password attribute.
     */
    public static final int PASSWORD = 0x0007;
    
    /**
     * The message integrity attribute.
     */
    public static final int ESSAGE_INTEGRITY = 0x0008;
    
    /**
     * The error code attribute.
     */
    public static final int ERROR_CODE = 0x0009;
    
    /**
     * The unknown attributes attribute.
     */
    public static final int UNKNOWN_ATTRIBUTES = 0x000A;
    
    /**
     * The realm attribute.
     */
    public static final int REALM = 0x0014;
    
    /**
     * The nonce attribute.
     */
    public static final int NONCE = 0x0015;
    
    /**
     * The XOR mapped address attribute.
     */
    public static final int XOR_MAPPED_ADDRESS = 0x0020;
    
    /**
     * The fingerprint attribute.
     */
    public static final int FINGERPRINT = 0x8023;
    
    /**
     * The server attribute.
     */
    public static final int SERVER = 0x8022;
    
    /**
     * The alternate server attribute.
     */
    public static final int ALTERNATE_SERVER = 0x8023;
    
    /**
     * The refresh interval attribute.
     */
    public static final int REFRESH_INTERVAL = 0x8024;

    }

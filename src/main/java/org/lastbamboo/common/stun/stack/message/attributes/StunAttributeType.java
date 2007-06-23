package org.lastbamboo.common.stun.stack.message.attributes;

import java.util.HashMap;
import java.util.Map;


/**
 * STUN attribute types.  We don't use an enum here becuase an enum would
 * require unnecessary conversion from an int read from the network to the
 * corresponding enum value, requiring an extra {@link Map} lookup.  This
 * is also simpler.
 */
public enum StunAttributeType 
    {
    
    /**
     * The mapped address attribute.
     */
    MAPPED_ADDRESS(0x0001), 
    
    /**
     * The username attribute.
     */
    USERNAME(0x0006),
    
    /**
     * The password attribute.
     */
    PASSWORD(0x0007),
    
    /**
     * The message integrity attribute.
     */
    MESSAGE_INTEGRITY(0x0008),
    
    /**
     * The error code attribute.
     */
    ERROR_CODE(0x0009),
    
    /**
     * The unknown attributes attribute.
     */
    UNKNOWN_ATTRIBUTES(0x000A),
    
    /**
     * The realm attribute.
     */
    REALM(0x0014),
    
    /**
     * The nonce attribute.
     */
    NONCE(0x0015),
    
    /**
     * The XOR mapped address attribute.
     */
    XOR_MAPPED_ADDRESS(0x0020),
    
    /**
     * The fingerprint attribute. TODO: This is 0x8023 in 
     * draft-ietf-behave-rfc3489bis-06.txt, but that conflicts with 
     * ALTERNATE SERVER.  Looks like maybe it should be 0x8021?
     */
    FINGERPRINT(0x8021),
    
    /**
     * The server attribute.
     */
    SERVER(0x8022),
    
    /**
     * The alternate server attribute.
     */
    ALTERNATE_SERVER(0x8023),
    
    /**
     * The refresh interval attribute.
     */
    REFRESH_INTERVAL(0x8024),

    /**
     * The relay address allocated for a client on a TURN server.
     */
    RELAY_ADDRESS(0x0016),

    /**
     * Used in Data and Send Indication messages to describe where the data 
     * came from.
     */
    REMOTE_ADDRESS(0x0012),

    /**
     * Used in TURN Data and Send Indication and messages to wrap the actual 
     * data.
     */
    DATA(0x0013),

    /**
     * Attribute for describing the TURN connection status.
     */
    CONNECT_STAT(0x0023);

    private static final Map<Integer, StunAttributeType> s_intsToEnums =
        new HashMap<Integer, StunAttributeType>();
    
    
    static
        {
        for (final StunAttributeType type : values())
            {
            s_intsToEnums.put(new Integer(type.toInt()), type);
            }
        }

    private final int m_type;
        
    private StunAttributeType(final int type)
        {
        m_type = type;
        }

    /**
     * Returns the int value for this type.
     * 
     * @return The int value for this type.
     */
    public int toInt()
        {
        return m_type;
        }

    /**
     * Returns the enum for the corresponding int value, or <code>null</code>
     * if no corresponding value exists.
     * 
     * @param typeInt The type as an int.
     * @return The corresponding enum value or <code>null</code> if no
     * corresponding value exists.
     */
    public static StunAttributeType toType(final int typeInt)
        {
        return s_intsToEnums.get(typeInt);
        }

    }

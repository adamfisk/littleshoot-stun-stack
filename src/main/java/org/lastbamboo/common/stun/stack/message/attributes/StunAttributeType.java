package org.lastbamboo.common.stun.stack.message.attributes;

import org.lastbamboo.common.util.EnumConverter;
import org.lastbamboo.common.util.ReverseEnumMap;


/**
 * STUN attribute types.
 */
public enum StunAttributeType implements EnumConverter<Integer>
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
     * The fingerprint attribute.
     */
    FINGERPRINT(0x8023),
    
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
    REFRESH_INTERVAL(0x8024);
    
    private static ReverseEnumMap<Integer, StunAttributeType> s_map = 
        new ReverseEnumMap<Integer, StunAttributeType>(StunAttributeType.class);
    
    /**
     * Converts from the int type representation to the enum equivalent.
     * 
     * @param type The int type.
     * @return The corresponding enum value.
     */
    public static StunAttributeType convert(final int type)
        {
        return s_map.get(type);
        }
    
    public static boolean hasAttribute(final int type)
        {
        return s_map.contains(type);
        }
    
    private final int m_type;
    
    private StunAttributeType(final int type)
        {
        this.m_type = type;
        }

    public Integer convert()
        {
        return this.m_type;
        }
    }

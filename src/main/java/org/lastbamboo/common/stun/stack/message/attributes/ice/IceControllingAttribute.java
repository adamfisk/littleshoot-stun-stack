package org.lastbamboo.common.stun.stack.message.attributes.ice;

import java.math.BigInteger;

import org.apache.commons.lang.math.RandomUtils;
import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * The ICE-CONTROLLING attribute is present in a Binding Request, and 
 * indicates that the client believes it is currently in the controlling
 * role.  The content of the attribute is a 64 bit unsigned integer in 
 * network byte ordering, which contains a random number used for 
 * tie-breaking of role conflicts.
 */
public final class IceControllingAttribute extends AbstractStunAttribute 
    {

    private final byte[] m_tieBreaker;
    
    /**
     * Creates a new ICE-CONTROLLING attribute.
     */
    public IceControllingAttribute()
        {
        this(new BigInteger(64, RandomUtils.JVM_RANDOM).toByteArray());
        }

    /**
     * Creates a new ICE-CONTROLLING attribute.
     * 
     * @param tieBreaker The tie-breaker for control conflicts.
     */
    public IceControllingAttribute(final byte[] tieBreaker)
        {
        super(StunAttributeType.ICE_CONTROLLED, 8);
        m_tieBreaker = tieBreaker;
        }

    /**
     * Accessor for the random 64 bit positive integer that serves as the 
     * tie-breaker.
     *  
     * @return The 64 bit positive integer for this attribute.
     */
    public byte[] getTieBreaker()
        {
        return m_tieBreaker;
        }
    
    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitIceControlling(this);
        }

    }

package org.lastbamboo.common.stun.stack.message.attributes.ice;

import java.math.BigInteger;

import org.apache.commons.lang.math.RandomUtils;
import org.lastbamboo.common.stun.stack.message.attributes.AbstractStunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;

/**
 * The ICE-CONTROLLED attribute is present in a Binding Request, and
 * indicates that the client believes it is currently in the controlled
 * role.  The content of the attribute is a 64 bit unsigned integer in
 * network byte ordering, which contains a random number used for tie-
 * breaking of role conflicts.
 */
public final class IceControlledAttribute extends AbstractStunAttribute 
    {

    private final byte[] m_tieBreaker;

    /**
     * Creates a new ICE-CONTROLLED attribute.
     */
    public IceControlledAttribute()
        {
        this(new BigInteger(64, RandomUtils.JVM_RANDOM).toByteArray());
        }

    /**
     * Creates a new ICE-CONTROLLED attribute.
     * 
     * @param tieBreaker The tie-breaker for control conflicts.
     */
    public IceControlledAttribute(final byte[] tieBreaker)
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
        visitor.visitIceControlled(this);
        }

    }

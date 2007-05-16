package org.lastbamboo.common.stun.stack.message.attributes;

/**
 * Abstracts out common STUN attribute functionality.
 */
public abstract class AbstractStunAttribute implements StunAttribute
    {

    private final int m_bodyLength;

    /**
     * Creates a new attribute.
     * 
     * @param bodyLength The length of the attribute body.
     */
    public AbstractStunAttribute(final int bodyLength)
        {
        m_bodyLength = bodyLength;
        }

    public int getBodyLength()
        {
        return m_bodyLength;
        }

    }

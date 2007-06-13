package org.lastbamboo.common.stun.stack.message.attributes;

/**
 * Abstracts out common STUN attribute functionality.
 */
public abstract class AbstractStunAttribute implements StunAttribute
    {

    private final int m_bodyLength;
    private final int m_attributeType;

    /**
     * Creates a new attribute.
     * 
     * @param attributeType The type of the attribute.
     * @param bodyLength The length of the attribute body.
     */
    public AbstractStunAttribute(final int attributeType, final int bodyLength)
        {
        m_attributeType = attributeType;
        m_bodyLength = bodyLength;
        }

    public int getBodyLength()
        {
        return m_bodyLength;
        }
    
    public int getTotalLength()
        {
        return m_bodyLength + 4;
        }

    public int getAttributeType()
        {
        return m_attributeType;
        }

    }

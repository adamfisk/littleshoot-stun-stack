package org.lastbamboo.common.stun.stack.message;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Abstracts out common methods and data of STUN messages.
 */
public abstract class AbstractStunMessage implements StunMessage
    {

    private static final Map<StunAttributeType, StunAttribute> EMPTY_MAP =
        Collections.emptyMap();
    
    private final byte[] m_transactionIdBytes;
    private final Map<StunAttributeType, StunAttribute> m_attributes;

    private final int m_totalLength;

    private final int m_bodyLength;

    private final int m_messageType;

    /**
     * Creates a new STUN message.
     * 
     * @param transactionId The transaction ID.
     * @param messageType The type of message.
     */
    public AbstractStunMessage(final byte[] transactionId,
        final int messageType)
        {
        this(transactionId, EMPTY_MAP, messageType);
        }
    
    /**
     * Creates a new STUN message.
     * 
     * @param transactionId The transaction ID.
     * @param attributes The message attributes.
     * @param messageType The type of the message.
     */
    public AbstractStunMessage(final byte[] transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes,
        final int messageType)
        {
        m_transactionIdBytes = transactionId;
        m_attributes = attributes;
        m_bodyLength = calculateBodyLength(attributes);
        m_totalLength = m_bodyLength + 20;
        m_messageType = messageType;
        }

    private int calculateBodyLength(
        final Map<StunAttributeType, StunAttribute> attributesMap)
        {
        final Collection<StunAttribute> attributes = attributesMap.values();
        int length = 0;
        for (final StunAttribute attribute : attributes)
            {
            length += attribute.getBodyLength();
            }
        return length;
        }

    public byte[] getTransactionId()
        {
        return this.m_transactionIdBytes;
        }

    public int getTotalLength()
        {
        return this.m_totalLength;
        }

    public Map<StunAttributeType, StunAttribute> getAttributes()
        {
        return m_attributes;
        }
    
    public int getBodyLength()
        {
        return this.m_bodyLength;
        }
    
    public int getType()
        {
        return this.m_messageType;
        }
    }

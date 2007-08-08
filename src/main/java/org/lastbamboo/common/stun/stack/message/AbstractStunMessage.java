package org.lastbamboo.common.stun.stack.message;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.ClassUtils;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;

/**
 * Abstracts out common methods and data of STUN messages.
 */
public abstract class AbstractStunMessage implements StunMessage
    {

    private static final Map<StunAttributeType, StunAttribute> EMPTY_MAP =
        Collections.emptyMap();
    
    private final UUID m_transactionId;
    private final Map<StunAttributeType, StunAttribute> m_attributes;

    private final int m_totalLength;

    private final int m_bodyLength;

    private final StunMessageType m_messageType;

    /**
     * Creates a new STUN message.
     * 
     * @param transactionId The transaction ID.
     * @param messageType The type of message.
     */
    public AbstractStunMessage(final UUID transactionId,
        final StunMessageType messageType)
        {
        this(transactionId, messageType, EMPTY_MAP);
        }
    
    /**
     * Creates a new STUN message.
     * 
     * @param transactionId The transaction ID.
     * @param attributes The message attributes.
     * @param messageType The type of the message.
     */
    public AbstractStunMessage(final UUID transactionId, 
        final StunMessageType messageType,
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        m_transactionId = transactionId;
        m_attributes = attributes;
        m_bodyLength = calculateBodyLength(attributes);
        m_totalLength = m_bodyLength + 20;
        m_messageType = messageType;
        }

    protected static Map<StunAttributeType, StunAttribute> createAttributes(
        final StunAttribute... attributes)
        {
        final Map<StunAttributeType, StunAttribute> attributesMap = 
            new HashMap<StunAttributeType, StunAttribute>();
        
        for (final StunAttribute attribute : attributes)
            {
            attributesMap.put(attribute.getAttributeType(), 
                attribute);
            }
        return attributesMap;
        }
    
    protected static Map<StunAttributeType, StunAttribute> createRemoteAddress(
        final InetSocketAddress remoteAddress)
        {
        final RemoteAddressAttribute att = 
            new RemoteAddressAttribute(remoteAddress);
        return createAttributes(att);
        }

    private static int calculateBodyLength(
        final Map<StunAttributeType, StunAttribute> attributesMap)
        {
        final Collection<StunAttribute> attributes = attributesMap.values();
        int length = 0;
        for (final StunAttribute attribute : attributes)
            {
            length += attribute.getTotalLength();
            }
        return length;
        }

    public UUID getTransactionId()
        {
        return this.m_transactionId;
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
    
    public StunMessageType getType()
        {
        return this.m_messageType;
        }
    
    public String toString()
        {
        return ClassUtils.getShortClassName(getClass()) + " " + 
            this.m_attributes + " body length: "+this.m_bodyLength;
        }
    }

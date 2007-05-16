package org.lastbamboo.common.stun.stack.message;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactory;

/**
 * Factory for creating new STUN messages.
 */
public class StunMessageFactoryImpl implements StunMessageFactory
    {

    private static final Log LOG = 
        LogFactory.getLog(StunMessageFactoryImpl.class);
    
    private final StunAttributesFactory m_stunAttributesFactory;

    /**
     * Creates a new STUN message factory.
     * 
     * @param stunAttributesFactory The factory for creating STUN message 
     * attributes.
     */
    public StunMessageFactoryImpl(
        final StunAttributesFactory stunAttributesFactory)
        {
        m_stunAttributesFactory = stunAttributesFactory;
        }
    
    public StunMessage createMessage(final ByteBuffer in)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Building message...");
            }
        final int messageType = in.getUnsignedShort();
        final int messageLength = in.getUnsignedShort();
        
        final long magicCookie = in.getUnsignedInt();
        
        if (magicCookie != 0x2112A442)
            {
            LOG.error("Unexpected magic cookie value!!!");
            }
        
        //final ByteBuffer transactionIdBuffer = ByteBuffer.allocate(12);
        //transactionIdBuffer.put(in.get)
        final byte[] transactionIdBytes = new byte[12];
        in.get(transactionIdBytes);
        
        final byte[] body = new byte[messageLength];
        in.get(body);
        final ByteBuffer bodyBuffer = ByteBuffer.wrap(body);
        bodyBuffer.flip();
        final Map<StunAttributeType, StunAttribute> attributes =
            this.m_stunAttributesFactory.createAttributes(bodyBuffer);
        
        return createMessage(messageType, transactionIdBytes, attributes);
        }

    private StunMessage createMessage(final int messageType, 
        final byte[] transactionIdBytes, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        switch (messageType)
            {
            case StunMessageType.BINDING:
                return new BindingRequest(transactionIdBytes);
            default:
                return null;
            }
        }

    }

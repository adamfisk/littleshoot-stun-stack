package org.lastbamboo.common.stun.stack.message;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactory;
import org.lastbamboo.common.util.BitUtils;

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
    
    public BindingRequest createBindingRequest()
        {
        return new BindingRequest();
        }
    
    public StunMessage createMessage(final ByteBuffer in)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Building message...");
            }
        final int messageType = in.getUnsignedShort();
        final int messageLength = in.getUnsignedShort();
 
        // Check for the magic cookie indicating support for the newer
        // STUN spec.
        final long maybeMagicCookie = in.getUnsignedInt();
        byte[] transactionIdBytes = new byte[12];
        in.get(transactionIdBytes);
       
        if (maybeMagicCookie != 0x2112A442)
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("No magic cookie");
                }
            final byte[] magicCookieBytes = 
                BitUtils.toByteArray(maybeMagicCookie);
            transactionIdBytes = 
                ArrayUtils.addAll(magicCookieBytes, transactionIdBytes);
            }
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Reading message body of length: "+messageLength);
            }
        final byte[] body = new byte[messageLength];
        in.get(body);
        final ByteBuffer bodyBuffer = ByteBuffer.wrap(body);
        final Map<Integer, StunAttribute> attributes =
            this.m_stunAttributesFactory.createAttributes(bodyBuffer);
        
        return createMessage(messageType, transactionIdBytes, 
            attributes);
        }

    private StunMessage createMessage(final int messageType, 
        final byte[] transactionIdBytes, 
        final Map<Integer, StunAttribute> attributes)
        {
        switch (messageType)
            {
            case StunMessageType.BINDING_REQUEST:
                return new BindingRequest(transactionIdBytes);
            case StunMessageType.SUCCESSFUL_BINDING_RESPONSE:
                return new BindingResponse(transactionIdBytes, attributes);
            default:
                return null;
            }
        }

    }

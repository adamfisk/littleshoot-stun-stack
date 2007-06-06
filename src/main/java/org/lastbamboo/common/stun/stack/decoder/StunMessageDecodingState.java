package org.lastbamboo.common.stun.stack.decoder;

import java.util.List;
import java.util.Map;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.BindingResponse;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactory;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactoryImpl;
import org.lastbamboo.common.util.mina.DecodingState;
import org.lastbamboo.common.util.mina.DecodingStateMachine;
import org.lastbamboo.common.util.mina.FixedLengthDecodingState;
import org.lastbamboo.common.util.mina.MinaUtils;
import org.lastbamboo.common.util.mina.decode.binary.UnsignedShortDecodingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State machine for decoding STUN messages.
 */
public class StunMessageDecodingState extends DecodingStateMachine 
    {

    private final Logger LOG = 
        LoggerFactory.getLogger(StunMessageDecodingState.class);
    
    
    @Override
    protected DecodingState init() throws Exception
        {
        return new ReadMessageType();
        }

    @Override
    protected void destroy() throws Exception
        {
        }
    
    @Override
    protected DecodingState finishDecode(final List<Object> childProducts, 
        final ProtocolDecoderOutput out) throws Exception
        {
        LOG.debug("Got finish decode");
        return null;
        }
    
    private class ReadMessageType extends UnsignedShortDecodingState
        {
    
        @Override
        protected DecodingState finishDecode(final int decoded, 
            final ProtocolDecoderOutput out) throws Exception
            {
            return new ReadMessageLength(decoded);
            }
        }
    
    private class ReadMessageLength extends UnsignedShortDecodingState
        {

        private final int m_messageType;

        private ReadMessageLength(final int messageType)
            {
            m_messageType = messageType;
            }

        @Override
        protected DecodingState finishDecode(final int decoded, 
            final ProtocolDecoderOutput out) throws Exception
            {
            return new ReadTransactionId(this.m_messageType, decoded);
            }
    
        }
    
    private class ReadTransactionId extends FixedLengthDecodingState
        {

        private final int m_messageType;
        private final int m_messageLength;

        private ReadTransactionId(final int messageType, 
            final int messageLength)
            {
            super(16);
            m_messageType = messageType;
            m_messageLength = messageLength;
            }

        @Override
        protected DecodingState finishDecode(final ByteBuffer readData, 
            final ProtocolDecoderOutput out) throws Exception
            {
            // This copy is not ideal, but passing around ByteBuffers was 
            // causing issues.
            final byte[] transactionId = MinaUtils.toByteArray(readData);
            return new ReadBody(this.m_messageType, this.m_messageLength, 
                transactionId);
            }
        }
    
    private class ReadBody extends FixedLengthDecodingState
        {

        private final int m_type;
        private final byte[] m_transactionId;

        private ReadBody(final int type, final int length, 
            final byte[] transactionId)
            {
            super(length);
            m_type = type;
            m_transactionId = transactionId;
            }

        @Override
        protected DecodingState finishDecode(final ByteBuffer readData, 
            final ProtocolDecoderOutput out) throws Exception
            {
            final StunAttributesFactory factory = 
                new StunAttributesFactoryImpl();
            
            // This decodes the entire body into an attributes map.
            final Map<StunAttributeType, StunAttribute> attributes = 
                factory.createAttributes(readData);
            
            final StunMessage message;
            switch (this.m_type)
                {
                case StunMessageType.BINDING_REQUEST:
                    message = new BindingRequest(m_transactionId);
                    break;
                case StunMessageType.SUCCESSFUL_BINDING_RESPONSE:
                    message = new BindingResponse(m_transactionId, attributes);
                    break;
                default:
                    LOG.warn("Did not understand message type: " + this.m_type);
                    return null;
                }
            
            out.write(message);
            return new ReadMessageType();
            }
    
        }

    }


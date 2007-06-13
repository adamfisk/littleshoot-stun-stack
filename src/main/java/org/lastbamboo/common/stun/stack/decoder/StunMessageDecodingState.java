package org.lastbamboo.common.stun.stack.decoder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.SuccessfulBindingResponse;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactory;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributesFactoryImpl;
import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectionStatusIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;
import org.lastbamboo.common.stun.stack.message.turn.SuccessfulAllocateResponse;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
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
    
    private static final Map<Integer, StunAttribute> EMPTY_ATTRIBUTES =
        Collections.emptyMap();
    
    
    @Override
    protected DecodingState init() throws Exception
        {
        LOG.debug("Initing...");
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
            LOG.debug("Read message length: "+decoded);
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
            LOG.debug("Read transaction id...");
            if (this.m_messageLength > 0)
                {
                return new ReadBody(this.m_messageType, this.m_messageLength, 
                    transactionId);                
                }
            else
                {
                final StunMessage message = 
                    createMessage(this.m_messageType, transactionId, EMPTY_ATTRIBUTES);
                out.write(message);
                return null;
                }
            }
        }
    
    private StunMessage createMessage(final int type,
        final byte[] transactionId, 
        final Map<Integer, StunAttribute> attributes)
        {
        final UUID id = new UUID(transactionId);
        switch (type)
            {
            case StunMessageType.BINDING_REQUEST:
                return new BindingRequest(id);
            case StunMessageType.SUCCESSFUL_BINDING_RESPONSE:
                return new SuccessfulBindingResponse(id, attributes);
            case StunMessageType.ALLOCATE_REQUEST:
                return new AllocateRequest(id);
            case StunMessageType.SUCCESSFUL_ALLOCATE_RESPONSE:
                return new SuccessfulAllocateResponse(id, attributes);
            case StunMessageType.DATA_INDICATION:
                return new DataIndication(id, attributes);
            case StunMessageType.SEND_INDICATION:
                return new SendIndication(id, attributes);
            case StunMessageType.CONNECT_REQUEST:
                return new ConnectRequest(id, attributes);
            case StunMessageType.CONNECTION_STATUS_INDICATION:
                return new ConnectionStatusIndication(id, attributes);
            default:
                LOG.warn("Did not understand message type: " + type);
                return null;
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
            final Map<Integer, StunAttribute> attributes = 
                factory.createAttributes(readData);
            
            final StunMessage message = 
                createMessage(this.m_type, this.m_transactionId, attributes);
            
            /*
            final UUID id = new UUID(m_transactionId);
            final StunMessage message;
            switch (this.m_type)
                {
                case StunMessageType.BINDING_REQUEST:
                    message = new BindingRequest(id);
                    break;
                case StunMessageType.SUCCESSFUL_BINDING_RESPONSE:
                    message = new SuccessfulBindingResponse(id, attributes);
                    break;
                case StunMessageType.ALLOCATE_REQUEST:
                    message = new AllocateRequest(id);
                    break;
                case StunMessageType.SUCCESSFUL_ALLOCATE_RESPONSE:
                    message = new SuccessfulAllocateResponse(id, attributes);
                    break;
                case StunMessageType.DATA_INDICATION:
                    message = new DataIndication(id, attributes);
                    break;
                case StunMessageType.SEND_INDICATION:
                    message = new SendIndication(id, attributes);
                    break;
                default:
                    LOG.warn("Did not understand message type: " + this.m_type);
                    return null;
                }
            */
            
            out.write(message);
            return new ReadMessageType();
            }
    
        }

    }


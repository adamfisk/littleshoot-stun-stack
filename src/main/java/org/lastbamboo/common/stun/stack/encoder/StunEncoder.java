package org.lastbamboo.common.stun.stack.encoder;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeWriter;

/**
 * Encodes bytes into STUN messages.
 */
public class StunEncoder implements ProtocolEncoder
    {

    private static final Log LOG = LogFactory.getLog(StunEncoder.class);
    
    public void dispose(final IoSession session) throws Exception
        {
        // TODO Auto-generated method stub

        }

    public void encode(final IoSession session, final Object message,
        final ProtocolEncoderOutput out) throws Exception
        {
        final StunMessage stunMessage = (StunMessage) message;
        
        final int length = stunMessage.getTotalLength();
        final ByteBuffer buf = ByteBuffer.allocate(length);
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Total message length: "+length);
            }
        buf.putShort((short) (stunMessage.getType() & 0xffff));
        buf.putShort((short) (stunMessage.getBodyLength() & 0xffff));
        
        
        final UUID transactionId = stunMessage.getTransactionId();

        buf.put(transactionId.getRawBytes());
        
        final Map<StunAttributeType, StunAttribute> attributes = 
            stunMessage.getAttributes();
        
        putAttributes(attributes, buf);
        
        buf.flip();
        out.write(buf);
        }

    private void putAttributes(
        final Map<StunAttributeType, StunAttribute> attributesMap, 
        final ByteBuffer buf)
        {
        final StunAttributeVisitor visitor = new StunAttributeWriter(buf);
        final Collection<StunAttribute> attributes = attributesMap.values();
        for (final StunAttribute attribute : attributes)
            {
            attribute.accept(visitor);
            }
        }

    }

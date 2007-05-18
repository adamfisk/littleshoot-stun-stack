package org.lastbamboo.common.stun.stack.message;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
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
    
    private static final int MAGIC_COOKIE = 0x2112A442;
    
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
        
        
        final byte[] transactionId = stunMessage.getTransactionId();
        
        // We only include the magic cookie if it's a new-style STUN request.
        // Otherwise, we use the old 16 byte transaction ID for backwards
        // compatibility.
        if (transactionId.length == 12)
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Writing magic cookie");
                }
            buf.putInt(MAGIC_COOKIE);
            }
        buf.put(transactionId);
        
        final Map<StunAttributeType, StunAttribute> attributes = 
            stunMessage.getAttributes();
        
        putAttributes(attributes, buf);
        
        //if (buf.hasRemaining())
          //  {
            //LOG.error("Should have completely filled the buffer");
            //}

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

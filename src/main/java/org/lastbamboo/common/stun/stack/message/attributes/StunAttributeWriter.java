package org.lastbamboo.common.stun.stack.message.attributes;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;

/**
 * Write STUN attributes.
 */
public class StunAttributeWriter implements StunAttributeVisitor
    {

    private static final Log LOG = LogFactory.getLog(StunAttributeWriter.class);
    
    private final ByteBuffer m_buf;

    /**
     * Creates a new class for writing STUN attributes.
     * 
     * @param buf The attribute buffer.
     */
    public StunAttributeWriter(final ByteBuffer buf)
        {
        m_buf = buf;
        }

    public void visitMappedAddress(final MappedAddress address)
        {
        final int type = 
            StunAttributeType.MAPPED_ADDRESS.convert().intValue();
        final int length = address.getBodyLength();
        m_buf.putShort((short) (type & 0xffff));
        m_buf.putShort((short) (length & 0xffff));

        // Now put the attribute body.
        
        // The first byte is ignored in MAPPED_ADDRESS.
        m_buf.put((byte) 0x00);
        
        final int family = address.getAddressFamily();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing family: "+family);
            }
        final InetSocketAddress socketAddress = address.getInetSocketAddress();
        final int port = socketAddress.getPort();
        final InetAddress ia = socketAddress.getAddress();
        final byte[] addressBytes = ia.getAddress();
        m_buf.put((byte) family);
        m_buf.putShort((short) (port & 0xffff));
        m_buf.put(addressBytes);
        }

    }

package org.lastbamboo.common.stun.server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddress;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Write STUN attributes.
 */
public class StunAttributeWriter implements StunAttributeVisitor
    {

    private final ByteBuffer m_buf;

    public StunAttributeWriter(final ByteBuffer buf)
        {
        m_buf = buf;
        }

    public void visitMappedAddress(final MappedAddress address)
        {
        final short type = StunAttributeType.MAPPED_ADDRESS.convert();
        final int length = address.getBodyLength();
        m_buf.putShort((short) (type & 0xffff));
        m_buf.putShort((short) (length & 0xffff));

        // Now put the attribute body.
        
        // The first byte is ignored in MAPPED_ADDRESS.
        m_buf.put((byte) 0x00);
        
        final int family = address.getAddressFamily();
        final InetSocketAddress socketAddress = address.getInetSocketAddress();
        final int port = socketAddress.getPort();
        final InetAddress ia = socketAddress.getAddress();
        final byte[] addressBytes = ia.getAddress();
        m_buf.put((byte) family);
        m_buf.putShort((short) (port & 0xffff));
        m_buf.put(addressBytes);
        }

    }

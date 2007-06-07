package org.lastbamboo.common.stun.stack.encoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddress;
import org.lastbamboo.common.stun.stack.message.attributes.StunAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.turn.DataAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RelayAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;

/**
 * Writes STUN attributes.
 */
public class StunAttributeEncoder implements StunAttributeVisitor
    {

    private static final Log LOG = LogFactory.getLog(StunAttributeEncoder.class);
    
    private final ByteBuffer m_buf;

    /**
     * Creates a new class for writing STUN attributes.
     * 
     * @param buf The attribute buffer.
     */
    public StunAttributeEncoder(final ByteBuffer buf)
        {
        m_buf = buf;
        }
    
    public void visitData(final DataAttribute data)
        {
        writeHeader(m_buf, StunAttributeType.DATA, data);
        final byte[] dataBytes = data.getData();
        m_buf.put(dataBytes);
        }

    public void visitRelayAddress(final RelayAddressAttribute address)
        {
        visitAddressAttribute(StunAttributeType.RELAY_ADDRESS, address);
        }
    
    public void visitMappedAddress(final MappedAddress address)
        {
        visitAddressAttribute(StunAttributeType.MAPPED_ADDRESS, address);
        }
    
    public void visitRemoteAddress(final RemoteAddressAttribute address)
        {
        visitAddressAttribute(StunAttributeType.REMOTE_ADDRESS, address);
        }
    
    private void visitAddressAttribute(final int type, 
        final StunAddressAttribute address)
        {
        writeHeader(m_buf, type, address);

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

    private static void writeHeader(final ByteBuffer buf, final int type, 
        final StunAttribute address)
        {
        final int length = address.getBodyLength();
        buf.putShort((short) (type & 0xffff));
        buf.putShort((short) (length & 0xffff));
        }
    }

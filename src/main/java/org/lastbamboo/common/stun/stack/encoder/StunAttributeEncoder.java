package org.lastbamboo.common.stun.stack.encoder;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatus;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatusAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.DataAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RelayAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;
import org.lastbamboo.common.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes STUN attributes.
 */
public class StunAttributeEncoder implements StunAttributeVisitor
    {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
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
        writeHeader(data);
        final byte[] dataBytes = data.getData();
        m_buf.put(dataBytes);
        }
    
    public void visitConnectionStatus(final ConnectionStatusAttribute attribute)
        {
        LOG.debug("Writing connection status attribute: {}", attribute);
        writeHeader(attribute);
        final ConnectionStatus status = attribute.getConnectionStatus();
        MinaUtils.putUnsignedInt(m_buf, status.toLong());
        }

    public void visitRelayAddress(final RelayAddressAttribute address)
        {
        visitAddressAttribute(address);
        }
    
    public void visitMappedAddress(final MappedAddressAttribute address)
        {
        visitAddressAttribute(address);
        }
    
    public void visitRemoteAddress(final RemoteAddressAttribute address)
        {
        visitAddressAttribute(address);
        }
    
    private void visitAddressAttribute(final StunAddressAttribute address)
        {
        writeHeader(address);

        // Now put the attribute body.
        
        // The first byte is ignored in address attributes.
        MinaUtils.putUnsignedByte(m_buf, 0x00);
        
        final int family = address.getAddressFamily();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing family: "+family);
            }
        final InetSocketAddress socketAddress = address.getInetSocketAddress();
        final int port = socketAddress.getPort();
        final InetAddress ia = socketAddress.getAddress();
        final byte[] addressBytes = ia.getAddress();
        MinaUtils.putUnsignedByte(m_buf, family);
        MinaUtils.putUnsignedShort(m_buf, port);
        m_buf.put(addressBytes);
        }

    private void writeHeader(final StunAttribute sa)
        {
        MinaUtils.putUnsignedShort(m_buf, sa.getAttributeType().toInt());
        MinaUtils.putUnsignedShort(m_buf, sa.getBodyLength());
        }
    }

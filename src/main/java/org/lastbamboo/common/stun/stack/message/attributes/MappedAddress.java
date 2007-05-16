package org.lastbamboo.common.stun.stack.message.attributes;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.stun.server.StunAttributeVisitor;

/**
 * Mapped Address attribute.
 */
public class MappedAddress extends AbstractStunAttribute
    {

    private static final Log LOG = LogFactory.getLog(MappedAddress.class);

    private final InetSocketAddress m_inetSocketAddress;

    private final int m_addressFamily;

    /**
     * Creates a new mapped address attribute.
     * 
     * @param socketAddress The IP and port to put in the attribute.
     * @param bodyLength The length of the attribute body.
     */
    public MappedAddress(final InetSocketAddress socketAddress, 
        final int bodyLength)
        {
        super(bodyLength);
        this.m_inetSocketAddress = socketAddress;
        
        final InetAddress address = socketAddress.getAddress();
        
        if (address instanceof Inet4Address)
            {
            this.m_addressFamily = 0x01;
            }
        else
            {
            this.m_addressFamily = 0x02;
            }
        }

    public InetSocketAddress getInetSocketAddress()
        {
        return m_inetSocketAddress;
        }

    public int getAddressFamily()
        {
        return this.m_addressFamily;
        }
    
    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitMappedAddress(this);
        }

    }

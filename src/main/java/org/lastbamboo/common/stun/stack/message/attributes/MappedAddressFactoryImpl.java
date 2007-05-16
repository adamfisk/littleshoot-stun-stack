package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;

/**
 * Factory for creating mapped address attributes.
 */
public class MappedAddressFactoryImpl implements MappedAddressFactory
    {
    
    private static final Log LOG = 
        LogFactory.getLog(MappedAddressFactoryImpl.class);
    
    private static final short IPv4 = 0x01;
    private static final short IPv6 = 0x02;

    public StunAttribute createAttribute(final ByteBuffer buffer) 
        throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating mapped address attribute");
            }
        final short family = buffer.getUnsigned();
        final short port = buffer.getUnsigned();
        
        final InetAddress inetAddress; 
        
        final int length;
        
        if (family == IPv4)
            {
            length = 4;
            }
        else if (family == IPv6)
            {
            length = 16;
            }
        else
            {
            LOG.error("Could not understand address family: "+family);
            throw new IOException("Could not understand address family: " +
                family);
            }

        final byte[] addressBytes = new byte[length];
        buffer.get(addressBytes);
        inetAddress = InetAddress.getByAddress(addressBytes);
        final InetSocketAddress socketAddress = 
            new InetSocketAddress(inetAddress, port);
        
        return new MappedAddress(socketAddress, getBodyLength(socketAddress));
        }
    
    public StunAttribute createMappedAddress(final InetSocketAddress address)
        {
        return new MappedAddress(address, getBodyLength(address));
        }
    
    private static int getBodyLength(final InetSocketAddress address)
        {
        final InetAddress ia = address.getAddress();
        if (ia instanceof Inet4Address)
            {
            // 2 byte family + 2 byte port + 4 byte address
            return 8;
            }
        // 2 byte family + 2 byte port + 16 byte address
        return 20;
        }

    }

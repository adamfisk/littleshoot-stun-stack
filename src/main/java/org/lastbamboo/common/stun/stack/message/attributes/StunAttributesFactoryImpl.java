package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatus;
import org.lastbamboo.common.stun.stack.message.attributes.turn.ConnectionStatusAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.DataAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RelayAddressAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.turn.RemoteAddressAttribute;
import org.lastbamboo.common.util.mina.MinaUtils;

/**
 * Class for creating STUN attributes.
 */
public class StunAttributesFactoryImpl implements StunAttributesFactory
    {

    private static final Log LOG = 
        LogFactory.getLog(StunAttributesFactoryImpl.class);
    
    public Map<Integer, StunAttribute> createAttributes(final ByteBuffer body)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating attributes...");
            }
        final Map<Integer, StunAttribute> attributes =
            new ConcurrentHashMap<Integer, StunAttribute>();
        while (body.hasRemaining())
            {
            addAttribute(attributes, body);
            }
        return attributes;
        }

    private void addAttribute(final Map<Integer, StunAttribute> attributes, 
        final ByteBuffer buf)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Adding attribute");
            }
        final int type = buf.getUnsignedShort();
        final int length = buf.getUnsignedShort();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Type is: "+type);
            LOG.debug("Length is: "+length);
            }
        
        final byte[] body = new byte[length];
        buf.get(body);
        
        try
            {
            final StunAttribute attribute = createAttribute(type, body);
            if (attribute != null)
                {
                attributes.put(new Integer(type), attribute);
                }
            }
        catch (final IOException e)
            {
            LOG.warn("Could not process attribute", e);
            }

        }

    private StunAttribute createAttribute(final int type, 
        final byte[] bodyBytes) throws IOException
        {
        final ByteBuffer body = ByteBuffer.wrap(bodyBytes);
        switch (type)
            {
            case StunAttributeType.MAPPED_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new MappedAddress(address);
                }
            case StunAttributeType.SERVER:
                {
                final String serverText = MinaUtils.toAsciiString(body);
                return new StunServerAttribute(bodyBytes.length, serverText);
                }
                
            case StunAttributeType.RELAY_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new RelayAddressAttribute(address);
                }
            case StunAttributeType.REMOTE_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new RemoteAddressAttribute(address);
                }
            case StunAttributeType.DATA:
                {
                return new DataAttribute(bodyBytes);
                }
            case StunAttributeType.CONNECT_STAT:
                {
                final long statusInt = body.getUnsignedInt();
                final ConnectionStatus status = 
                    ConnectionStatus.valueOf(statusInt);
                return new ConnectionStatusAttribute(status);
                }
            default:
                {
                LOG.warn("Unrecognized attribute: "+type);
                return null;
                }
            }
        }

    }

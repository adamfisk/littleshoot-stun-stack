package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControllingAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IcePriorityAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceUseCandidateAttribute;
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
    
    public Map<StunAttributeType, StunAttribute> createAttributes(
        final ByteBuffer body)
        {
        final Map<StunAttributeType, StunAttribute> attributes =
            new ConcurrentHashMap<StunAttributeType, StunAttribute>();
        while (body.hasRemaining())
            {
            addAttribute(attributes, body);
            }
        return attributes;
        }

    private void addAttribute(
        final Map<StunAttributeType, StunAttribute> attributes, 
        final ByteBuffer buf)
        {
        final int typeInt = buf.getUnsignedShort();
        final StunAttributeType type = StunAttributeType.toType(typeInt);
        if (type == null)
            {
            // This could just be a weird attribute from a STUN server we 
            // don't understand, for example.  Then again, it could be a bug!
            LOG.debug("Could not get type for int: "+typeInt);
            }
        final int length = buf.getUnsignedShort();
        
        if (buf.remaining() < length)
            {
            LOG.error("Error reading attribute.\nExpected length:  "+length+
                "\nActual remaining: "+buf.remaining());
            }
        final byte[] body = new byte[length];
        buf.get(body);
        
        // Handle types we don't recognize, such as types returned from
        // "foreign" STUN servers.
        if (type == null)
            {
            LOG.debug("Did not recognize type: "+typeInt);
            return;
            }
        try
            {
            final StunAttribute attribute = createAttribute(type, body);
            if (attribute != null)
                {
                attributes.put(type, attribute);
                }
            }
        catch (final IOException e)
            {
            LOG.warn("Could not process attribute", e);
            }

        }
    
    private StunAttribute createAttribute(final StunAttributeType type, 
        final byte[] bodyBytes) throws IOException
        {
        final ByteBuffer body = ByteBuffer.wrap(bodyBytes);
        switch (type)
            {
            case MAPPED_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new MappedAddressAttribute(address);
                }
            case SERVER:
                {
                final String serverText = MinaUtils.toAsciiString(body);
                return new StunServerAttribute(bodyBytes.length, serverText);
                }
                
            case RELAY_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new RelayAddressAttribute(address);
                }
            case REMOTE_ADDRESS:
                {
                final InetSocketAddress address = 
                    AddressAttributeReader.readAddress(body);
                return new RemoteAddressAttribute(address);
                }
            case DATA:
                {
                return new DataAttribute(bodyBytes);
                }
            case CONNECT_STAT:
                {
                final long statusInt = body.getUnsignedInt();
                final ConnectionStatus status = 
                    ConnectionStatus.valueOf(statusInt);
                return new ConnectionStatusAttribute(status);
                }
                
            case PRIORITY:
                {
                final long priority = body.getUnsignedInt();
                return new IcePriorityAttribute(priority);
                }
                
            case USE_CANDIDATE:
                {
                return new IceUseCandidateAttribute();
                }
                
            case ICE_CONTROLLED:
                {
                final byte[] tieBreaker = new byte[8];
                body.get(tieBreaker);
                return new IceControlledAttribute(tieBreaker);
                }
                
            case ICE_CONTROLLING:
                {
                final byte[] tieBreaker = new byte[8];
                body.get(tieBreaker);
                return new IceControllingAttribute(tieBreaker);
                }
                
            default:
                {
                LOG.error("Unrecognized attribute: "+type);
                return null;
                }
            }
        }

    }

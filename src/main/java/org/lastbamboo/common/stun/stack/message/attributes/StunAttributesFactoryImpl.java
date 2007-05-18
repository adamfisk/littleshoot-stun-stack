package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;

/**
 * Class for creating STUN attributes.
 */
public class StunAttributesFactoryImpl implements StunAttributesFactory
    {

    private static final Log LOG = 
        LogFactory.getLog(StunAttributesFactoryImpl.class);
    
    private final StunAttributeFactory m_mappedAddressFactory =
        new MappedAddressFactoryImpl();
    
    public Map<StunAttributeType, StunAttribute> createAttributes(
        final ByteBuffer body)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating attributes...");
            }
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
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Adding attribute");
            }
        //final short type = buf.getUnsigned();
        //final short length = buf.getUnsigned();

        final int type = buf.getUnsignedShort();
        final int length = buf.getUnsignedShort();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Type is: "+type);
            LOG.debug("Length is: "+length);
            }
        
        final byte[] body = new byte[length];
        buf.get(body);
        
        final ByteBuffer bodyBuf = ByteBuffer.wrap(body);
        //bodyBuf.flip();
        
        final StunAttributeType enumType = StunAttributeType.convert((short) type);
        try
            {
            final StunAttribute attribute = createAttribute(enumType, bodyBuf);
            if (attribute != null)
                {
                attributes.put(enumType, attribute);
                }
            }
        catch (final IOException e)
            {
            LOG.warn("Could not process attribute", e);
            }

        }

    private StunAttribute createAttribute(final StunAttributeType type, 
        final ByteBuffer body) throws IOException
        {
        switch (type)
            {
            case MAPPED_ADDRESS:
                {
                return this.m_mappedAddressFactory.createAttribute(body);
                //return new MappedAddress(body);
                }
            default:
                {
                LOG.warn("Unrecognized attribute: "+type);
                return null;
                }
            }
        }

    }

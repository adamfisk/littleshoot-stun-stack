package org.lastbamboo.common.stun.stack.message;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddress;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddressFactoryImpl;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Binding response message.
 */
public class BindingResponse extends AbstractStunMessage
    implements VisitableStunMessage
    {

    private static final Log LOG = LogFactory.getLog(BindingResponse.class);
    private final InetSocketAddress m_mappedAddress;

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId The ID of the transaction.
     * @param address The mapped address.
     */
    public BindingResponse(final byte[] transactionId, 
        final InetSocketAddress address)
        {
        super(transactionId, createAttributes(address), 
            StunMessageType.SUCCESSFUL_BINDING_RESPONSE);
        m_mappedAddress = address;
        }

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId The transaction ID of the response.
     * @param attributes The response attributes.
     */
    public BindingResponse(final byte[] transactionId, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(transactionId, attributes, 
            StunMessageType.SUCCESSFUL_BINDING_RESPONSE);
        m_mappedAddress = getAddress(attributes);
        }

    private InetSocketAddress getAddress(
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        final MappedAddress mappedAddress = 
            (MappedAddress) attributes.get(StunAttributeType.MAPPED_ADDRESS);
        if (mappedAddress == null)
            {
            LOG.error("No mapped address in: "+attributes.values());
            return null;
            }
        return mappedAddress.getInetSocketAddress();
        }

    private static Map<StunAttributeType, StunAttribute> createAttributes(
        final InetSocketAddress address)
        {
        final Map<StunAttributeType, StunAttribute> attributes =
            new HashMap<StunAttributeType, StunAttribute>();
        
        final MappedAddressFactoryImpl factory = new MappedAddressFactoryImpl();
        final StunAttribute attribute = factory.createMappedAddress(address);
        attributes.put(StunAttributeType.MAPPED_ADDRESS, attribute);
        
        return attributes;
        }
    
    public InetSocketAddress getMappedAddress()
        {
        return m_mappedAddress;
        }
    
    public void accept(final StunMessageVisitor visitor)
        {
        visitor.visitBindingResponse(this);
        }
    
    public String toString()
        {
        return ClassUtils.getShortClassName(getClass()) + 
            " with MAPPED ADDRESS: " + getMappedAddress(); 
        }

    }

package org.lastbamboo.common.stun.stack.message;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddress;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;
import org.lastbamboo.common.stun.stack.message.attributes.StunMappedAddressAttributeFactory;

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
        super(new UUID(transactionId), createAttributes(address), 
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
        final Map<Integer, StunAttribute> attributes)
        {
        super(new UUID(transactionId), attributes, 
            StunMessageType.SUCCESSFUL_BINDING_RESPONSE);
        m_mappedAddress = getAddress(attributes);
        }

    private InetSocketAddress getAddress(
        final Map<Integer, StunAttribute> attributes)
        {
        final MappedAddress mappedAddress = 
            (MappedAddress) attributes.get(
                new Integer(StunAttributeType.MAPPED_ADDRESS));
        if (mappedAddress == null)
            {
            LOG.error("No mapped address in: "+attributes.values());
            return null;
            }
        return mappedAddress.getInetSocketAddress();
        }

    private static Map<Integer, StunAttribute> createAttributes(
        final InetSocketAddress address)
        {
        final Map<Integer, StunAttribute> attributes =
            new HashMap<Integer, StunAttribute>();
        
        final StunMappedAddressAttributeFactory factory = 
            new StunMappedAddressAttributeFactory();
        final StunAttribute attribute = factory.createMappedAddress(address);
        attributes.put(new Integer(StunAttributeType.MAPPED_ADDRESS), 
            attribute);
        
        return attributes;
        }
    
    /**
     * Accessor for the mapped address received in the binding response.
     * 
     * @return The client's mapped address.
     */
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

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

/**
 * Binding response message.
 */
public class SuccessfulBindingResponse extends AbstractStunMessage
    {

    private static final Log LOG = 
        LogFactory.getLog(SuccessfulBindingResponse.class);
    private final InetSocketAddress m_mappedAddress;

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId The ID of the transaction.
     * @param address The mapped address.
     */
    public SuccessfulBindingResponse(final byte[] transactionId, 
        final InetSocketAddress address)
        {
        super(new UUID(transactionId), 
            StunMessageType.SUCCESSFUL_BINDING_RESPONSE, 
            createAttributes(address));
        m_mappedAddress = address;
        }

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId The transaction ID of the response.
     * @param attributes The response attributes.
     */
    public SuccessfulBindingResponse(final UUID transactionId, 
        final Map<Integer, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.SUCCESSFUL_BINDING_RESPONSE, 
            attributes);
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
        
        final StunAttribute attribute = new MappedAddress(address);
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
        visitor.visitSuccessfulBindingResponse(this);
        }
    
    public String toString()
        {
        return ClassUtils.getShortClassName(getClass()) + 
            " with MAPPED ADDRESS: " + getMappedAddress(); 
        }

    }

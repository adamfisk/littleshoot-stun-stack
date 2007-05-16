package org.lastbamboo.common.stun.stack.message;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.lastbamboo.common.stun.stack.message.attributes.MappedAddressFactoryImpl;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Binding response message.
 */
public class BindingResponse extends AbstractStunMessage
    implements VisitableStunMessage
    {

    private final InetSocketAddress m_address;

    /**
     * Creates a new binding response message.
     * 
     * @param transactionId The ID of the transaction.
     * @param address The mapped address.
     */
    public BindingResponse(final byte[] transactionId, 
        final InetSocketAddress address)
        {
        super(transactionId, createAttributes(address), 0x0101);
        m_address = address;
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
    
    public void accept(final StunMessageVisitor visitor)
        {
        visitor.visitBindingResponse(this);
        }

    }

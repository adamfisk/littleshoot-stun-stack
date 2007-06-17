package org.lastbamboo.common.stun.stack.message.turn;

import java.net.InetSocketAddress;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.AbstractStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.MappedAddress;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Creates a new response to an allocate request.  This includes the mapped
 * address the server has allocated to proxy data to the TURN client.
 */
public final class SuccessfulAllocateResponse extends AbstractStunMessage
    {
    
    private final InetSocketAddress m_mappedAddress;

    /**
     * Creates a new successful response to an allocate request.
     * 
     * @param transactionId The ID of the transaction, matching the ID of the
     * request.
     * @param attributes The message attributes.
     */
    public SuccessfulAllocateResponse(final UUID transactionId, 
        final Map<Integer, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.SUCCESSFUL_ALLOCATE_RESPONSE,
            attributes);
        final MappedAddress ma = (MappedAddress) attributes.get(
            new Integer(StunAttributeType.MAPPED_ADDRESS));
        m_mappedAddress = ma.getInetSocketAddress(); 
        if (m_mappedAddress == null)
            {
            throw new NullPointerException("Null mapped address");
            }
        }

    /**
     * Creates a new successful response to an allocate request.
     * 
     * @param transactionId The ID of the transaction, matching the ID of the
     * request.
     * @param address The MAPPED ADDRESS.
     */
    public SuccessfulAllocateResponse(final UUID transactionId, 
        final InetSocketAddress address)
        {
        super(transactionId, StunMessageType.SUCCESSFUL_ALLOCATE_RESPONSE,
            createAttributes(address));
        this.m_mappedAddress = address;
        if (m_mappedAddress == null)
            {
            throw new NullPointerException("Null mapped address");
            }
        }

    private static Map<Integer, StunAttribute> createAttributes(
        final InetSocketAddress address)
        {
        final MappedAddress ma = new MappedAddress(address);
        return createAttributes(ma);
        }

    /**
     * Accessor for the mapped address in the response.
     * 
     * @return The mapped address.
     */
    public InetSocketAddress getMappedAddress()
        {
        return m_mappedAddress;
        }
    
    public void accept(final StunMessageVisitor visitor)
        {
        visitor.visitSuccessfulAllocateResponse(this);
        }

    }

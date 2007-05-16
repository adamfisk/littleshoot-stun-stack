package org.lastbamboo.common.stun.stack.message.attributes;

import java.net.InetSocketAddress;

/**
 * Factory for creating mapped address attributes.
 */
public interface MappedAddressFactory extends StunAttributeFactory
    {

    /**
     * Creates a new mapped address for the specified address.
     * 
     * @param address The address to put in the attribute.
     * @return The new mapped address attribute.
     */
    StunAttribute createMappedAddress(final InetSocketAddress address);
    
    }

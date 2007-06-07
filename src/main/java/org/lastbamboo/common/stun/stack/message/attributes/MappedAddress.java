package org.lastbamboo.common.stun.stack.message.attributes;

import java.net.InetSocketAddress;

/**
 * Mapped Address attribute.
 */
public class MappedAddress extends AbstractStunAddressAttribute
    {

    /**
     * Creates a new mapped address attribute.
     * 
     * @param socketAddress The IP and port to put in the attribute.
     */
    public MappedAddress(final InetSocketAddress socketAddress)
        {
        super(socketAddress);
        }

    public void accept(final StunAttributeVisitor visitor)
        {
        visitor.visitMappedAddress(this);
        }

    }

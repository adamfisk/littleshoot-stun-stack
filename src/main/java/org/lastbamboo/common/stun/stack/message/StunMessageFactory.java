package org.lastbamboo.common.stun.stack.message;

import org.apache.mina.common.ByteBuffer;

/**
 * Factory API for creating STUN messages.
 */
public interface StunMessageFactory
    {

    /**
     * Creates a STUN message from the {@link ByteBuffer}.
     * 
     * @param in The {@link ByteBuffer} received from the network.
     * @return The STUN message.
     */
    StunMessage createMessage(ByteBuffer in);

    /**
     * Creates a new binding request.
     * 
     * @return The new binding request.
     */
    StunMessage createBindingRequest();

    }

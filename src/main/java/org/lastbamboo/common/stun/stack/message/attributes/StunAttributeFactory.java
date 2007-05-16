package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;

import org.apache.mina.common.ByteBuffer;

/**
 * Factory interface for creating individual STUN attributes.
 */
public interface StunAttributeFactory
    {

    /**
     * Creates a singe STUN attribute.
     * 
     * @param buffer The buffer containing attribute data.
     * @return The new STUN attribute.
     * @throws IOException If the buffer does not contain the expected data.
     */
    StunAttribute createAttribute(ByteBuffer buffer) throws IOException;

    }

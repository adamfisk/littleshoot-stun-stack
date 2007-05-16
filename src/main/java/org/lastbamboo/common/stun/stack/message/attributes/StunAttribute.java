package org.lastbamboo.common.stun.stack.message.attributes;

import org.lastbamboo.common.stun.server.StunAttributeVisitor;


/**
 * Interface for the various STUN attributes.
 */
public interface StunAttribute
    {

    /**
     * Returns the length of the attribute.
     * 
     * @return The length of the attribute;
     */
    int getBodyLength();

    /**
     * Allows attributes to accept visitors.
     * 
     * @param visitor The visitor to accept.
     */
    void accept(StunAttributeVisitor visitor);

    }

package org.lastbamboo.common.stun.stack.message.attributes;


/**
 * Visits STUN attributes.
 */
public interface StunAttributeVisitor
    {

    /**
     * Visits the MAPPED ADDRESS attribute.
     * 
     * @param address The MAPPED ADDRESS.
     */
    void visitMappedAddress(MappedAddress address);

    }

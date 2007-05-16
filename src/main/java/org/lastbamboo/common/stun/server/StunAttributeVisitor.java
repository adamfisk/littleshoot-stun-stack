package org.lastbamboo.common.stun.server;

import org.lastbamboo.common.stun.stack.message.attributes.MappedAddress;

/**
 * Visits STUN attributes.
 */
public interface StunAttributeVisitor
    {

    void visitMappedAddress(MappedAddress address);

    }

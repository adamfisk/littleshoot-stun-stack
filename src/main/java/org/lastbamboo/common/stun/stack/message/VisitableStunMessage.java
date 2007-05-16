package org.lastbamboo.common.stun.stack.message;

/**
 * Interface for STUN messages that are visitable by visitors.
 */
public interface VisitableStunMessage
    {

    /**
     * Accepts the specified visitor class.
     * 
     * @param visitor The visitor to accept.
     */
    void accept(StunMessageVisitor visitor);
    
    }

package org.lastbamboo.common.stun.stack.message;


/**
 * Visitor for various STUN messages.
 */
public interface StunMessageVisitor
    {

    /**
     * Visits a binding message.
     * 
     * @param binding The binding message.
     */
    void visitBindingRequest(BindingRequest binding);

    /**
     * Visits a binding response.
     * 
     * @param response The binding response.
     */
    void visitBindingResponse(BindingResponse response);

    }

package org.lastbamboo.common.stun.stack.message;

import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.SuccessfulAllocateResponse;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;

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
    void visitSuccessfulBindingResponse(SuccessfulBindingResponse response);

    /**
     * Visits the TURN usage allocate request message.
     * 
     * @param request The TURN usage allocate request.
     */
    void visitAllocateRequest(AllocateRequest request);

    void visitSuccessfulAllocateResponse(SuccessfulAllocateResponse response);

    void visitDataIndication(DataIndication data);

    void visitSendIndication(SendIndication request);

    }

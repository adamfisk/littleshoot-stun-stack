package org.lastbamboo.common.stun.stack.message;

import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectionStatusIndication;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;
import org.lastbamboo.common.stun.stack.message.turn.SuccessfulAllocateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter class for convenient creation of message visitor subclasses.  This
 * will log errors whenever something is visited that's not overidden.  If
 * a subclass should handle a message, it therefore must override the 
 * appropriate visit method.
 */
public abstract class StunMessageVisitorAdapter implements StunMessageVisitor
    {

    private Logger LOG = LoggerFactory.getLogger(
        StunMessageVisitorAdapter.class);
    
    public void visitAllocateRequest(final AllocateRequest request)
        {
        LOG.error("Visiting unexpected message: {}", request);
        }

    public void visitBindingRequest(final BindingRequest request)
        {
        LOG.error("Visiting unexpected message: {}", request);
        }

    public void visitConnectRequest(final ConnectRequest request)
        {
        LOG.error("Visiting unexpected message: {}", request);
        }

    public void visitConnectionStatusIndication(
        final ConnectionStatusIndication indication)
        {
        LOG.error("Visiting unexpected message: {}", indication);
        }

    public void visitDataIndication(final DataIndication data)
        {
        LOG.error("Visiting unexpected message: {}", data);
        }

    public void visitSendIndication(final SendIndication request)
        {
        LOG.error("Visiting unexpected message: {}", request);
        }

    public void visitSuccessfulAllocateResponse(
        final SuccessfulAllocateResponse response)
        {
        LOG.error("Visiting unexpected message: {}", response);
        }

    public void visitSuccessfulBindingResponse(
        final SuccessfulBindingResponse response)
        {
        LOG.error("Visiting unexpected message: {}", response);
        }

    }

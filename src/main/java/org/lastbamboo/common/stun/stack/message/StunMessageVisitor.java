package org.lastbamboo.common.stun.stack.message;

import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectionStatusIndication;
import org.lastbamboo.common.stun.stack.message.turn.SuccessfulAllocateResponse;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;

/**
 * Visitor for various STUN messages.
 */
public interface StunMessageVisitor<T>
    {

    /**
     * Visits a binding message.
     * 
     * @param binding The binding message.
     */
    T visitBindingRequest(BindingRequest binding);

    /**
     * Visits a binding response.
     * 
     * @param response The binding response.
     */
    T visitSuccessfulBindingResponse(SuccessfulBindingResponse response);

    /**
     * Visits the TURN usage allocate request message.
     * 
     * @param request The TURN usage allocate request.
     */
    T visitAllocateRequest(AllocateRequest request);

    T visitSuccessfulAllocateResponse(SuccessfulAllocateResponse response);

    T visitDataIndication(DataIndication data);

    T visitSendIndication(SendIndication request);
    
    /**
     * Visits a connection request from a client.  Only STUN servers will
     * respond to this request.  This request indicates the client wishes
     * to allow connections from the host specified in the REMOTE ADDRESS
     * attribute.
     * 
     * @param request The connect request.
     */
    T visitConnectRequest(ConnectRequest request);
    
    /**
     * Visits a connection status indication message informing clients of the
     * connection status of remote hosts. The connection status is sent in the
     * CONNECT STAT attribute.
     * 
     * @param indication The connection status indication message.
     */
    T visitConnectionStatusIndication(ConnectionStatusIndication indication);

    /**
     * Visits the absence of a message.  This can occur, for example, when a
     * request receives no response whatsoever.
     * 
     * @param message The message to visit.
     */
    T visitNullMessage(NullStunMessage message);

    }

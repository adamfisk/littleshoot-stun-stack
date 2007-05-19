package org.lastbamboo.common.stun.stack;

import org.lastbamboo.common.stun.stack.message.BindingResponse;

/**
 * Listener for binding response messages received from a STUN server.
 */
public interface BindingResponseListener
    {

    /**
     * Called when the client receives a binding response message.
     * 
     * @param response The binding response message.
     */
    void onBindingResponse(BindingResponse response);

    }

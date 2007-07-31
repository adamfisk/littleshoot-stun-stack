package org.lastbamboo.common.stun.stack.transaction;

import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Interface for "transaction user" (TU) classes wishing to listen for 
 * transtaction events.
 */
public interface StunTransactionListener
    {

    /**
     * Called when the transaction completed normally with a successful
     * binding response.
     * 
     * @param message The binding request.
     * @param response The binding response.
     */
    void onTransactionSucceeded(StunMessage message, StunMessage response);

    /**
     * Called when the transaction failed with an error response, a timeout,
     * or for any other reason. 
     * 
     * @param request The original request.
     * @param response The binding response.
     */
    void onTransactionFailed(StunMessage request, StunMessage response);

    }

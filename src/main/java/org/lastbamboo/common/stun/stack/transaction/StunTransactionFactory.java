package org.lastbamboo.common.stun.stack.transaction;

import org.lastbamboo.common.stun.stack.message.StunMessage;


/**
 * Interface for factories for creating transactions.
 */
public interface StunTransactionFactory
    {

    /**
     * Creates a new client transaction.
     * 
     * @param request The STUN request creating the client transaction.
     * @param transactionListener The listener for transaction events.
     * @return The new client transaction.
     */
    StunClientTransaction createClientTransaction(StunMessage request, 
        StunTransactionListener transactionListener);

    }

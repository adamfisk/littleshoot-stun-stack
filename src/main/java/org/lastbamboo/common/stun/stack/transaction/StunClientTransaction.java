package org.lastbamboo.common.stun.stack.transaction;

import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;

/**
 * Interface for client transactions.
 */
public interface StunClientTransaction extends StunMessageVisitor
    {

    /**
     * Accessor for the request that started the transaction.
     * 
     * @return The request that started the transaction.
     */
    StunMessage getRequest();
    
    /**
     * Accessor for the total transaction time for the transaction.
     * 
     * @return The total time the transaction took.
     */
    long getTransactionTime();

    /**
     * Adds a listener to the transaction.  This should typically be called 
     * before any message has been sent -- before the transaction has started 
     * -- to ensure events aren't missed.
     * 
     * @param listener The listener to add.
     */
    void addListener(StunTransactionListener listener);

    }

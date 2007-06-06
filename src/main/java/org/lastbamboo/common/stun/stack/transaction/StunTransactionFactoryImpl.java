package org.lastbamboo.common.stun.stack.transaction;

import java.util.LinkedList;
import java.util.List;

import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Class for creating SIP transactions.
 */
public class StunTransactionFactoryImpl implements StunTransactionFactory
    {
    
    private final StunTransactionTracker m_transactionTracker;

    /**
     * Creates a new transaction factory with the specified tracker.
     * 
     * @param tracker The class that keeps track of transactions.
     */
    public StunTransactionFactoryImpl(final StunTransactionTracker tracker)
        {
        this.m_transactionTracker = tracker;
        }
    
    public StunClientTransaction createClientTransaction(
        final StunMessage request, final StunTransactionListener listener)
        {
        final List<StunTransactionListener> transactionListeners = 
            new LinkedList<StunTransactionListener>();
        transactionListeners.add(listener);
        
        final StunClientTransaction ct = 
            new StunClientTransactionImpl(request, transactionListeners);
        this.m_transactionTracker.trackTransaction(ct);
        return ct;
        }

    }

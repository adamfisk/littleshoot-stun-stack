package org.lastbamboo.common.stun.stack.transaction;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.BindingResponse;
import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Implementation of a SIP client transaction.
 */
public class StunClientTransactionImpl implements StunClientTransaction
    {
    
    private static final Log LOG = 
        LogFactory.getLog(StunClientTransactionImpl.class);
    
    private final StunMessage m_request;

    private long m_transactionTime = Long.MAX_VALUE;

    private final List<StunTransactionListener> m_transactionListeners;

    private final long m_transactionStartTime;

    /**
     * Creates a new SIP client transaction.
     * 
     * @param request The request starting the transaction.
     * @param transactionListeners The listeners for transaction events.
     */
    public StunClientTransactionImpl(final StunMessage request, 
        final List<StunTransactionListener> transactionListeners)
        {
        this.m_request = request;
        this.m_transactionListeners = transactionListeners;
        this.m_transactionStartTime = System.currentTimeMillis();
        }
    
    public void addListener(final StunTransactionListener listener)
        {
        this.m_transactionListeners.add(listener);
        }

    public StunMessage getRequest()
        {
        return this.m_request;
        }
    
    public long getTransactionTime()
        {
        return m_transactionTime;
        }
    
    public void visitBindingRequest(BindingRequest binding)
        {
        // TODO Auto-generated method stub
        
        }

    public void visitBindingResponse(final BindingResponse response)
        {
        setTransactionTime();
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Transaction time: "+getTransactionTime());
            }
        for (final StunTransactionListener listener : 
            this.m_transactionListeners)
            {
            listener.onTransactionSucceeded(this.m_request, response);
            }
        }

    private void setTransactionTime()
        {
        this.m_transactionTime = 
            System.currentTimeMillis() - this.m_transactionStartTime;
        }
    }

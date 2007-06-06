package org.lastbamboo.common.stun.stack.transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.stun.stack.message.StunMessage;

/**
 * Class for matching requests and responses to their associated transactions.
 */
public class StunTransactionTrackerImpl implements StunTransactionTracker, 
    StunTransactionListener
    {
    
    private static final Log LOG = 
        LogFactory.getLog(StunTransactionTrackerImpl.class);
    
    private final Map<UUID, StunClientTransaction> m_transactions = 
        new ConcurrentHashMap<UUID, StunClientTransaction>();

    public void trackTransaction(final StunClientTransaction ct)
        {
        LOG.debug("Tracking transaction...");
        final StunMessage message = ct.getRequest();
        final UUID key = getTransactionKey(message);
        this.m_transactions.put(key, ct);
        ct.addListener(this);
        }

    public StunClientTransaction getClientTransaction(final StunMessage message)
        {
        LOG.debug("Accessing client transaction...");
        final UUID key = getTransactionKey(message);
        final StunClientTransaction ct = this.m_transactions.get(key);
        if (ct == null)
            {
            // This will happen fairly often with STUN using UDP because
            // multiple requests and responses could be sent.  We should just
            // silently ignore it.
            LOG.debug("Nothing known about transaction: "+key);
            LOG.debug("Known transactions: "+this.m_transactions.keySet());
            }
        return ct;
        }

    private UUID getTransactionKey(final StunMessage message)
        {
        return message.getTransactionId();
        }

    public void onTransactionFailed(final StunMessage request)
        {
        removeTransaction(request);
        }

    public void onTransactionSucceeded(final StunMessage request, 
        final StunMessage response)
        {
        removeTransaction(request);
        }

    private void removeTransaction(final StunMessage message)
        {
        // We now consider the transaction completed and remove the 
        // transaction.
        final UUID key = getTransactionKey(message);
        
        LOG.debug("Removing transaction with key '" + key + "'");
        this.m_transactions.remove(key);
        }
    }

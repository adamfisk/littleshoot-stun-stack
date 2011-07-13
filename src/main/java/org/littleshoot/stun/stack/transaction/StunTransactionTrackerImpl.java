package org.littleshoot.stun.stack.transaction;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.id.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.stun.stack.message.StunMessage;

/**
 * Class for matching requests and responses to their associated transactions.
 */
public class StunTransactionTrackerImpl implements
    StunTransactionTracker<StunMessage>, StunTransactionListener {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Map<UUID, StunClientTransaction<StunMessage>> transactions = 
        new ConcurrentHashMap<UUID, StunClientTransaction<StunMessage>>();

    @Override
    public void addTransaction(final StunMessage request,
            final StunTransactionListener listener,
            final InetSocketAddress localAddress,
            final InetSocketAddress remoteAddress) {
        final List<StunTransactionListener> transactionListeners = 
            new LinkedList<StunTransactionListener>();
        transactionListeners.add(listener);

        final StunClientTransaction<StunMessage> ct = 
            new StunClientTransactionImpl(request, transactionListeners, 
                remoteAddress);
        trackTransaction(ct);
    }

    private void trackTransaction(final StunClientTransaction<StunMessage> ct) {
        log.debug("Tracking transaction...");
        final StunMessage message = ct.getRequest();
        final UUID key = getTransactionKey(message);
        this.transactions.put(key, ct);
        ct.addListener(this);
    }

    @Override
    public StunClientTransaction<StunMessage> getClientTransaction(
            final StunMessage message) {
        log.debug("Accessing client transaction...");
        final UUID key = getTransactionKey(message);
        final StunClientTransaction<StunMessage> ct = this.transactions
                .get(key);
        if (ct == null) {
            // This will happen fairly often with STUN using UDP because
            // multiple requests and responses could be sent. We should just
            // silently ignore it.
            log.debug("Nothing known about transaction: " + key);
            log.debug("Known transactions: " + this.transactions.keySet());
        }
        return ct;
    }

    private UUID getTransactionKey(final StunMessage message) {
        return message.getTransactionId();
    }

    @Override
    public Object onTransactionFailed(final StunMessage request,
            final StunMessage response) {
        log.debug("Transaction failed...");
        return removeTransaction(request);
    }

    @Override
    public Object onTransactionSucceeded(final StunMessage request,
            final StunMessage response) {
        log.debug("Transaction succeeded...");
        return removeTransaction(request);
    }

    private Object removeTransaction(final StunMessage message) {
        // We now consider the transaction completed and remove the
        // transaction.
        final UUID key = getTransactionKey(message);

        log.debug("Removing transaction with key '" + key + "'");
        this.transactions.remove(key);
        return null;
    }
}

package org.littleshoot.stun.stack.transaction;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.littleshoot.stun.stack.message.BindingErrorResponse;
import org.littleshoot.stun.stack.message.BindingRequest;
import org.littleshoot.stun.stack.message.BindingSuccessResponse;
import org.littleshoot.stun.stack.message.CanceledStunMessage;
import org.littleshoot.stun.stack.message.ConnectErrorStunMessage;
import org.littleshoot.stun.stack.message.NullStunMessage;
import org.littleshoot.stun.stack.message.StunMessage;
import org.littleshoot.stun.stack.message.turn.AllocateErrorResponse;
import org.littleshoot.stun.stack.message.turn.AllocateRequest;
import org.littleshoot.stun.stack.message.turn.AllocateSuccessResponse;
import org.littleshoot.stun.stack.message.turn.ConnectRequest;
import org.littleshoot.stun.stack.message.turn.ConnectionStatusIndication;
import org.littleshoot.stun.stack.message.turn.DataIndication;
import org.littleshoot.stun.stack.message.turn.SendIndication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;

/**
 * Implementation of a STUN client transaction.
 */
public class StunClientTransactionImpl implements
        StunClientTransaction<StunMessage> {

    private final Logger m_log = LoggerFactory.getLogger(getClass());

    private final StunMessage m_request;

    private long m_transactionTime = Long.MAX_VALUE;

    /**
     * The listeners for a transaction. We must lock this whenever it's
     * accessed.
     */
    private List<StunTransactionListener> m_transactionListeners;

    private final long m_transactionStartTime;

    private final InetSocketAddress m_remoteAddress;

    /**
     * Creates a new STUN client transaction.
     * 
     * @param request
     *            The request starting the transaction.
     * @param transactionListeners
     *            The listeners for transaction events.
     * @param remoteAddress
     *            The remote address for the transaction.
     */
    public StunClientTransactionImpl(final StunMessage request,
            final List<StunTransactionListener> transactionListeners,
            final InetSocketAddress remoteAddress) {
        this.m_request = request;
        this.m_transactionListeners = Collections
                .synchronizedList(transactionListeners);
        this.m_remoteAddress = remoteAddress;
        this.m_transactionStartTime = System.currentTimeMillis();
    }

    /**
     * Creates a new STUN client transaction.
     * 
     * @param request
     *            The request starting the transaction.
     * @param transactionListener
     *            The listener for transaction events.
     * @param remoteAddress
     *            The remote address for the transaction.
     */
    public StunClientTransactionImpl(final StunMessage request,
            final StunTransactionListener transactionListener,
            final InetSocketAddress remoteAddress) {
        this.m_request = request;
        final List<StunTransactionListener> listeners = 
            new ArrayList<StunTransactionListener>();
        listeners.add(transactionListener);
        this.m_transactionListeners = Collections.synchronizedList(listeners);
        this.m_remoteAddress = remoteAddress;
        this.m_transactionStartTime = System.currentTimeMillis();
    }

    public void addListener(final StunTransactionListener listener) {
        this.m_transactionListeners.add(listener);
    }

    public StunMessage getRequest() {
        return this.m_request;
    }

    public long getTransactionTime() {
        return m_transactionTime;
    }

    public InetSocketAddress getIntendedDestination() {
        return this.m_remoteAddress;
    }

    public StunMessage visitBindingSuccessResponse(
            final BindingSuccessResponse response) {
        m_log.debug("Received success response");
        final Function<StunTransactionListener, Boolean> success = 
            new Function<StunTransactionListener, Boolean>() {

            public Boolean apply(final StunTransactionListener listener) {
                m_log.warn("Notifying transaction succeeded");
                listener.onTransactionSucceeded(m_request, response);
                return true;
            }
        };
        return notifyListeners(response, success);
    }

    public StunMessage visitBindingErrorResponse(
            final BindingErrorResponse response) {
        return notifyFailure(response);
    }

    public StunMessage visitConnectErrorMesssage(
            final ConnectErrorStunMessage message) {
        return notifyFailure(message);
    }

    private StunMessage notifyFailure(final StunMessage message) {
        final Function<StunTransactionListener, Boolean> error = 
            new Function<StunTransactionListener, Boolean>() {

            public Boolean apply(final StunTransactionListener listener) {
                m_log.warn("Notifying transaction failed");
                listener.onTransactionFailed(m_request, message);
                return true;
            }
        };
        return notifyListeners(message, error);
    }

    private StunMessage notifyListeners(final StunMessage response,
            final Function<StunTransactionListener, Boolean> closure) {
        if (isSameTransaction(response)) {
            m_log.info("About to notify " + this.m_transactionListeners.size()
                    + " listeners...");
            synchronized (this.m_transactionListeners) {
                for (final StunTransactionListener stl : this.m_transactionListeners) {
                    closure.apply(stl);
                }
            }
            return response;
        } else {
            m_log.warn("Received response from different transaction.");
            return new NullStunMessage();
        }
    }

    private boolean isSameTransaction(final StunMessage response) {
        if (!this.m_request.getTransactionId().equals(
                response.getTransactionId())) {
            m_log.error("Unexpected transaction ID.  Expected "
                    + this.m_request.getTransactionId() + " but was "
                    + response.getTransactionId());
            return false;
        } else {
            setTransactionTime();
            return true;
        }
    }

    private void setTransactionTime() {
        this.m_transactionTime = System.currentTimeMillis()
                - this.m_transactionStartTime;
    }

    public StunMessage visitBindingRequest(final BindingRequest binding) {
        return null;
    }

    public StunMessage visitAllocateRequest(final AllocateRequest request) {
        return null;
    }

    public StunMessage visitDataIndication(final DataIndication data) {
        return null;
    }

    public StunMessage visitSendIndication(final SendIndication request) {
        return null;
    }

    public StunMessage visitAllocateSuccessResponse(
            final AllocateSuccessResponse response) {
        return null;
    }

    public StunMessage visitAllocateErrorResponse(
            final AllocateErrorResponse response) {
        return null;
    }

    public StunMessage visitConnectRequest(final ConnectRequest request) {
        m_log.error("Client received connect request");
        return null;
    }

    public StunMessage visitConnectionStatusIndication(
            final ConnectionStatusIndication indication) {
        return null;
    }

    public StunMessage visitNullMessage(final NullStunMessage message) {
        return message;
    }

    public StunMessage visitCanceledMessage(final CanceledStunMessage message) {
        return message;
    }
}

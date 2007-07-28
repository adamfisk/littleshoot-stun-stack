package org.lastbamboo.common.stun.stack.transaction;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.NullStunMessage;
import org.lastbamboo.common.stun.stack.message.SuccessfulBindingResponse;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.turn.AllocateRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectRequest;
import org.lastbamboo.common.stun.stack.message.turn.ConnectionStatusIndication;
import org.lastbamboo.common.stun.stack.message.turn.DataIndication;
import org.lastbamboo.common.stun.stack.message.turn.SendIndication;
import org.lastbamboo.common.stun.stack.message.turn.SuccessfulAllocateResponse;

/**
 * Implementation of a SIP client transaction.
 */
public class StunClientTransactionImpl 
    implements StunClientTransaction<StunMessage>
    {
    
    private static final Log LOG = 
        LogFactory.getLog(StunClientTransactionImpl.class);
    
    private final StunMessage m_request;

    private long m_transactionTime = Long.MAX_VALUE;

    private final List<StunTransactionListener> m_transactionListeners;

    private final long m_transactionStartTime;

    private final InetSocketAddress m_localAddress;

    private final InetSocketAddress m_remoteAddress;

    /**
     * Creates a new SIP client transaction.
     * 
     * @param request The request starting the transaction.
     * @param transactionListeners The listeners for transaction events.
     * @param remoteAddress The remote address for the transaction.
     * @param localAddress The local address for the transation.
     */
    public StunClientTransactionImpl(final StunMessage request, 
        final List<StunTransactionListener> transactionListeners, 
        final InetSocketAddress localAddress, 
        final InetSocketAddress remoteAddress)
        {
        this.m_request = request;
        this.m_transactionListeners = transactionListeners;
        this.m_localAddress = localAddress;
        this.m_remoteAddress = remoteAddress;
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
    
    public StunMessage visitSuccessfulBindingResponse(
        final SuccessfulBindingResponse response)
        {
        if (!this.m_request.getTransactionId().equals(
            response.getTransactionId()))
            {
            LOG.error("Unexpected transaction ID.  Expected " + 
                this.m_request.getTransactionId() +
                " but was "+response.getTransactionId());
            return null;
            }
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
        return response;
        }

    private void setTransactionTime()
        {
        this.m_transactionTime = 
            System.currentTimeMillis() - this.m_transactionStartTime;
        }

    public StunMessage visitBindingRequest(final BindingRequest binding)
        {
        return null;
        }
    
    public StunMessage visitAllocateRequest(final AllocateRequest request)
        {
        return null;
        }

    public StunMessage visitDataIndication(final DataIndication data)
        {
        return null;
        }

    public StunMessage visitSendIndication(final SendIndication request)
        {
        return null;
        }

    public StunMessage visitSuccessfulAllocateResponse(
        final SuccessfulAllocateResponse response)
        {
        return null;
        }

    public StunMessage visitConnectRequest(final ConnectRequest request)
        {
        LOG.error("Client received connect request");
        return null;
        }

    public StunMessage visitConnectionStatusIndication(
        final ConnectionStatusIndication indication)
        {
        return null;
        }

    public StunMessage visitNullMessage(final NullStunMessage message)
        {
        return message;
        }

    public InetSocketAddress getIntendedDestination()
        {
        return this.m_remoteAddress;
        }
    }

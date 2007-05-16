package net.java.stun4j.stack;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.ResponseCollector;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.message.Message;
import net.java.stun4j.message.Request;
import net.java.stun4j.message.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The StunProvider class is an implementation of a Stun Transaction Layer. STUN
 * transactions are extremely simple and are only used to correlate requests and
 * responses. In the Stun4J implementation it is the transaction layer that
 * ensures reliable delivery.
 *
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
public final class StunProvider implements MessageEventHandler
    {

    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(StunProvider.class);
    
    /**
     * Stores active client transactions mapped against TransactionID-s.
     */
    private final Map m_clientTransactions = new Hashtable();

    /**
     * Currently open server transactions. The vector contains transaction ids
     * of all non-answered received requests.
     */
    private final Vector m_serverTransactions = new Vector();

    /**
     * The instance to notify for incoming StunRequests;
     */
    private RequestListener m_requestListener;

    private final StunStack m_stunStack;


    //------------------ public interface
    /**
     * Creates the provider.
     * @param stunStack The currently active stack instance.
     */
    StunProvider(final StunStack stunStack)
        {
        this.m_stunStack = stunStack;
        }

    /**
     * Sends the specified request through the specified access point, and
     * registers the specified ResponseCollector for later notification.
     * @param  request     the request to send
     * @param  sendTo      the destination address of the request.
     * @param  sendThrough the access point to use when sending the request
     * @param  collector   the instance to notify when a response arrives or the
     *                     the transaction timeouts
     * @throws StunException
     * ILLEGAL_STATE if the stun stack is not started. <br/>
     * ILLEGAL_ARGUMENT if the apDescriptor references an access point that had
     * not been installed <br/>
     * NETWORK_ERROR if an error occurs while sending message bytes through the
     * network socket. <br/>
     */
    public void sendRequest(final Request request, final StunAddress sendTo,
        final NetAccessPointDescriptor sendThrough,
        final ResponseCollector collector) throws StunException
        {
        final StunClientTransaction clientTransaction =
            new StunClientTransaction(this, 
                this.m_stunStack.getNetAccessManager(), request, 
                sendTo, sendThrough, collector);

        m_clientTransactions.put(clientTransaction.getTransactionID(),
            clientTransaction);
        clientTransaction.sendRequest();
        }

    /**
     * Sends the specified response message through the specified access point.
     *
     * @param transactionID the id of the transaction to use when sending the
     *    response. Actually we are getting kind of redundant here as we already
     *    have the id in the response object, but I am bringing out as an extra
     *    parameter as the user might otherwise forget to explicitly set it.
     * @param response      the message to send.
     * @param sendThrough   the access point to use when sending the message.
     * @param sendTo        the destination of the message.
     * @throws StunException TRANSACTION_DOES_NOT_EXIST if the response message
     * has an invalid transaction id. <br/>
     * ILLEGAL_STATE if the stun stack is not started. <br/>
     * ILLEGAL_ARGUMENT if the apDescriptor references an access point that had
     * not been installed <br/>
     * NETWORK_ERROR if an error occurs while sending message bytes through the
     * network socket. <br/>
     */
    public void sendResponse(final byte[] transactionID, 
        final Response response, final NetAccessPointDescriptor sendThrough,
        final StunAddress sendTo) throws StunException
        {
        final TransactionID tid = 
            TransactionID.createTransactionID(transactionID);


        if(!m_serverTransactions.remove(tid))
            throw new StunException(StunException.TRANSACTION_DOES_NOT_EXIST,
                                    "The trensaction specified in the response "
                                    +"object does not exist.");

        response.setTransactionID(transactionID);
        this.m_stunStack.getNetAccessManager().sendMessage(response, 
            sendThrough, sendTo);
        }

    /**
     * Sets the listener that should be notified when a new Request is received.
     * @param m_requestListener the listener interested in incoming requests.
     */
    public void setRequestListener(final RequestListener requestListener)
        {
        this.m_requestListener = requestListener;
        }

    /**
     * Removes a client transaction from this providers client transactions 
     * list.  Method is used by ClientStunTransaction-s themselves when a 
     * timeout occurs.
     * @param tran the transaction to remove.
     */
    void removeClientTransaction(final StunClientTransaction tran)
        {
        m_clientTransactions.remove(tran.getTransactionID());
        }

    /**
     * Called to notify this provider for an incoming message.
     * @param event the event object that contains the new message.
     */
    public void handleMessageEvent(final StunMessageEvent event)
        {
        LOG.trace("Received message event!!");
        final Message msg = event.getMessage();
        //request
        if(msg instanceof Request)
            {
            final TransactionID serverTid = 
                TransactionID.createTransactionID(msg.getTransactionID());

            m_serverTransactions.add(serverTid);
            if(m_requestListener != null)
                m_requestListener.requestReceived(event);
            }
        //response
        else if(msg instanceof Response)
            {
            final TransactionID tid = 
                TransactionID.createTransactionID(msg.getTransactionID());

            final StunClientTransaction tran = 
                (StunClientTransaction)m_clientTransactions.remove(tid);

            if(tran != null)
                {
                tran.handleResponse(event);
                }
            else
                {
                // This should not happen with compliant servers, but it does
                // seem to happen quite often.
                LOG.debug("No transaction for response!");
                }
            }

        }

    /**
     * Cancels all running transactions and prepares for garbage collection
     */
    void shutDown()
        {
        m_requestListener = null;
        final Iterator tids = m_clientTransactions.keySet().iterator();
        while (tids.hasNext()) 
            {
            final TransactionID item = (TransactionID)tids.next();
            final StunClientTransaction tran =
                (StunClientTransaction)m_clientTransactions.remove(item);
            if(tran != null)
                tran.cancel();

            }
        }
    }
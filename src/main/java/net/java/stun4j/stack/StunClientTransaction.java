package net.java.stun4j.stack;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.ResponseCollector;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.message.Request;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ClientTransaction class retransmits (what a surprise) requests as
 * specified by rfc 3489.
 *
 * Once formulated and sent, the client sends the Binding Request.  Reliability
 * is accomplished through request retransmissions.  The ClientTransaction
 * retransmits the request starting with an interval of 100ms, doubling
 * every retransmit until the interval reaches 1.6s.  Retransmissions
 * continue with intervals of 1.6s until a response is received, or a
 * total of 9 requests have been sent. If no response is received by 1.6
 * seconds after the last request has been sent, the client SHOULD
 * consider the transaction to have failed. In other words, requests
 * would be sent at times 0ms, 100ms, 300ms, 700ms, 1500ms, 3100ms,
 * 4700ms, 6300ms, and 7900ms. At 9500ms, the client considers the
 * transaction to have failed if no response has been received.
 *
 *
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
final class StunClientTransaction implements Runnable
    {
    
    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(StunClientTransaction.class);
    
    /**
     * Maximum number of retransmissions. Once this number is reached and if no
     * response is received after MAX_WAIT_INTERVAL miliseconds the request is
     * considered unanswered.
     */
    public static final int MAX_RETRANSMISSIONS = 8;

    /**
     * The number of miliseconds to wait before the first retansmission of the
     * request.
     */
    public static final int ORIGINAL_WAIT_INTERVAL = 100;

    /**
     * The maximum wait interval. Once this interval is reached we should stop
     * doubling its value.
     */
    public static final int MAX_WAIT_INTERVAL = 1600;

    /**
     * Indicates how many times we have retransmitted so fat.
     */
    private int retransmissionsCounter = 0;

    /**
     * How much did we wait after our last retransmission.
     */
    private int lastWaitInterval       = ORIGINAL_WAIT_INTERVAL;

    /**
     * The StunProvider that created us.
     */
    private StunProvider      providerCallback  = null;

    /**
     * The request that we are retransmitting.
     */
    private Request request           = null;

    /**
     * The destination of the request.
     */
    private StunAddress requestDestination= null;


    /**
     * The id of the transaction.
     */
    private TransactionID transactionID      = null;

    /**
     * The NetAccessPoint through which the original request was sent an where
     * we are supposed to be retransmitting.
     */
    private NetAccessPointDescriptor apDescriptor = null;

    /**
     * The instance to notify when a response has been received in the current
     * transaction or when it has timed out.
     */
    private ResponseCollector responseCollector = null;

    /**
     * Specifies whether the transaction is active or not.
     */
    private boolean cancelled = false;

    /**
     * The date (in millis) when the next retransmission should follow.
     */
    private long nextRetransmissionDate = -1;

    /**
     * The thread that this transaction runs in.
     */
    private final Thread runningThread = new Thread(this);

    private final NetAccessManager m_netAccessManager;

    /**
     * Creates a client transaction
     * @param providerCallback the provider that created us.
     * @param request the request that we are living for.
     * @param requestDestination the destination of the request.
     * @param apDescriptor the access point through which we are supposed to
     * @param responseCollector the instance that should receive this request's
     * response.
     * retransmit.
     */
    public StunClientTransaction(final StunProvider providerCallback,
        final NetAccessManager netAccessManager, 
        final Request request, final StunAddress requestDestination,
        final NetAccessPointDescriptor apDescriptor,
        final ResponseCollector responseCollector)
        {
        this.providerCallback  = providerCallback;
        this.m_netAccessManager = netAccessManager;
        this.request           = request;
        this.apDescriptor      = apDescriptor;
        this.responseCollector = responseCollector;
        this.requestDestination = requestDestination;

        this.transactionID = TransactionID.createTransactionID();
        try
            {
            request.setTransactionID(transactionID.getTransactionID());
            }
        catch (final StunException e)
            {
            LOG.error("Could not set transation ID", e);
            //Shouldn't happen so lets just throw a runtime exception in
            //case anything is real messed up
            throw new IllegalArgumentException("The TransactionID class "
                +"genereated an invalid transaction ID");
            }
    }

    /**
     * Implements the retransmissions algorithm. Retransmits the request
     * starting with an interval of 100ms, doubling every retransmit until the
     * interval reaches 1.6s.  Retransmissions continue with intervals of 1.6s
     * until a response is received, or a total of 9 requests have been sent.
     * If no response is received by 1.6 seconds after the last request has been
     * sent, we consider the transaction to have failed.
     */
    public void run()
        {
        runningThread.setName("CliTran");
        while(retransmissionsCounter++ < MAX_RETRANSMISSIONS)
            {

            waitUntilNextRetransmissionDate();
            //did someone tell us to get lost?

            if (cancelled)
                return;

            if (lastWaitInterval < MAX_WAIT_INTERVAL)
                lastWaitInterval *= 2;

            try
                {
                m_netAccessManager.sendMessage(
                    request, apDescriptor, requestDestination);
                }
            catch (final StunException e)
                {
                LOG.warn("Could not send message", e);
                //I wonder whether we should notify anyone that a retransmission
                //has failed.
                };

            schedule(lastWaitInterval);
            }

        responseCollector.processTimeout();
        providerCallback.removeClientTransaction(this);

        }

    /**
     * Sends the request and schedules the first retransmission for after
     * ORIGINAL_WAIT_INTERVAL and thus starts the retransmission algorithm.
     * @throws StunException if message encoding fails, ILLEGAL_ARGUMENT if the
     * apDescriptor references an access point that had not been installed,
     * NETWORK_ERROR if an error occurs while sending message bytes through the
     * network socket.

     */
    void sendRequest() throws StunException
        {
        m_netAccessManager.sendMessage(this.request, apDescriptor, 
            requestDestination);

        schedule(ORIGINAL_WAIT_INTERVAL);
        runningThread.start();
        }

    /**
     * Returns the request that was the reason for creating this transaction.
     * @return the request that was the reason for creating this transaction.
     */
    Request getRequest()
        {
        return this.request;
        }


    /**
     * Waits until next retransmission is due or until the transaction is
     * cancelled (whichever comes first).
     */
    synchronized void waitUntilNextRetransmissionDate()
        {
        long current = System.currentTimeMillis();
        while(nextRetransmissionDate - current > 0)
            {
            try
                {
                wait(nextRetransmissionDate - current);
                }
            catch (final InterruptedException ex)
                {
                }

            //did someone ask us to get lost?
            if (cancelled)
                return;
            current = System.currentTimeMillis();
            }
        }

    /**
     * Sets the next retransmission date.
     * @param timeout the number of millis to wait before next retransmission.
     */
    void schedule(final long timeout)
        {
        this.nextRetransmissionDate = System.currentTimeMillis() + timeout;
        }

    /**
     * Cancels the transaction. Once this method is called the transaction is
     * considered terminated and will stop retransmissions.
     */
    synchronized void cancel()
        {
        this.cancelled = true;
        notifyAll();
        }

    /**
     * Dispatches the response then cancels itself and notifies the StunProvider
     * for its termination.
     * @param evt the event that contains the newly received message
     */
    void handleResponse(final StunMessageEvent evt)
        {
        this.cancel();
        this.responseCollector.processResponse(evt);
        }

    /**
     * Returns the ID of the current transaction.
     *
     * @return the ID of the transaction.
     */
    TransactionID getTransactionID()
        {
        return this.transactionID;
        }
    }
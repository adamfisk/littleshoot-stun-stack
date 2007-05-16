package net.java.stun4j.stack;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.message.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Manages NetAccessPoints and MessageProcessor pooling. This class serves as a
 * layer that masks network primitives and provides equivalent STUN abstractions.
 * Instances that operate with the NetAccessManager are only supposed to
 * understand STUN talk and shouldn't be aware of datagrams sockets, and etc.
 *
 * <p>Organisation: Louis Pasteur University, Strasbourg, France</p>
 *                   <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */

final class NetAccessManager
    {

    private static final Log LOG = LogFactory.getLog(NetAccessManager.class);
    
    /**
     * All access points currently in use. The table maps 
     * NetAccessPointDescriptor to NetAccessPoint.
     */
    private final Map m_netAccessPoints = 
        Collections.synchronizedMap(new HashMap());

    /**
     * A synchronized FIFO where incoming messages are stocked for processing.
     */
    private final MessageQueue m_messageQueue = new MessageQueue();

    /**
     * A thread pool of message processors.
     */
    private final Vector m_messageProcessors = new Vector();

    /**
     * The instance that should be notified whan an incoming message has been
     * processed and ready for delivery
     */
    private final MessageEventHandler m_messageEventHandler;

    /**
     * Indicates whether the access manager has been started.
     */
    private boolean m_isRunning = false;

    /**
     * The number of threads to split our flow in.
     */
    private static final int DEFAULT_THREAD_POOL_SIZE = 5;
    
    /**
     * The size of the thread pool to start with.
     */
    private int initialThreadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    /**
     * Creates a new manager for network access points.
     * @param eventHandler The handler for message events. 
     */
    public NetAccessManager(final MessageEventHandler eventHandler)
        {
        m_messageEventHandler = eventHandler;
        start();
        }
    
    /**
     * Initializes the message processors pool and sets the status of the manager
     * to running.
     */
    private void start()
        {
        if (m_isRunning)
            return;

        this.m_isRunning = true;
        this.initThreadPool();
        }

    /**
     * Determines whether the NetAccessManager has been started.
     * @return true if this NetAccessManager has been started, and false
     * otherwise.
     */
    boolean isRunning()
        {
        return m_isRunning;
        }

    //------------------------ error handling -------------------------------------
    /**
     * A civilized way of not caring!
     * @param message a description of the error
     * @param error   the error that has occurred
     */
    public void handleError(String message, Throwable error)
        {
        /** @todo log */
        /**
         * apart from logging, i am not sure what else we could do here.
         * So for the time being - just drop.
         */
        }

    /**
     * Clears the faulty thread and tries to repair the damage and instanciate
     * a replacement.
     *
     * @param callingThread the thread where the error occurred.
     * @param message       A description of the error
     * @param error         The error itself
     */
    public void handleFatalError(final Runnable callingThread, String message,
        Throwable error)
        {
        if (callingThread instanceof NetAccessPoint)
            {
            final NetAccessPoint ap = (NetAccessPoint) callingThread;

            //make sure socket is closed
            removeNetAccessPoint(ap.getDescriptor());

            try
                {
                System.err.println("An access point has unexpectedly "
                    + "stopped. AP:" + ap.toString());
                installNetAccessPoint(ap.getDescriptor());
                /** @todo: log fixing the error*/
                }
            catch (final StunException ex)
                {
                //make sure nothing's left and notify user
                removeNetAccessPoint(ap.getDescriptor());
                LOG.warn("Failed to relaunch accesspoint:" + ap, ex);
                }
            }
        else if (callingThread instanceof MessageProcessor)
            {
            MessageProcessor mp = (MessageProcessor) callingThread;

            //make sure the guy's dead.
            mp.stop();
            m_messageProcessors.remove(mp);

            mp = new MessageProcessor(m_messageQueue, m_messageEventHandler);
            mp.start();
            LOG.warn("A message processor has been relaunched because of " +
                "an error.");

            }
        LOG.error("Error was: ", error);
        }

    /**
     * Creates and starts a new access point according to the given descriptor.
     * If the specified access point has already been installed the method
     * has no effect.
     *
     * @param apDescriptor   a description of the access point to create.
     * @throws StunException if we fail to create or start the accesspoint.
     */
    void installNetAccessPoint(final NetAccessPointDescriptor apDescriptor)
        throws StunException
        {
        LOG.trace("Installing net access point: " + 
            apDescriptor.getAddress().getSocketAddress()+" this: "+this);
        if (m_netAccessPoints.containsKey(apDescriptor))
            {
            LOG.trace("Cannot install access point on existing address...");
            return;
            }

        final NetAccessPoint ap = new NetAccessPoint(apDescriptor,
            m_messageQueue);
        
        startAccessPoint(apDescriptor, ap);
        }

    /**
     * Creates and starts a new access point according to the given descriptor.
     * If the specified access point has already been installed the method
     * has no effect.
     *
     * @param apDescriptor A description of the access point to create.
     * @param socket The UDP socket for the access point.
     * @throws StunException if we fail to create or start the accesspoint.
     */
    void installNetAccessPoint(final NetAccessPointDescriptor apDescriptor,
        final DatagramSocket socket) throws StunException
        {
        LOG.trace("Installing net access point: " + 
            apDescriptor.getAddress().getSocketAddress()+" this: "+this);
        if (m_netAccessPoints.containsKey(apDescriptor))
            {
            LOG.trace("Cannot install access point on existing address...");
            return;
            }

        final NetAccessPoint ap = new NetAccessPoint(apDescriptor,
            m_messageQueue);
        ap.useExternalSocket(socket);       
        startAccessPoint(apDescriptor, ap);
        }
    
    private void startAccessPoint(final NetAccessPointDescriptor apDescriptor, 
        final NetAccessPoint ap) throws StunException
        {
        m_netAccessPoints.put(apDescriptor, ap);
        try
            {
            ap.start();
            }
        catch (final IOException e)
            {
            LOG.warn("Could not start access point", e);
            throw new StunException(StunException.NETWORK_ERROR,
                "An IOException occurred while starting the access point", e);
            }
        }

    /**
     * Creates and starts a new access point based on the specified socket.
     * If the specified access point has already been installed the method
     * has no effect.
     *
     * @param  socket   the socket that the access point should use.
     * @return an access point descriptor to allow further management of the
     * newly created access point.
     * @throws StunException if we fail to create or start the accesspoint.
     */
    NetAccessPointDescriptor installNetAccessPoint(final DatagramSocket socket)
        throws StunException
        {
        LOG.trace("Installing net access point with socket: "
            + socket.getLocalSocketAddress());
        //no null check - let it through a null pointer exception
        final StunAddress address = new StunAddress(socket.getLocalAddress(),
            socket.getLocalPort());
        final NetAccessPointDescriptor apDescriptor = 
            new NetAccessPointDescriptor(address);

        installNetAccessPoint(apDescriptor, socket);
        return apDescriptor;
        }

    /**
     * Stops and deletes the specified access point.
     * @param apDescriptor the access  point to remove
     */
    void removeNetAccessPoint(final NetAccessPointDescriptor apDescriptor)
        {
        final NetAccessPoint accessPoint = 
            (NetAccessPoint) m_netAccessPoints.remove(apDescriptor);

        if (accessPoint != null)
            {
            LOG.trace("Removing access point: "+accessPoint);
            accessPoint.stop();
            }
        }

    //---------------thread pool implementation --------------------------------
    /**
     * Adjusts the number of concurrently running MessageProcessors.
     * If the number is smaller or bigger than the number of threads
     * currentlyrunning, then message processors are created/deleted so that 
     * their count matches the new value.
     *
     * @param threadPoolSize the number of MessageProcessors that should be 
     * running concurrently
     * @throws StunException INVALID_ARGUMENT if threadPoolSize is not a 
     * valid size.
     */
    void setThreadPoolSize(final int threadPoolSize) throws StunException
        {
        if (threadPoolSize < 1)
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                threadPoolSize + " is not a legal thread pool size value.");

        //if we are not running just record the size so that we could init later.
        if (!m_isRunning)
            {
            initialThreadPoolSize = threadPoolSize;
            return;
            }

        if (m_messageProcessors.size() < threadPoolSize)
            {
            //create additional processors
            fillUpThreadPool(threadPoolSize);
            }
        else
            {
            //delete extra processors
            shrinkThreadPool(threadPoolSize);
            }
        }

    /**
     * Populates the thread pool.
     */
    private void initThreadPool()
        {
        //create additional processors
        fillUpThreadPool(initialThreadPoolSize);
        }

    /**
     * Starts all message processors
     *
     * @param newSize the new thread poolsize
     */
    private void fillUpThreadPool(final int newSize)
        {
        //make sure we don't resize more than once
        m_messageProcessors.ensureCapacity(newSize);

        for (int i = m_messageProcessors.size(); i < newSize; i++)
            {
            final MessageProcessor mp = new MessageProcessor(m_messageQueue,
                m_messageEventHandler);
            m_messageProcessors.add(mp);

            mp.start();
            }

        }

    /**
     * Starts all message processors
     *
     * @param newSize the new thread poolsize
     */
    private void shrinkThreadPool(int newSize)
        {
        while (m_messageProcessors.size() > newSize)
            {
            final MessageProcessor mp = 
                (MessageProcessor) m_messageProcessors.remove(0);
            mp.stop();
            }
        }

    //--------------- SENDING MESSAGES -----------------------------------------
    /**
     * Sends the specified stun message through the specified access point.
     * @param stunMessage the message to send
     * @param apDescriptor the access point to use to send the message
     * @param address the destination of the message.
     * @throws StunException if message encoding fails, ILLEGAL_ARGUMENT if the
     * apDescriptor references an access point that had not been installed,
     * NETWORK_ERROR if an error occurs while sending message bytes through the
     * network socket.
     */
    void sendMessage(final Message stunMessage,
        final NetAccessPointDescriptor apDescriptor, final StunAddress address)
        throws StunException
        {
        LOG.trace("Sending STUN message to: "+apDescriptor);
        final byte[] bytes = stunMessage.encode();
        final NetAccessPoint accessPoint = 
            (NetAccessPoint) m_netAccessPoints.get(apDescriptor);

        if (accessPoint == null)
            {
            LOG.error("Could not find access point -- null...this: "+this);
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                "The specified access point had not been installed: "+address);
            }

        try
            {
            accessPoint.sendMessage(bytes, address);
            }
        catch (final IOException e)
            {
            LOG.warn("Exception sending stun message", e);
            throw new StunException(StunException.NETWORK_ERROR,
                "An Exception occurred while sending message bytes "
                    + "through a network socket!", e);
            }
        }

    }

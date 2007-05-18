package net.java.stun4j.stack;

import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.message.Message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class is used to parse and dispatch incoming messages in a multithreaded
 * manner.
 *
 * <p>Organisation: Louis Pasteur University, Strasbourg, France</p>
 *                  <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
final class MessageProcessor implements Runnable
    {
    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(MessageProcessor.class);
    
    private final MessageQueue m_messageQueue;
    private final MessageEventHandler m_messageHandler;

    private boolean m_isRunning = false;
    private Thread m_runningThread;

    MessageProcessor(final MessageQueue queue, 
        final MessageEventHandler messageHandler)
        {   
        if (queue == null)
            {
            throw new NullPointerException("null queue");
            }
        if (messageHandler == null)
            {
            throw new NullPointerException("null message handler");
            }
        this.m_messageQueue = queue;
        this.m_messageHandler = messageHandler;
        }

    /**
     * Does the message parsing.
     */
    public void run()
        {
        // add an extra try/catch block that handles uncatched errors and helps 
        // avoid having dead threads in our pools.
        try
            {
            processMessages();
            }
        catch (final Throwable t)
            {
            LOG.error("Fatal exception", t);
            }
        }

    private void processMessages()
        {
        while (m_isRunning)
            {
            final RawMessage rawMessage;
            try
                {
                rawMessage = m_messageQueue.remove();
                }
            catch (final InterruptedException e)
                {
                LOG.warn("Unexpected interrupt", e);
                continue;
                }

            // were we asked to stop?
            if (!isRunning())
                return;

            //anything to parse?
            if (rawMessage == null)
                continue;

            LOG.trace("Received raw message!!");
            final Message stunMessage;
            try
                {
                stunMessage =
                    Message.decode(rawMessage.getBytes(),
                        (char) 0, (char) rawMessage.getMessageLength());
                
                LOG.debug("Decoded STUN message: "+stunMessage);
                }
            catch (final StunException e)
                {
                LOG.warn("Unexpected STUN exception", e);
                continue; //let this one go and wish for better luck next time.
                }

            final StunAddress address = 
                new StunAddress(
                    rawMessage.getRemoteAddress().getAddress(),
                    rawMessage.getRemoteAddress().getPort());
            final StunMessageEvent stunMessageEvent =
                new StunMessageEvent(rawMessage.getNetAccessPoint(),
                    stunMessage, address);
            m_messageHandler.handleMessageEvent(stunMessageEvent);
            }
        }

    /**
     * Start the message processing thread.
     */
    void start()
        {
        this.m_isRunning = true;

        m_runningThread = new Thread(this, "MessageProcessor-Thread");
        m_runningThread.setDaemon(true);
        m_runningThread.start();
        }


    /**
     * Shut down the message processor.
     */
    void stop()
        {
        this.m_isRunning = false;
        }

    /**
     * Determines whether the processor is still running;
     *
     * @return true if the processor is still authorised to run, and false
     * otherwise.
     */
    boolean isRunning()
        {
        return m_isRunning;
        }
    }
package net.java.stun4j.client;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.ResponseCollector;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.message.Request;
import net.java.stun4j.stack.StunProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

/**
 * A utility used to flatten the multithreaded architecture of the Stack
 * and execute the discovery process in a synchronized manner. Roughly what
 * happens here is:
 *
 * ApplicationThread:
 *     sendMessage()
 * 	   wait();
 *
 * StackThread:
 *     processMessage/Timeout()
 *     {
 *          saveMessage();
 *          notify();
 *     }
 *
 *
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
class BlockingRequestSender implements ResponseCollector
    {
    /**
     * Logger for this class.
     */
    private static final Log LOG = 
            LogFactory.getLog (BlockingRequestSender.class);
    
    /**
     * The STUN provider.
     */
    private final StunProvider m_stunProvider;
    
    /**
     * The access point descriptor describing the network access point to which
     * to send requests.
     */
    private final NetAccessPointDescriptor m_apDescriptor;

    /**
     * The response event, if a successful response was received for our
     * request.
     */
    private StunMessageEvent m_responseEvent;
    
    /**
     * Whether the current request timed out.
     */
    private boolean m_timedOut;
    
    /**
     * Whether we currently have an outstanding request.  When this is true,
     * we are blocked waiting for a response from our request.
     */
    private boolean m_requestOutstanding;

    /**
     * Constructs a new blocking request sender.
     * 
     * @param stunProvider The STUN provider.
     * @param apDescriptor The descriptor describing the network access point
     *                         to which to send requests.
     */
    BlockingRequestSender (final StunProvider stunProvider,
                           final NetAccessPointDescriptor apDescriptor)
        {
        m_stunProvider = stunProvider;
        m_apDescriptor = apDescriptor;
        
        m_responseEvent = null;
        m_timedOut = false;
        m_requestOutstanding = false;
        }

    public synchronized void processResponse (final StunMessageEvent event)
        {
        // Reset the blocking guard.
        m_requestOutstanding = false;
        
        // Store the event for use by the blocking method.
        m_responseEvent = event;
        notifyAll ();
        }

    public synchronized void processTimeout ()
        {
        // Flag that we have timed out so we can tell what happened.
        m_timedOut = true;
        
        // Reset the blocking guard.
        m_requestOutstanding = false;
        notifyAll ();
        }

    /**
     * Sends the specified request and blocks until a response has been
     * received or the request transaction has timed out.
     * 
     * @param request       The request to send.
     * @param serverAddress The request destination address.
     * 
     * @return The event of the response.
     * 
     * @throws StunException If there is a problem getting a response.
     */
    public synchronized StunMessageEvent sendRequestAndWaitForResponse
            (final Request request, final StunAddress serverAddress)
                throws StunException
        {
        LOG.trace("Sending request to STUN provider...");
        m_stunProvider.sendRequest (request,
                                    serverAddress,
                                    m_apDescriptor,
                                    BlockingRequestSender.this);

        // At this point, we have an outstanding request.  We set our guard.
        m_requestOutstanding = true;
    
        // While we still have not received a response for our request, we
        // wait.  When the request comes back either with a response or a
        // timeout, the guard is reset.  At that point, we continue past this
        // loop.
        while (m_requestOutstanding)
            {
            try
                {
                // We wait with no time limit since the request should have
                // its own time limit.
                wait ();
                }
            catch (final InterruptedException interruptedException)
                {
                // If we get interrupted, we just flunk out since someone
                // clearly wants us to stop.
                throw (new RuntimeException (interruptedException));
                }
            }

        // Timing out is expected in some cases, as STUN tests largely consist
        // of seeing which messages the server is unable to
        // respond to.
        if (m_timedOut)
            {
            // Reset for the next call.
            m_timedOut = false;
            
            return null;
            }
        else
            {
            final StunMessageEvent event;

            // We got a response.
            
            // Make sure the response was set properly.
            Assert.notNull (m_responseEvent);
            
            // Save the event to return so we can clear the event field for the
            // next call.
            event = m_responseEvent;
            
            // Reset for the next call.
            m_responseEvent = null;

            return (event);
            }
        }
    }


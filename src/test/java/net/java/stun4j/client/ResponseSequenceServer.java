package net.java.stun4j.client;

import java.util.Vector;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.message.Response;
import net.java.stun4j.stack.RequestListener;
import net.java.stun4j.stack.StunProvider;
import net.java.stun4j.stack.StunStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * This class implements a programmable STUN server that sends predefined
 * sequences of responses. It may be used to test whether a STUN client
 * behaves correctly in different use cases.
 *
 *
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
public final class ResponseSequenceServer 
    extends AbstractDependencyInjectionSpringContextTests 
    implements RequestListener
    {
    private static final Log LOG = 
        LogFactory.getLog(ResponseSequenceServer.class);
    
    /**
     * The sequence of responses to send.
     */
    private Vector messageSequence = new Vector();

    private StunStack    stunStack    = null;
    private StunProvider stunProvider = null;

    private StunAddress              serverAddress       = null;
    private NetAccessPointDescriptor apDescriptor        = null;

    public ResponseSequenceServer(StunAddress bindAddress, 
        final StunStack stack)
        {
        this.serverAddress = bindAddress;
        this.stunStack = stack;
        }
    

    protected String[] getConfigLocations()
        {
        return new String[] {"stunBeans.xml"};
        }

    /**
     * Initializes the underlying stack
     * @throws StunException if something fails
     */
    public void start() throws StunException
        {
        stunProvider = stunStack.getProvider();
        apDescriptor = new NetAccessPointDescriptor(serverAddress);
        stunStack.installNetAccessPoint(apDescriptor);
        stunProvider.setRequestListener(this);
        }

    /**
     * Resets the server (deletes the sequence and stops the stack)
     */
    public void shutDown()
        {
        messageSequence.removeAllElements();
        stunStack    = null;
        stunProvider = null;
        }
        

    /**
     * Adds the specified response to this sequence or marks a pause (i.e. do
     * not respond) if response is null.
     * @param response the response to add or null to mark a pause
     */
    public void addMessage(Response response)
        {
        LOG.trace("Adding message...");
        if (response == null)
            {
            //leave a mark to skip a message

            //according to rfc 3489 the client send a request up to 9 times
            //before giving up so introduce 9 marks here
            for(int i = 0; i < 9; i++)
                messageSequence.add(new Boolean(false));
            }
        else
            messageSequence.add(response);
        }

    /**
     * Completely ignores the event that is passed and just sends the next
     * message from the sequence - or does nothing if there's something
     * different from a Response on the current position.
     * @param evt the event being dispatched
     */
    public void requestReceived(final StunMessageEvent evt)
        {
        LOG.trace("Received request!!");
        if(messageSequence.isEmpty())
            {
            LOG.trace("Message sequence is empty!!  All Asia, all the time.");
            return;
            }
        Object obj = messageSequence.remove(0);

        if( !(obj instanceof Response) )
            {
            LOG.trace("Next message was not a response!!!");
            return;
            }

        Response res = (Response)obj;

        try
            {
            LOG.trace("Sending response!!!!!!!!!!!!!!!!!!!!!!!");
            stunProvider.sendResponse(evt.getMessage().getTransactionID(),
                                      res,
                                      apDescriptor,
                                      evt.getRemoteAddress());
            }
        catch (Exception ex)
            {
            ex.printStackTrace();
            }
        }   
    }

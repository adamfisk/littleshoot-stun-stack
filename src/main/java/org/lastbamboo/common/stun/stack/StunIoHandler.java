package org.lastbamboo.common.stun.stack;

import java.net.PortUnreachableException;

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.IcmpErrorStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;
import org.lastbamboo.common.stun.stack.message.VisitableStunMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes STUN messages.  This class can be subclassed to implement 
 * specialized policies, for example for specialized policies for idle sessions
 * for specific STUN usages.
 * 
 * @param <T> The type returned when visitors visit {@link StunMessage}s. 
 */
public class StunIoHandler<T> extends IoHandlerAdapter
    {
    
    private final Logger LOG = LoggerFactory.getLogger(StunIoHandler.class);
    private final StunMessageVisitorFactory<T> m_visitorFactory;
    
    /**
     * Creates a new STUN IO handler class.
     * 
     * @param visitorFactory The factory for creating visitors for the 
     * specific STUN deployment.  Some factories might create visitors for the
     * client side while others create visitors for the server side, 
     * for example.
     */
    public StunIoHandler(final StunMessageVisitorFactory<T> visitorFactory)
        {
        m_visitorFactory = visitorFactory;
        }

    public void messageReceived(final IoSession session, final Object message)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Received message: "+message);
            }
        
        final VisitableStunMessage stunMessage = (VisitableStunMessage) message;
        
        // The visitor will handle the particular message type, allowing for 
        // variation between, for example, client and server visitor 
        // implementations.
        final StunMessageVisitor<T> visitor = 
            this.m_visitorFactory.createVisitor(session);
        
        LOG.debug("Sending message to visitor: {}", visitor);
        stunMessage.accept(visitor);
        }
    
    public void exceptionCaught(final IoSession session, final Throwable cause)
        throws Exception
        {
        LOG.debug("Exception on STUN IoHandler", cause);
        if (cause instanceof PortUnreachableException)
            {
            // We pretend it's like an ordinary STUN "message" and visit it.
            // We allow the processing classes to close the session as they
            // see fit.
            //
            // This will occur relatively frequently over the course of normal
            // STUN checks.
            final IcmpErrorStunMessage icmpError = new IcmpErrorStunMessage();
            messageReceived(session, icmpError);
            }
        else
            {
            session.close();
            }
        }
    }

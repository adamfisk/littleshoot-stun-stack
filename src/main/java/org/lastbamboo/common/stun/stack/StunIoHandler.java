package org.lastbamboo.common.stun.stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitorFactory;
import org.lastbamboo.common.stun.stack.message.VisitableStunMessage;

/**
 * Processes STUN messages.  This class can be subclassed to implement 
 * specialized policies, for example for specialized policies for idle sessions
 * for specific STUN usages.
 */
public class StunIoHandler extends IoHandlerAdapter
    {
    
    private final Log LOG = LogFactory.getLog(StunIoHandler.class);
    private final StunMessageVisitorFactory m_visitorFactory;
    
    /**
     * Creates a new STUN IO handler class.
     * 
     * @param visitorFactory The factory for creating visitors for the 
     * specific STUN deployment.  Some factories might create visitors for the
     * client side while others create visitors for the server side, 
     * for example.
     */
    public StunIoHandler(final StunMessageVisitorFactory visitorFactory)
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
        stunMessage.accept(this.m_visitorFactory.createVisitor(session));
        }
    
    public void exceptionCaught(final IoSession session, final Throwable cause)
        throws Exception
        {
        LOG.warn("Exception on STUN IoHandler", cause);
        session.close();
        }
    }

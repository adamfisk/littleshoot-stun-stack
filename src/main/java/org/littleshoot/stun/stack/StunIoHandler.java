package org.littleshoot.stun.stack;

import java.net.PortUnreachableException;

import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoHandlerAdapter;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.util.SessionUtil;
import org.littleshoot.stun.stack.message.ConnectErrorStunMessage;
import org.littleshoot.stun.stack.message.StunMessage;
import org.littleshoot.stun.stack.message.StunMessageVisitor;
import org.littleshoot.stun.stack.message.StunMessageVisitorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes STUN messages.  This class can be sub-classed to implement 
 * specialized policies, for example for specialized policies for idle sessions
 * for specific STUN usages.
 * 
 * @param <T> The type returned when visitors visit {@link StunMessage}s. 
 */
public class StunIoHandler<T> extends IoHandlerAdapter {

    private final Logger m_log = LoggerFactory.getLogger(StunIoHandler.class);
    private final StunMessageVisitorFactory m_visitorFactory;
    
    /**
     * Creates a new STUN IO handler class.
     * 
     * @param visitorFactory The factory for creating visitors for the 
     * specific STUN deployment.  Some factories might create visitors for the
     * client side while others create visitors for the server side, 
     * for example.
     */
    public StunIoHandler(final StunMessageVisitorFactory visitorFactory) {
        this.m_visitorFactory = visitorFactory;
    }

    @Override
    public void messageReceived(final IoSession session, final Object message) {
        m_log.debug("Received message: {}", message);

        if (session.isClosing() || !session.isConnected()) {
            m_log.debug("Ignoring message on closing session...");
            return;
        }
        final StunMessage stunMessage = (StunMessage) message;

        // The visitor will handle the particular message type, allowing for
        // variation between, for example, client and server visitor
        // implementations.
        final StunMessageVisitor visitor = this.m_visitorFactory
                .createVisitor(session);

        m_log.debug("Sending message to visitor: {}", visitor);
        stunMessage.accept(visitor);
    }

    @Override
    public void exceptionCaught(final IoSession session, final Throwable cause) {
        m_log.debug("Exception on STUN IoHandler", cause);
        if (cause instanceof PortUnreachableException) {
            // We pretend it's like an ordinary STUN "message" and visit it.
            // We allow the processing classes to close the session as they
            // see fit.
            //
            // This will occur relatively frequently over the course of normal
            // STUN checks for UDP.
            final ConnectErrorStunMessage icmpError = new ConnectErrorStunMessage();
            messageReceived(session, icmpError);
        } else {
            m_log.warn("Exception on STUN IoHandler for session: "+session, cause);
            session.close();
        }
    }

    @Override
    public void sessionCreated(final IoSession session) throws Exception {
        SessionUtil.initialize(session);

        // The idle time is in seconds. If there's been no traffic in either
        // direction for awhile, we free the connection.
        session.setIdleTime(IdleStatus.BOTH_IDLE, 100);
    }

    @Override
    public void sessionIdle(final IoSession session, final IdleStatus status) {
        m_log.debug("Killing idle session");
        // Kill idle sessions.
        session.close();
    }
}

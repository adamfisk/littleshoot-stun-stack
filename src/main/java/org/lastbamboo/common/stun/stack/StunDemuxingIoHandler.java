package org.lastbamboo.common.stun.stack;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IoHandler} that allows STUN to be demultiplexed with other protocols. 
 */
public class StunDemuxingIoHandler implements IoHandler
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final Class m_clazz;
    private final IoHandler m_ioHandler;
    private final IoHandler m_stunIoHandler;

    /**
     * Creates a new {@link IoHandler} that demultiplexes encoded and decoded
     * messages between STUN and another protocol.
     * 
     * @param clazz The message class for the other protocol.
     * @param handler The {@link IoHandler} for the other protocol.
     * @param stunIoHandler The {@link IoHandler} for STUN messages.
     */
    public StunDemuxingIoHandler(final Class clazz, final IoHandler handler,
        final IoHandler stunIoHandler)
        {
        m_clazz = clazz;
        m_ioHandler = handler;
        m_stunIoHandler = stunIoHandler;
        }
    
    public void exceptionCaught(final IoSession session, final Throwable cause)
        throws Exception
        {
        m_log.warn("Exception caught", cause);
        this.m_stunIoHandler.exceptionCaught(session, cause);
        this.m_ioHandler.exceptionCaught(session, cause);
        }

    public void messageReceived(final IoSession session, final Object message)
        throws Exception
        {
        m_log.debug("Received message: {}", message);
        final IoHandler handler = getHandlerForMessage(message);
        if (handler != null)
            {
            handler.messageReceived(session, message);
            }
        }

    public void messageSent(final IoSession session, final Object message) 
        throws Exception
        {
        m_log.debug("Sent message: {}", message);
        final IoHandler handler = getHandlerForMessage(message);
        if (handler != null)
            {
            handler.messageSent(session, message);
            }
        }

    private IoHandler getHandlerForMessage(final Object message)
        {
        if (StunMessage.class.isAssignableFrom(message.getClass()))
            {
            return this.m_stunIoHandler;
            }
        else if (this.m_clazz.isAssignableFrom(message.getClass()))
            {
            return this.m_ioHandler;
            }
        else
            {
            m_log.warn("Could not find IoHandler for message: {}", message);
            return null;
            }
        }

    public void sessionClosed(final IoSession session) throws Exception
        {
        this.m_stunIoHandler.sessionClosed(session);
        this.m_ioHandler.sessionClosed(session);
        }

    public void sessionCreated(final IoSession session) throws Exception
        {
        this.m_stunIoHandler.sessionCreated(session);
        this.m_ioHandler.sessionCreated(session);
        }

    public void sessionIdle(final IoSession session, final IdleStatus status)
        throws Exception
        {
        this.m_stunIoHandler.sessionIdle(session, status);
        this.m_ioHandler.sessionIdle(session, status);
        }

    public void sessionOpened(IoSession session) throws Exception
        {
        this.m_stunIoHandler.sessionOpened(session);
        this.m_ioHandler.sessionOpened(session);
        }

    }

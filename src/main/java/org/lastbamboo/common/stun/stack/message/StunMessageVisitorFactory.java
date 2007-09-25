package org.lastbamboo.common.stun.stack.message;

import org.apache.mina.common.IoSession;
import org.apache.mina.handler.StreamIoHandler;

/**
 * Factory for creating STUN message visitors.  Implementing classes might
 * include a factory for the server and a factory for the client, for example.
 * 
 * @param <T> The return type of visitor methods.
 */
public interface StunMessageVisitorFactory<T>
    {

    /**
     * Creates a new visitor.
     * 
     * @param session The {@link IoSession} for reading or writing any necessary
     * data.
     * @return The new visitor.
     */
    StunMessageVisitor<T> createVisitor(IoSession session);

    }

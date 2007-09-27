package org.lastbamboo.common.stun.stack.message;

import org.apache.mina.common.IoSession;

/**
 * Factory for creating STUN message visitors.  Implementing classes might
 * include a factory for the server and a factory for the client, for example.
 * 
 * @param <T> The type visitors return.
 * @param <Z> The type of any attached helper class. 
 */
public interface StunMessageVisitorFactory<T, Z>
    {

    /**
     * Creates a new visitor.
     * 
     * @param session The {@link IoSession} for reading or writing any necessary
     * data.
     * @return The new visitor.
     */
    StunMessageVisitor<T> createVisitor(IoSession session);
    
    /**
     * Creates a new visitor.
     * 
     * @param session The {@link IoSession} for reading or writing any necessary
     * data.
     * @param attachment The attached helper class.
     * @return The new visitor.
     */
    StunMessageVisitor<T> createVisitor(IoSession session, Z attachment);

    }

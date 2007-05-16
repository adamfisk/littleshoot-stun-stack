package net.java.stun4j;

import java.util.EventObject;

import net.java.stun4j.message.Message;


/**
 * The class is used to dispatch incoming stun messages. Apart from the m_message
 * itself one could also obtain the address from where the m_message is coming
 * (used by a server implementation to determine the mapped address)
 * as well as the Descriptor of the NetAccessPoint that received it (In case the
 * stack is used on more than one ports/addresses).
 *
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */

public final class StunMessageEvent extends EventObject
    {

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 3834031350834607668L;

    /**
     * The m_message itself.
     */
    private final Message m_message;

    /**
     * The sending address.
     */
    private final StunAddress m_remoteAddress;

    /**
     * Constructs a StunMessageEvent according to the specified message.
     * @param source The access point that sent the message.
     * @param message The message itself.
     * @param remoteAddress the address that sent the message
     */
    public StunMessageEvent(final Object src, final Message message, 
        final StunAddress remoteAddress)
        {
        super(src);
        this.m_message = message;
        this.m_remoteAddress  = remoteAddress;
        }

    /**
     * Returns the m_message being dispatched.
     * @return the m_message that caused the event.
     */
    public Message getMessage()
        {
        return m_message;
        }

    /**
     * Returns the address that sent the m_message.
     * @return the address that sent the m_message.
     */
    public StunAddress getRemoteAddress()
        {
        return m_remoteAddress;
        }
    }   
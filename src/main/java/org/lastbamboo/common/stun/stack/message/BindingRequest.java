package org.lastbamboo.common.stun.stack.message;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A STUN Binding message.
 */
public class BindingRequest extends AbstractStunMessage 
    implements VisitableStunMessage
    {

    private static final Log LOG = LogFactory.getLog(BindingRequest.class);
    
    /**
     * Creates a new STUN binding message.
     * 
     * @param id The message's transaction ID.
     */
    public BindingRequest(final UUID id)
        {
        super(id, StunMessageType.BINDING_REQUEST);
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Building binding message");
            }
        }

    /**
     * Creates a new binding request from scratch.
     */
    public BindingRequest()
        {
        super(UUID.randomUUID(), StunMessageType.BINDING_REQUEST);
        }

    public void accept(final StunMessageVisitor visitor)
        {
        visitor.visitBindingRequest(this);
        }

    }

package org.lastbamboo.common.stun.stack.message;

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
     * @param transactionIdBytes The message's transaction ID.
     */
    public BindingRequest(final byte[] transactionIdBytes)
        {
        super(transactionIdBytes, 0x0001);
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Building binding message");
            }
        }

    public void accept(final StunMessageVisitor visitor)
        {
        visitor.visitBindingRequest(this);
        }

    }

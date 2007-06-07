package org.lastbamboo.common.stun.stack.message.turn;

import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.AbstractStunMessage;
import org.lastbamboo.common.stun.stack.message.StunMessageType;
import org.lastbamboo.common.stun.stack.message.StunMessageVisitor;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;

/**
 * Creates a new response to an allocate request.  This includes the mapped
 * address the server has allocated to proxy data to the TURN client.
 */
public final class SuccessfulAllocateResponse extends AbstractStunMessage
    {

    /**
     * Creates a new response to an allocate request.
     * 
     * @param transactionId The ID of the transaction, matching the ID of the
     * request.
     * @param attributes The message attributes.
     */
    public SuccessfulAllocateResponse(final UUID transactionId, 
        final Map<Integer, StunAttribute> attributes)
        {
        super(transactionId, StunMessageType.SUCCESSFUL_ALLOCATE_RESPONSE,
            attributes);
        }

    public void accept(final StunMessageVisitor visitor)
        {
        visitor.visitSuccessfulAllocateResponse(this);
        }

    }

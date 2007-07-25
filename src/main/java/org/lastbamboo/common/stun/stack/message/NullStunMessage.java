package org.lastbamboo.common.stun.stack.message;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * Placeholder class that forces callers to handle cases such as when there
 * is no response to a request. 
 */
public class NullStunMessage implements StunMessage, VisitableStunMessage
    {

    public Map<StunAttributeType, StunAttribute> getAttributes()
        {
        return Collections.emptyMap();
        }

    public int getBodyLength()
        {
        return 0;
        }

    public int getTotalLength()
        {
        return 0;
        }

    public UUID getTransactionId()
        {
        return UUID.randomUUID();
        }

    public int getType()
        {
        return Integer.MAX_VALUE;
        }

    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitNullMessage(this);
        }

    }

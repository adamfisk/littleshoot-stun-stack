package org.lastbamboo.common.stun.stack.message;

import java.util.UUID;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.util.BitUtils;

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
        super(transactionIdBytes, StunMessageType.BINDING_REQUEST);
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
        super(createTransactionId(), StunMessageType.BINDING_REQUEST);
        }

    private static byte[] createTransactionId()
        {
        final UUID id = UUID.randomUUID();
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Generated UUID: "+id);
            }
        
        final long mostSig = id.getMostSignificantBits();
        final long leastSig = id.getLeastSignificantBits();
        
        final byte[] idBytes0 = BitUtils.toByteArray(mostSig);
        final byte[] idBytes1 = BitUtils.toByteArray(leastSig);
        
        final byte[] allBytes = ArrayUtils.addAll(idBytes0, idBytes1);
        
        // Return the first 12 bytes.
        return ArrayUtils.subarray(allBytes, 0, 12);
        }

    public void accept(final StunMessageVisitor visitor)
        {
        visitor.visitBindingRequest(this);
        }

    }

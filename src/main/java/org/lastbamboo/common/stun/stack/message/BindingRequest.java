package org.lastbamboo.common.stun.stack.message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttributeType;

/**
 * A STUN Binding message.
 */
public class BindingRequest extends AbstractStunMessage 
    {

    private static final Log LOG = LogFactory.getLog(BindingRequest.class);
    
    /**
     * Creates a new STUN binding message.
     * 
     * @param id The message's transaction ID.
     * @param attributes Additional Binding Request attributes, typically 
     * attributes associated with a particular STUN usage.
     */
    public BindingRequest(final UUID id, 
        final Map<StunAttributeType, StunAttribute> attributes)
        {
        super(id, StunMessageType.BINDING_REQUEST, attributes);
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

    /**
     * Creates a new Binding Request with the specified attributes.
     * 
     * @param attributes Additional Binding Request attributes, typically 
     * attributes associated with a particular STUN usage.
     */
    public BindingRequest(final Collection<StunAttribute> attributes)
        {
        super(UUID.randomUUID(), StunMessageType.BINDING_REQUEST, 
            createAttributes(attributes));
        }

    private static Map<StunAttributeType, StunAttribute> createAttributes(
        final Collection<StunAttribute> attributes)
        {
        final Map<StunAttributeType, StunAttribute> attributesMap = 
            new HashMap<StunAttributeType, StunAttribute>();
        
        for (final StunAttribute attribute : attributes)
            {
            attributesMap.put(attribute.getAttributeType(), attribute);
            }
        return attributesMap;
        }

    public <T> T accept(final StunMessageVisitor<T> visitor)
        {
        return visitor.visitBindingRequest(this);
        }

    }

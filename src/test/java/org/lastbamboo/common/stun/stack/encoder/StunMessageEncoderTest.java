package org.lastbamboo.common.stun.stack.encoder;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.stun.stack.message.BindingRequest;
import org.lastbamboo.common.stun.stack.message.attributes.StunAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IceControlledAttribute;
import org.lastbamboo.common.stun.stack.message.attributes.ice.IcePriorityAttribute;

public class StunMessageEncoderTest extends TestCase
    {

    public void testEncode()
        {
        final StunMessageEncoder encoder = new StunMessageEncoder();

    
        // Now send a BindingRequest with PRIORITY, USE-CANDIDATE,
        // ICE-CONTROLLING etc.
        final Collection<StunAttribute> attributes = 
            new LinkedList<StunAttribute>();

        final long priority = 427972L;

        final IcePriorityAttribute priorityAttribute = new IcePriorityAttribute(
                priority);

        // The agent uses the same tie-breaker throughout the session.
        final byte[] tieBreaker = 
            new BigInteger(64, new Random()).toByteArray();
        
        final StunAttribute controlling =
            new IceControlledAttribute(tieBreaker);
            
        attributes.add(priorityAttribute);
        attributes.add(controlling);

        // TODO: Add CREDENTIALS attribute.
        final BindingRequest request = new BindingRequest(attributes);
        
        // Make sure no exceptions are thrown.
        final ByteBuffer encoded = encoder.encode(request);
        
        
        
        }

    }

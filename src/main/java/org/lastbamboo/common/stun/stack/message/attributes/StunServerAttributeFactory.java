package org.lastbamboo.common.stun.stack.message.attributes;

import java.io.IOException;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating the STUN SERVER attribute.  The SERVER attribute is
 * just a variable length text attribute describing the server.
 */
public class StunServerAttributeFactory implements StunAttributeFactory
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(StunServerAttributeFactory.class);

    public StunAttribute createAttribute(final ByteBuffer body) 
        throws IOException
        {
        final String serverText = MinaUtils.toAsciiString(body);
        LOG.debug("Got server text: {}", serverText);
        return new StunServerAttribute(body.capacity(), serverText);
        }

    }

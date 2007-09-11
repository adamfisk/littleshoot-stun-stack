package org.lastbamboo.common.stun.stack;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.stun.stack.decoder.StunMessageDecodingState;
import org.lastbamboo.common.stun.stack.encoder.StunProtocolEncoder;
import org.lastbamboo.common.stun.stack.message.StunMessage;
import org.lastbamboo.common.util.mina.DecodingStateMachine;
import org.lastbamboo.common.util.mina.DemuxableProtocolCodecFactory;
import org.lastbamboo.common.util.mina.DemuxableProtocolDecoder;
import org.lastbamboo.common.util.mina.DemuxingStateMachineProtocolDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link DemuxableProtocolCodecFactory} for STUN.
 */
public class StunDemuxableProtocolCodecFactory 
    implements DemuxableProtocolCodecFactory<StunMessage>
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    public boolean canDecode(ByteBuffer in)
        {
        final int pos = in.position();
        final int limit = in.limit();
        try
            {
            final int firstByte = in.getUnsigned();
            
            // The first 2 bits of STUN messages are always zero.
            final int masked = firstByte & 0xc0;
            if (masked > 0)
                {
                return false;
                }
            else
                {
                // OK, it could be a STUN message.  Let's check the 
                // STUN magic cookie field to make sure.
                final long magicCookie = 0x2112A442;
                final long secondFourBytes = in.getUnsignedInt(pos + 4);

                final boolean magicCookieMatches = 
                    secondFourBytes == magicCookie;
                
                m_log.debug("Magic cookie matches: "+
                    magicCookieMatches);
                return magicCookieMatches;
                }
            }
        finally
            {
            // Make sure we reset the buffer!
            in.position(pos);
            in.limit(limit);
            }
        }

    public Class<StunMessage> getClassToEncode()
        {
        return StunMessage.class;
        }

    public DemuxableProtocolDecoder newDecoder()
        {
        final DecodingStateMachine startState = 
            new StunMessageDecodingState();
        return new DemuxingStateMachineProtocolDecoder(startState);
        }

    public ProtocolEncoder newEncoder()
        {
        return new StunProtocolEncoder();
        }

    }

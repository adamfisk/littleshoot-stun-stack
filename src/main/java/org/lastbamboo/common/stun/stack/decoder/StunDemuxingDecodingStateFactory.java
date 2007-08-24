package org.lastbamboo.common.stun.stack.decoder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.util.mina.DecodingState;
import org.lastbamboo.common.util.mina.DecodingStateFactory;

/**
 * Decoding state factory for a state that can differentiate between STUN
 * messages and messages from another protocol.
 */
public class StunDemuxingDecodingStateFactory 
    implements DecodingStateFactory
    {
    
    private final DecodingStateFactory m_decodingStateFactory;

    /**
     * Creates a new factory for a state that differentiates between STUN
     * messages and messages from another protocol.
     * 
     * @param decodingStateFactory The factory for creating the initial 
     * reading state for the other non-STUN protocol.
     */
    public StunDemuxingDecodingStateFactory(
        final DecodingStateFactory decodingStateFactory)
        {
        m_decodingStateFactory = decodingStateFactory;
        }
    
    public DecodingState newState()
        {
        return new StunDemultiplexingDecodingState(this.m_decodingStateFactory);
        }

    private static final class StunDemultiplexingDecodingState 
        implements DecodingState
        {
        private final DecodingStateFactory m_stateFactory;

        private StunDemultiplexingDecodingState(
            final DecodingStateFactory stateFactory)
            {
            m_stateFactory = stateFactory;
            }

        public DecodingState decode(final ByteBuffer in, 
            final ProtocolDecoderOutput out) throws Exception
            {
            in.mark();
            final int firstByte = in.getUnsigned();
            
            // Reset the buffer position.
            in.reset();
            final int masked = firstByte & 0xc0;
            if (masked > 0)
                {
                return this.m_stateFactory.newState();
                }
            else
                {
                //return new ReadMessageType();
                return null;
                }
            }
        }
    }

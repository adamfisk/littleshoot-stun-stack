package org.lastbamboo.common.stun.stack.decoder;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.stun.stack.encoder.StunProtocolEncoder;
import org.lastbamboo.common.util.mina.StateMachineProtocolDecoder;

/**
 * Codec factory for creating STUN encoders and decoders.  Note this creates
 * a new encoder and a new decoder with each call rather than storing 
 * encoder and decoder instances.  This ultimately results in each session
 * having its own encoder and decoder.
 */
public class StunProtocolCodecFactory implements ProtocolCodecFactory
    {

    public ProtocolDecoder getDecoder() throws Exception
        {
        final StunMessageDecodingState topLevelState = 
            new StunMessageDecodingState();
        return new StateMachineProtocolDecoder(topLevelState);
        }

    public ProtocolEncoder getEncoder() throws Exception
        {
        return new StunProtocolEncoder();
        }

    }

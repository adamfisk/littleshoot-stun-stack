package org.littleshoot.stun.stack;

import org.littleshoot.stun.stack.decoder.StunMessageDecodingState;
import org.littleshoot.util.mina.StateMachineProtocolDecoder;

/**
 * Decoder for STUN messages
 */
public class StunMessageDecoder extends StateMachineProtocolDecoder {

    /**
     * Creates a new decoder.
     */
    public StunMessageDecoder() {
        super(new StunMessageDecodingState());
    }

}

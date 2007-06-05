package org.lastbamboo.common.stun.stack.message;

/**
 * STUN attribute types.  These are converted to the full values including
 * the class bits for ease of use.
 */
public class StunMessageType
    {
    
    private StunMessageType()
        {
        // Disable construction.
        }

    /**
     * Binding request type ID.
     */
    public static final int BINDING_REQUEST = 0x0001;
    

    /**
     * Binding response type ID.
     */
    public static final int SUCCESSFUL_BINDING_RESPONSE = 0x0101;
    
    /**
     * Shared secret message type ID.
     */
    //public static final int SHARED_SECRET = 0x002;

    // TODO: draft-ietf-behave-turn-03.txt has a conflict between the 
    // indication message IDs and the STUN message IDs. Should be fixed with
    // draft-ietf-behave-turn-04.txt
    }

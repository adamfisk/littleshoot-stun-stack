package net.java.stun4j.client;

import java.net.InetSocketAddress;

/**
 * Interface for the receipt of the MAPPED-ADDRESS attribute in STUN
 * "Binding Response" messages.
 */
public interface MappedAddressListener
    {

    /**
     * Called when the STUN client has received the MAPPED-ADDRESS attribute
     * in a STUN "Binding Response" message.
     * @param mappedAddress The MAPPED-ADDRESS of the client.  This is the
     * address of the client from the perspective of the STUN server --
     * the address the server read the request from.
     */
    void mappedAddress(final InetSocketAddress mappedAddress);
    }

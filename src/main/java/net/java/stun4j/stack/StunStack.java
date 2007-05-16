/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * Portions of this software are based upon public domain software
 * originally written at the National Center for Supercomputing Applications,
 * University of Illinois, Urbana-Champaign.
 */

package net.java.stun4j.stack;

import java.net.DatagramSocket;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.StunException;

/**
 * The entry point to the Stun4J stack. The class is used to start, stop and
 * configure the stack.
 * 
 * <p>
 * Organisation: Louis Pasteur University, Strasbourg, France
 * </p>
 * <p>
 * Network Research Team (http://www-r2.u-strasbg.fr)
 * </p>
 * </p>
 * 
 * @author Emil Ivov
 * @version 0.1
 */
public final class StunStack
    {

    private final StunProvider m_stunProvider = new StunProvider(this);
    
    /**
     * Our network gateway.
     */
    private final NetAccessManager m_netAccessManager = 
        new NetAccessManager(m_stunProvider);

    /**
     * Creates and starts the specified Network Access Point.
     * 
     * @param apDescriptor A descriptor containing the address and port of 
     * the STUN server that the newly created access point will communicate
     * with.
     * @throws StunException <p>
     *  NETWORK_ERROR if we fail to create or bind the datagram socket.
     *  </p>
     *  <p>
     *  ILLEGAL_STATE if the stack had not been started.
     *  </p>
     */
    public void installNetAccessPoint(
        final NetAccessPointDescriptor apDescriptor)
        throws StunException
        {
        m_netAccessManager.installNetAccessPoint(apDescriptor);
        }

    /**
     * Creates and starts the specified Network Access Point.
     * 
     * @param apDescriptor A descriptor containing the address and port of the 
     * STUN server that the newly created access point will communicate with.
     * @throws StunException NETWORK_ERROR if we fail to create or bind the 
     * datagram socket. ILLEGAL_STATE if the stack had not been started.
     */
    public void installNetAccessPoint(
        final NetAccessPointDescriptor apDescriptor, final DatagramSocket sock) 
        throws StunException
        {
        m_netAccessManager.installNetAccessPoint(apDescriptor, sock);
        }

    /**
     * Creates and starts the specified Network Access Point based on the
     * specified socket and returns a relevant descriptor.
     * 
     * @param sock The socket that the new access point should represent.
     * @throws StunException NETWORK_ERROR if we fail to create or bind the 
     * datagram socket. ILLEGAL_STATE if the stack had not been started.
     * @return a descriptor of the newly created access point.
     */
    public NetAccessPointDescriptor installNetAccessPoint(
        final DatagramSocket sock) throws StunException
        {
        return m_netAccessManager.installNetAccessPoint(sock);
        }

    /**
     * Stops and deletes the specified access point.
     * 
     * @param apDescriptor The access point to remove
     */
    public void removeNetAccessPoint(
        final NetAccessPointDescriptor apDescriptor) 
        {
        m_netAccessManager.removeNetAccessPoint(apDescriptor);
        }

    /**
     * Returns a StunProvider instance to be used for sending and receiving
     * mesages.
     * 
     * @return an instance of StunProvider
     */
    public StunProvider getProvider()
        {
        return m_stunProvider;
        }

    /**
     * Returns the currently active instance of NetAccessManager.
     * 
     * @return the currently active instance of NetAccessManager.
     */
    NetAccessManager getNetAccessManager()
        {
        return m_netAccessManager;
        }

    }

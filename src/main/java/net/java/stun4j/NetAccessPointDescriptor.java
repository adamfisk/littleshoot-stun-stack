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

package net.java.stun4j;
import java.net.InetSocketAddress;
import java.net.*;

/**
 * The class is used to represent an [address]:[port] couple where the Stun4J
 * stack is to listen for incoming messages. We need such a class so that we
 * could identify and manage (add, remove, etc.) DatagramListeners.
 *
 * <p>Organisation: Louis Pasteur University, Strasbourg, France</p>
 * 					<p>Network Research Team (http://www-r2.u-strasbg.fr)</p>
 * @author Emil Ivov
 * @version 0.1
 */

public class NetAccessPointDescriptor
{

    /**
     * The address string representing the host (interface) where the stack
     * should bind.
     */
    protected StunAddress stunAddr = null;


    /**
     * Creates a net access point that will bind to the  specified address.
     *
     * @param address a valid Stun Address.
     */
    public NetAccessPointDescriptor(StunAddress address)
    {
        stunAddr = address;
    }

    /**
     * Attempts to automatically detect the address of local host and binds
     * on the specified port.
     *
     * @param port the port where to bind.
     */
/*    public NetAccessPointDescriptor(int port)
    {
        stunAddr = new StunAddress(port);
    }*/

    /**
     * Compares this object against the specified object. The result is true if
     * and only if the argument is not null and it represents the same address
     * as this object.
     *
     * Two instances of InetSocketAddress represent the same address if both the
     * InetAddresses (or hostnames if it is unresolved) and port numbers are
     * equal. If both addresses are unresolved, then the hostname & the port
     * number are compared.
     *
     * @param obj the object to compare against.
     * @return true if the objects are the same; false otherwise.
     */
    public final boolean equals(Object obj)
        {
        if(obj == null
           || !(obj instanceof NetAccessPointDescriptor))
           return false;

        if (obj == this)
            return true;

        return stunAddr.equals( ((NetAccessPointDescriptor)obj).stunAddr );
        }
    
    public int hashCode()
        {
        return 17 * stunAddr.hashCode();
        }

    /**
     * Returns the socket address wrapped by this class.
     * @return an InetSocketAddress instance.
     */
    public StunAddress getAddress()
        {
        return stunAddr;
        }

    /**
     * Clones the NetAccessPointDescriptor.
     *
     * @return a copy of this NetAccessPointDescriptor.
     */
    public Object clone()
        {
        NetAccessPointDescriptor napd = new NetAccessPointDescriptor(stunAddr);

        return napd;
        }
}

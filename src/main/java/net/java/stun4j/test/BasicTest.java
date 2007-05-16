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
package net.java.stun4j.test;

import java.net.*;
import java.io.IOException;
import java.net.SocketAddress;

/**
 * <p>Title: Stun4J</p>
 * <p>Description: Simple Traversal of UDP Through NAT</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organisation: ULP</p>
 * @author Emil Ivov
 * @version 0.1
 */

public class BasicTest
{
//    String stunSerAddrStr =  "larry.gloo.net";
    String stunSerAddrStr =  "stun01.sipphone.com";
    DatagramSocket sock   =  null;
    private byte[] bindingRequest =
        {
          //STUN Msg Type  |  Msg Length
            0x00, 0x01,      0x00, 0x08,
          // Transaction ID
            0x01, 0x02,      0x03, 0x04,
            0x05, 0x06,      0x07, 0x08,
            0x09, 0x10,      0x11, 0x12,
            0x13, 0x14,      0x15, 0x16,
          //ATTRIBUTES,
          //Change Request
            0x00, 0x03,      0x00, 0x04,
            0x00, 0x00,      0x00, 0x00
        };

    private byte[] wrongBindingRequest =
        {
            //STUN Msg Type  |  Msg Length
            0x00, 0x01,      0x00, 0x07,
            // Transaction ID
            0x01, 0x02,      0x03, 0x04,
            0x05, 0x06,      0x07, 0x08,
            0x09, 0x10,      0x11, 0x12,
            0x13, 0x14,      0x15, 0x16,
            //ATTRIBUTES,
            //Change Request
            0x00, 0x03,      0x00, 0x04,
            0x00, 0x00,      0x00, 0x06
        };


    public BasicTest()
    {
    }

    public void sendBindingRequest()
    {
        try
        {
            SocketAddress stunSerAddr = new InetSocketAddress(stunSerAddrStr, 3478);


            DatagramPacket packet = new DatagramPacket(bindingRequest, 28, stunSerAddr);
            sock = new DatagramSocket();

            sock.send(packet);
        }
        catch (SocketException ex)
        {
            System.err.println("Failed to open a socket to " + stunSerAddrStr
                               + ". " + ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            System.err.println("Failed to send the binding request to " + stunSerAddrStr
                               + ". " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void sendWrongBindingRequest()
    {
        try
        {
            SocketAddress stunSerAddr = new InetSocketAddress(stunSerAddrStr, 3478);


            DatagramPacket packet = new DatagramPacket(wrongBindingRequest, 28, stunSerAddr);
            sock = new DatagramSocket();

            sock.send(packet);
        }
        catch (SocketException ex)
        {
            System.err.println("Failed to open a socket to " + stunSerAddrStr
                               + ". " + ex.getMessage());
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            System.err.println("Failed to send the binding request to " + stunSerAddrStr
                               + ". " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public void receiveBindingResponse()
    {
        byte responseData[] = new byte[512];
        DatagramPacket responsePacket = new DatagramPacket(responseData, 512);
        try
        {
            sock.receive(responsePacket);
        }
        catch (IOException ex)
        {
            System.err.println("Failed to receive a packet! " + ex.getMessage());
        }

        //decode
        //for(int i = 0; i < responsePacket.getLength(); i++)
        //    System.out.print("0x" + byteToHex(responseData[i]) + " ");
        System.out.println("====================== Stun Header =============================");
        System.out.println("STUN Message Type: 0x" + byteToHex(responseData[0]) + byteToHex(responseData[1]));
        System.out.println("Message Length:    0x" + byteToHex(responseData[2]) + byteToHex(responseData[3]));
        System.out.println("Transaction ID:    0x" + byteToHex(responseData[4]) + byteToHex(responseData[5])
                                                   + byteToHex(responseData[6]) + byteToHex(responseData[7])
                                                   + byteToHex(responseData[8]) + byteToHex(responseData[9])
                                                   + byteToHex(responseData[10]) + byteToHex(responseData[11])
                                                   + byteToHex(responseData[12]) + byteToHex(responseData[13])
                                                   + byteToHex(responseData[14]) + byteToHex(responseData[15])
                                                   + byteToHex(responseData[16]) + byteToHex(responseData[17])
                                                   + byteToHex(responseData[18]) + byteToHex(responseData[19])
                                                   );
        System.out.println("====================== Attributes ==============================");
        for (int i = 20; i < responsePacket.getLength(); )
        {
            byte attLen1 = 0;
            byte attLen2 = 0;
            System.out.println("Attribute Type:   0x" + byteToHex(responseData[i++]) + byteToHex(responseData[i++]));
            System.out.println("Attribute Length: 0x" + byteToHex(attLen2=responseData[i++]) + byteToHex(attLen2=responseData[i++]));
            int attLen = (((int)attLen1)<<8) + attLen2;
            for (int j = 0; j < attLen; j++)
            {
                System.out.println("    data["+j+"]="+(responseData[j+i]&0xFF));
            }
            i+=attLen;
        }
    }

    private String byteToHex(byte b)
    {
        return (b<0xF?"0":"") + Integer.toHexString(b&0xff).toUpperCase();
    }

    public static void main(String[] args)
    {
        BasicTest basicTest = new BasicTest();
//        basicTest.sendBindingRequest();
//        basicTest.receiveBindingResponse();
        basicTest.sendWrongBindingRequest();
        basicTest.receiveBindingResponse();
    }

}
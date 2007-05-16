package net.java.stun4j.client;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import junit.framework.TestCase;

import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Request;
import net.java.stun4j.stack.TransactionID;


public class UdpSocketTest extends TestCase
    {

    public void testSimpleUdpConnection() throws Exception
        {
        final Request request = MessageFactory.createBindingRequest();
        final TransactionID transactionID = TransactionID.createTransactionID();
        request.setTransactionID(transactionID.getTransactionID());
        final byte[] buffer = request.encode();
        final SocketAddress sa = new InetSocketAddress("stun01.sipphone.com", 3478);
        final DatagramSocket client = new DatagramSocket(4798);
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length, sa);
        
        //client.connect(sa);
        client.send(packet);
        
        System.out.println("Finished sending packet...");
        }
    }

package net.java.stun4j.stack;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Arrays;

import net.java.stun4j.MsgFixture;
import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.ResponseCollector;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Request;
import net.java.stun4j.message.Response;

import org.lastbamboo.common.util.NetworkUtils;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * All unit stack tests should be provided later. I just don't have the time now.
 *
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
public class ShallowStackTest extends 
    AbstractDependencyInjectionSpringContextTests
    {
    private StunProvider stunProvider;
    private StunStack stunStack;
    private MsgFixture msgFixture;

    private StunAddress stun4jAddressOfDummyImpl;
    private InetSocketAddress socketAddressOfStun4jStack;

    private DatagramCollector dgramCollector = new DatagramCollector();

    private NetAccessPointDescriptor apDescriptor;

    private DatagramSocket dummyImplSocket;

    protected String[] getConfigLocations()
        {
        return new String[] {"stunBeans.xml"};
        }
    
    protected void onSetUp() throws Exception
        {
        msgFixture = new MsgFixture(this);
        msgFixture.setUp();
        //Addresses
        stun4jAddressOfDummyImpl = 
            new StunAddress(NetworkUtils.getLocalHost(), 6000);
        socketAddressOfStun4jStack = 
            new InetSocketAddress(NetworkUtils.getLocalHost(), 5000);

        //init the stack
        stunStack = (StunStack) applicationContext.getBean("stun-stack");

        stunProvider = stunStack.getProvider();

         //access point
        apDescriptor = new NetAccessPointDescriptor(
            new StunAddress(NetworkUtils.getLocalHost(), 5000));

        stunStack.installNetAccessPoint(apDescriptor);

        //init the phoney stack
        dummyImplSocket = new DatagramSocket( 6000 );
        }

    protected void onTearDown() throws Exception
        {
        msgFixture.tearDown();

        //stunStack.shutDown();
        dummyImplSocket.close();

        msgFixture = null;
        }

    /**
     * Sends a byte array containing a bindingRequest, through a datagram socket
     * and verifies that the stack receives it alright.
     *
     * @throws java.lang.Exception if we fail
     */
    /*
    public void testReceiveRequest() throws Exception
        {
        final SimpleRequestCollector requestCollector = 
            new SimpleRequestCollector();
        stunProvider.setRequestListener(requestCollector);

        dummyImplSocket.send(new DatagramPacket(msgFixture.bindingRequest,
            msgFixture.bindingRequest.length, socketAddressOfStun4jStack));

        //wait for the packet to arrive
        try{ Thread.sleep(500); }catch (InterruptedException ex){}

        final Request collectedRequest = requestCollector.collectedRequest;

        final byte expectedReturn[] = msgFixture.bindingRequest;
        final byte actualReturn[] = collectedRequest.encode();
        assertTrue("Received request was not the same as the one that was sent",
                   Arrays.equals(expectedReturn, actualReturn));
        }
    
    /**
     * Sends a binding request using the stack to a phoney socket, and verifies
     * that it is received and that the contents of the datagram corresponds to
     * the request that was sent.
     *
     * @throws java.lang.Exception if we fail
     */
    public void testSendRequest() throws Exception
        {
        final Request bindingRequest = MessageFactory.createBindingRequest();

        dgramCollector.startListening(dummyImplSocket);

        stunProvider.sendRequest(bindingRequest, 
            stun4jAddressOfDummyImpl, apDescriptor,
            new SimpleResponseCollector());

        //wait for its arrival
        try{ Thread.sleep(500); }catch (InterruptedException ex){}

        DatagramPacket receivedPacket = dgramCollector.collectPacket();

        assertTrue("The stack did not properly send a Binding Request",
                   (receivedPacket.getLength() > 0));

        Request receivedRequest =
            (Request)Request.decode(receivedPacket.getData(), (char)0,
                (char)receivedPacket.getLength());
        assertEquals("The received request did not match the one that was sent.",
            bindingRequest, //expected
            receivedRequest); // actual

        //wait for retransmissions

        dgramCollector.startListening(dummyImplSocket);

        try{ Thread.sleep(1000); }catch (InterruptedException ex){}

        receivedPacket = dgramCollector.collectPacket();

        assertTrue("The stack did not retransmit a Binding Request",
                   (receivedPacket.getLength() > 0));

        receivedRequest = (Request)Request.decode(receivedPacket.getData(),
            (char)0, (char)receivedPacket.getLength());
        assertEquals("The retransmitted request did not match the original.",
                     bindingRequest, //expected
                     receivedRequest); // actual
        }


    /**
     * Sends a byte array containing a bindingRequest, through a datagram socket,
     * verifies that the stack receives it properly and then sends a response
     * using the stack. Finally, the response is expected at the other end and
     * compared with the sent one.
     *
     * @throws java.lang.Exception if we fail
     */
    public void testSendResponse() throws Exception
        {
        //---------- send & receive the request --------------------------------
        final SimpleRequestCollector requestCollector = 
            new SimpleRequestCollector();
        stunProvider.setRequestListener(requestCollector);

        dummyImplSocket.send(new DatagramPacket(msgFixture.bindingRequest,
            msgFixture.bindingRequest.length, socketAddressOfStun4jStack));

        //wait for the packet to arrive
        try{ Thread.sleep(500); }catch (InterruptedException ex){}

        final Request collectedRequest = requestCollector.collectedRequest;

        byte expectedReturn[] = msgFixture.bindingRequest;
        byte actualReturn[]   = collectedRequest.encode();
        assertTrue("Received request was not the same as the one that was sent",
                   Arrays.equals(expectedReturn, actualReturn));

        //---------- create the response ---------------------------------------
        Response bindingResponse = MessageFactory.createBindingResponse(
            new StunAddress( MsgFixture.ADDRESS_ATTRIBUTE_ADDRESS, MsgFixture.ADDRESS_ATTRIBUTE_PORT ),
            new StunAddress( MsgFixture.ADDRESS_ATTRIBUTE_ADDRESS_2, MsgFixture.ADDRESS_ATTRIBUTE_PORT_2),
            new StunAddress( MsgFixture.ADDRESS_ATTRIBUTE_ADDRESS_3, MsgFixture.ADDRESS_ATTRIBUTE_PORT_3));

        //---------- send & receive the response -------------------------------
        dgramCollector.startListening(dummyImplSocket);

        stunProvider.sendResponse(collectedRequest.getTransactionID(),
            bindingResponse, apDescriptor, stun4jAddressOfDummyImpl);

        //wait for its arrival
        try{ Thread.sleep(500); }catch (InterruptedException ex){}

        DatagramPacket receivedPacket = dgramCollector.collectPacket();

        assertTrue("The stack did not properly send a Binding Request",
                   (receivedPacket.getLength() > 0));

        Response receivedResponse =
            (Response) Response.decode(receivedPacket.getData(),
                                       (char) 0,
                                       (char) receivedPacket.getLength());
        assertEquals(
            "The received request did not match the one that was sent.",
            bindingResponse, //expected
            receivedResponse); // actual
        }

    /*
    public void testReceiveResponse()
        throws Exception
    {
        SimpleResponseCollector collector = new SimpleResponseCollector();
        //--------------- send the original request ----------------------------
        Request bindingRequest = MessageFactory.createBindingRequest();

        stunProvider.sendRequest(bindingRequest, stun4jAddressOfDummyImpl, apDescriptor,
                                 collector);

        //wait for its arrival
        try{ Thread.sleep(500); }catch (InterruptedException ex){}

        //create the right response
        byte response[] = new byte[msgFixture.bindingResponse.length];
        System.arraycopy(msgFixture.bindingResponse, 0, response, 0,
                         response.length);

        //Set the valid tid.
        System.arraycopy(bindingRequest.getTransactionID(),
                         0,
                         response,
                         4,
                         16);

        //send the response

        dummyImplSocket.send(new DatagramPacket(response,
                                                response.length,
                                                socketAddressOfStun4jStack));

        //wait for the packet to arrive
        try{ Thread.sleep(500); }catch (InterruptedException ex){}

        Response collectedResponse = collector.collectedResponse;

        byte expectedReturn[] = response;
        byte actualReturn[]   = collectedResponse.encode();
        assertTrue("Received request was not the same as the one that was sent",
                   Arrays.equals(expectedReturn, actualReturn));
    }
    */

    //--------------------------------------- listener implementations ---------
    public class SimpleResponseCollector
        implements ResponseCollector
    {
        Response collectedResponse = null;
        public void processResponse(StunMessageEvent evt)
        {
            collectedResponse = (Response)evt.getMessage();
            System.out.println("Received response.");
        }

        public void processTimeout()
        {
            System.out.println("Timeout");
        }
    }

    private static final class SimpleRequestCollector implements RequestListener
        {
        Request collectedRequest = null;
        public void requestReceived(final StunMessageEvent evt)
            {
            collectedRequest = (Request)evt.getMessage();
            System.out.println("Received request.");
            }
        }
    }

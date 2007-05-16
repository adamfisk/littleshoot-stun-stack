package net.java.stun4j.stack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.StunAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The Network Access Point is the most outward part of the stack. It is
 * constructed around a datagram socket and takes care about forwarding incoming
 * messages to the MessageProcessor as well as sending datagrams to the STUN server
 * specified by the original NetAccessPointDescriptor.
 *
 * <p>Organisation: Louis Pasteur University, Strasbourg, France</p>
 *                   <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
class NetAccessPoint implements Runnable
    {

    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(NetAccessPoint.class);

    /**
     * Max datagram size.
     */
    private static final int MAX_DATAGRAM_SIZE = 4 * 1024;

    /**
     * The message queue is where incoming messages are added.
     */
    private final MessageQueue m_messageQueue;

    /**
     * The socket object that used by this access point to access the network.
     */
    private DatagramSocket sock;

    /**
     * Indicates whether the access point is using a socket that was created
     * by someone else. The variable is set to true when the AP's socket is
     * set using the <code>useExternalSocket()</code> method and is consulted
     * inside the stop method. When its value is true, the AP's socket is not
     * closed when <code>stop()</code>ing the AP.
     *
     * This variable is part of bug fix reported by Dave Stuart - SipQuest
     */
    private boolean isUsingExternalSocket = false;

    /**
     * A flag that is set to false to exit the message processor.
     */
    private volatile boolean isRunning;

    /**
     * The descriptor used to create this access point.
     */
    private NetAccessPointDescriptor m_apDescriptor;

    /**
     * Creates a network access point.
     * @param apDescriptor the address and port where to bind.
     * @param messageQueue the FIFO list where incoming messages should be 
     * queued.
     */
    NetAccessPoint(NetAccessPointDescriptor apDescriptor,
        MessageQueue messageQueue)
        {
        this.m_apDescriptor = apDescriptor;
        this.m_messageQueue = messageQueue;
        }

    /**
     * Start the network listening thread.
     *
     * @throws IOException if we fail to setup the socket.
     */
    void start() throws IOException
        {
        // do not create the socket earlier as someone might want to set an
        // existing one was != null fixed (Ranga)

        LOG.debug("Starting net access point...");
        if (sock == null)
            {
            LOG.trace("datagram address: "
                + getDescriptor().getAddress().getSocketAddress());
            this.sock = new DatagramSocket(getDescriptor().getAddress()
                .getSocketAddress().getPort());
            
            this.sock.setReuseAddress (true);
            this.isUsingExternalSocket = false;
            }
        sock.setReceiveBufferSize(MAX_DATAGRAM_SIZE);
        this.isRunning = true;
        final Thread accessPointThread =
            new Thread(this, "NetAccessPoint-Thread");
        accessPointThread.setDaemon(true);
        accessPointThread.start();
        }

    /**
     * Returns the NetAccessPointDescriptor that contains the port and address
     * associated with this accesspoint.
     * @return the NetAccessPointDescriptor associated with this AP.
     */
    NetAccessPointDescriptor getDescriptor()
        {
        return this.m_apDescriptor;
        }

    /**
     * The listening thread's run method.
     */
    public void run()
        {
        while (this.isRunning)
            {
            try
                {
                final int bufsize = sock.getReceiveBufferSize();
                final byte message[] = new byte[bufsize];
                final DatagramPacket packet =
                    new DatagramPacket(message, bufsize);
                LOG.trace("About to receive packets...");
                LOG.trace ("Using socket: " + sock);
                LOG.trace ("Port before receiving: " + packet.getPort ());
                sock.receive(packet);

                LOG.trace("Received packet!!!!!!!!!!!!!!!!");
                LOG.trace ("Port: " + packet.getPort ());
                LOG.trace ("Length: " + packet.getLength ());
                LOG.trace ("Address: " + packet.getAddress ());

                final RawMessage rawMessage =
                    new RawMessage(message, packet.getLength(),
                        packet.getAddress(), packet.getPort(), getDescriptor());

                m_messageQueue.add(rawMessage);
                }
            catch (final SocketException e)
                {
                if (isRunning)
                    {
                    LOG.debug("Failure on: "+
                        getDescriptor().getAddress().getSocketAddress()+
                        " -- net access point now useless:", e);

                    stop();
                    }
                else
                    {
                    LOG.trace("Not running...");
                    //The exception was most probably caused by calling 
                    // this.stop()
                    }
                }
            catch (final IOException e)
                {
                LOG.debug("Failure on: "+
                    getDescriptor().getAddress().getSocketAddress()+
                    " -- net access point now useless:", e);
                //do not stop the thread;
                }
            catch (final Throwable e)
                {
                LOG.error("Failure on: "+
                    getDescriptor().getAddress().getSocketAddress()+
                    " -- net access point now useless:", e);

                stop();
                }
            }
        }

    /**
     * Shut down the access point. Close the socket for recieving
     * incoming messages. The method want close the socket if it was created
     * outside the stack. (Bug Report - Dave Stuart - SipQuest)
     */
    void stop()
        {
        this.isRunning = false;

        //avoid a needless null pointer exception
        if ((sock != null) && (isUsingExternalSocket == false))
            {
            LOG.debug ("Closing socket on (port, local port) == " +
                           "(" + sock.getPort () + ", " +
                           sock.getLocalPort () + ")");
            sock.close ();
            }
        }

    /**
     * Sends message through this access point's socket.
     * @param message the bytes to send.
     * @param address message destination.
     * @throws IOException if an exception occurs while sending the message.
     */
    void sendMessage(final byte[] message, final StunAddress address)
        throws IOException
        {
        final InetSocketAddress socketAddress = address.getSocketAddress();
        
        if (socketAddress.isUnresolved())
            {
            LOG.debug("Socket address is unresolved");
            
            throw new IOException("Unresolved socket address: " +
                                      socketAddress);
            }

        if (message.length > 1400)
            {
            LOG.warn("Sending large UDP datagram: "+message.length);
            }
        final DatagramPacket datagramPacket =
            new DatagramPacket(message, message.length, socketAddress);

        //sock.connect(socketAddress);
        
        LOG.trace("Sending packet on socket '" + sock.getLocalAddress() +
                      "' to address '" + address + "'");

        sock.send(datagramPacket);
        }

    /**
     * Returns a String representation of the object.
     * @return a String representation of the object.
     */
    public String toString()
        {
        return "net.java.stun4j.stack.AccessPoint@" +
            this.m_apDescriptor.getAddress() + " status: " +
            (isRunning ? "not" : "") + " running";
        }

    /**
     * Sets a socket for the access point to use. This socket will not be
     * closed when the AP is <code>stop()</code>ed
     * (Bug Report - Dave Stuart - SipQuest).
     * @param socket the socket that the AP should use.
     */

    void useExternalSocket(final DatagramSocket socket)
        {
        LOG.trace("Using external socket: "
            + socket.getRemoteSocketAddress());
        this.sock = socket;
        this.isUsingExternalSocket = true;
        }
    }

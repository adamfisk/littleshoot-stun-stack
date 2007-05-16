package net.java.stun4j.client;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.attribute.Attribute;
import net.java.stun4j.attribute.ChangeRequestAttribute;
import net.java.stun4j.attribute.ChangedAddressAttribute;
import net.java.stun4j.attribute.MappedAddressAttribute;
import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Request;
import net.java.stun4j.stack.StunProvider;
import net.java.stun4j.stack.StunStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * This class implements the STUN Discovery Process as described by section 10.1
 * of rfc 3489.
 * </p><p>
 * The flow makes use of three tests.  In test I, the client sends a
 * STUN Binding Request to a server, without any flags set in the
 * CHANGE-REQUEST attribute, and without the RESPONSE-ADDRESS attribute.
 * This causes the server to send the response back to the address and
 * port that the request came from.  In test II, the client sends a
 * Binding Request with both the "change IP" and "change port" flags
 * from the CHANGE-REQUEST attribute set.  In test III, the client sends
 * a Binding Request with only the "change port" flag set.
 * </p><p>
 * The client begins by initiating test I.  If this test yields no
 * response, the client knows right away that it is not capable of UDP
 * connectivity.  If the test produces a response, the client examines
 * the MAPPED-ADDRESS attribute.  If this address and port are the same
 * as the local IP address and port of the socket used to send the
 * request, the client knows that it is not natted.  It executes test
 * II.
 * </p><p>
 * If a response is received, the client knows that it has open access
 * to the Internet (or, at least, its behind a firewall that behaves
 * like a full-cone NAT, but without the translation).  If no response
 * is received, the client knows its behind a symmetric UDP firewall.
 * </p><p>
 * In the event that the IP address and port of the socket did not match
 * the MAPPED-ADDRESS attribute in the response to test I, the client
 * knows that it is behind a NAT.  It performs test II.  If a response
 * is received, the client knows that it is behind a full-cone NAT.  If
 * no response is received, it performs test I again, but this time,
 * does so to the address and port from the CHANGED-ADDRESS attribute
 * from the response to test I.  If the IP address and port returned in
 * the MAPPED-ADDRESS attribute are not the same as the ones from the
 * first test I, the client knows its behind a symmetric NAT.  If the
 * address and port are the same, the client is either behind a
 * restricted or port restricted NAT.  To make a determination about
 * which one it is behind, the client initiates test III.  If a response
 * is received, its behind a restricted NAT, and if no response is
 * received, its behind a port restricted NAT.
 * </p><p>
 * This procedure yields substantial informtion about the operating
 * condition of the client application.  In the event of multiple NATs
 * between the client and the Internet, the type that is discovered will
 * be the type of the most restrictive NAT between the client and the
 * Internet.  The types of NAT, in order of restrictiveness, from most
 * to least, are symmetric, port restricted cone, restricted cone, and
 * full cone.
 * </p><p>
 * Typically, a client will re-do this discovery process periodically to
 * detect changes, or look for inconsistent results.  It is important to
 * note that when the discovery process is redone, it should not
 * generally be done from the same local address and port used in the
 * previous discovery process.  If the same local address and port are
 * reused, bindings from the previous test may still be in existence,
 * and these will invalidate the results of the test.  Using a different
 * local address and port for subsequent tests resolves this problem.
 * An alternative is to wait sufficiently long to be confident that the
 * old bindings have expired (half an hour should more than suffice).
 * </p><p>
 * <p>Organisation: <p> Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */

public final class NetworkConfigurationDiscoveryProcess
    {
    /**
     * Logger for this class.
     */
    private static final Log LOG = 
        LogFactory.getLog(NetworkConfigurationDiscoveryProcess.class);

    /**
     * The stack to use for STUN communication.
     */
    private final StunStack m_stunStack;

    /**
     * The provider to send our messages through
     */
    private final StunProvider m_stunProvider;

    /**
     * The point where we'll be listening.
     */
    private final NetAccessPointDescriptor m_apDescriptor;

    /**
     * The address of the stun server
     */
    private final StunAddress m_serverAddress;

    /**
     * A utility used to flatten the multithreaded architecture of the Stack
     * and execute the discovery process in a synchronized manner
     */
    private BlockingRequestSender m_requestSender;

    /**
     * Creates a StunAddressDiscoverer. In order to use it one must start the
     * discoverer.
     * @param localAddress the address where the stach should bind.
     * @param serverAddress the address of the server to interrogate.
     */
    public NetworkConfigurationDiscoveryProcess(final StunAddress localAddress,
        final StunAddress serverAddress, final StunStack stack)
        {
        this(new NetAccessPointDescriptor(localAddress), serverAddress, stack);
        }

    /**
     * Creates a StunAddressDiscoverer. In order to use it one must start the
     * discoverer.
     * @param apDescriptor the address where the stach should bind.
     * @param serverAddress the address of the server to interrogate.
     */
    public NetworkConfigurationDiscoveryProcess(
        final NetAccessPointDescriptor apDescriptor,
        final StunAddress serverAddress, final StunStack stack)
        {
        this.m_apDescriptor  = apDescriptor;
        this.m_serverAddress = serverAddress;
        this.m_stunStack = stack;
        this.m_stunProvider = this.m_stunStack.getProvider();
        }

    /**
     * Puts the discoverer into an operational state.
     * @throws StunException if we fail to bind or some other error occurs.
     */
    public void start()  throws StunException
        {
        LOG.debug("Starting...");
        m_stunStack.installNetAccessPoint(this.m_apDescriptor);
        m_requestSender = 
            new BlockingRequestSender(this.m_stunProvider, this.m_apDescriptor);
        }
    
    /**
     * Starts the discovery process using the specified existing socket for 
     * sending and receiving STUN messages.
     * @param socket The socket to use for sending and receiving STUN
     * messages.
     * @throws StunException If any unexpected STUN error occurs.
     */
    public void start(final DatagramSocket socket) throws StunException
        {
        m_stunStack.installNetAccessPoint(this.m_apDescriptor, socket);
        m_requestSender = 
            new BlockingRequestSender(this.m_stunProvider, this.m_apDescriptor);     
        }

    /**
     * Implements the discovery process itself (see class description).
     * @return a StunDiscoveryReport containing details about the network
     * configuration of the host where the class is executed.
     * @throws StunException NETWORK_ERROR or ILLEGAL_ARGUMENT if a failure 
     * occurs while executing the discovery algorithm
     */
    public StunDiscoveryReport determineAddress() throws StunException
        {
        return determineAddress(new NoOpMappedAddressListener());
        }
    
    /**
     * Implements the discovery process itself (see class description).
     * 
     * @param listener The listener for the first receipt of the MAPPED-ADDRESS
     * attribute from a STUN "Binding Response" message.
     * @return a StunDiscoveryReport containing details about the network
     * configuration of the host where the class is executed.
     * @throws StunException NETWORK_ERROR or ILLEGAL_ARGUMENT if a failure 
     * occurs while executing the discovery algorithm
     */
    public StunDiscoveryReport determineAddress(
        final MappedAddressListener listener) throws StunException
        {
        LOG.trace("Determining address...");
        final StunDiscoveryReport report = new StunDiscoveryReport();
        final StunMessageEvent evt = doTestI(this.m_serverAddress);

        if (evt == null)
            {
            // UDP Blocked
            LOG.trace("NAT is UDP BLOCKING FIREWALL");
            report.setNatType(StunDiscoveryReport.UDP_BLOCKING_FIREWALL);
            return report;
            }
        else
            {
            final StunAddress mappedAddress = 
                ((MappedAddressAttribute) evt.getMessage().getAttribute(
                    Attribute.MAPPED_ADDRESS)).getAddress();
            LOG.trace("mapped address is=" + mappedAddress + ", name="
                + mappedAddress.getHostName());
            
            listener.mappedAddress(mappedAddress.getSocketAddress());
            final StunAddress backupServerAddress = 
                ((ChangedAddressAttribute) evt.getMessage().getAttribute(
                    Attribute.CHANGED_ADDRESS)).getAddress();
            LOG.trace("backup server address is="
                + backupServerAddress + ", name="
                + backupServerAddress.getHostName());
            report.setPublicAddress(mappedAddress);
            if (mappedAddress.equals(this.m_apDescriptor.getAddress()))
                {
                return determineUdpFirewallOrOpenInternet(report, 
                    this.m_serverAddress);
                }
            else
                {
                return determineConeOrSymmetric(report, this.m_serverAddress,
                    mappedAddress, backupServerAddress);
                }
            }

        }

    private StunDiscoveryReport determineConeOrSymmetric(
        final StunDiscoveryReport report, final StunAddress serverAddress,
        final StunAddress mappedAddress, final StunAddress backupServerAddress) 
        throws StunException
        {
        LOG.trace("Choosing between cone and symmetric NAT...");
        StunMessageEvent evt = doTestII(serverAddress);
        if (evt == null)
            {
            evt = doTestI(backupServerAddress);
            if (evt == null)
                {
                LOG.trace("Failed to receive a response from backup " +
                    "stun server!");
                return report;
                }
            final StunAddress mappedAddress2 = ((MappedAddressAttribute) evt
                .getMessage().getAttribute(Attribute.MAPPED_ADDRESS))
                .getAddress();
            if (mappedAddress.equals(mappedAddress2))
                {
                LOG.trace("Cone NAT but not full...");
                return determineTypeOfConeNat(report, this.m_serverAddress);
                }
            else
                {
                // Symmetric NAT
                LOG.trace("NAT is SYMMETRIC");
                report.setNatType(StunDiscoveryReport.SYMMETRIC_NAT);
                return report;
                }
            }
        else
            {
            //full cone
            LOG.trace("NAT is FULL CONE");
            report.setNatType(StunDiscoveryReport.FULL_CONE_NAT);
            return report;
            }
        }

    /**
     * Called once we know the user is behind either a restricted cone NAT or
     * a port restricted cone NAT.
     * 
     * @param report The report to add data to before returning.
     * @param serverAddress The address of the STUN server to access.
     * @return The report containing the type of NAT.
     * @throws StunException If there's an exception getting data from the
     * STUN server.
     */
    private StunDiscoveryReport determineTypeOfConeNat(
        final StunDiscoveryReport report, final StunAddress serverAddress) 
        throws StunException
        {
        final StunMessageEvent evt = doTestIII(serverAddress);
        if (evt == null)
            {
            // port restricted cone
            LOG.trace("NAT is PORT RESTRICTED CONE");
            report.setNatType(StunDiscoveryReport.PORT_RESTRICTED_CONE_NAT);
            return report;
            }
        else
            {
            // restricted cone
            LOG.trace("NAT is RESTRICTED CONE");
            report.setNatType(StunDiscoveryReport.RESTRICTED_CONE_NAT);
            return report;

            }
        }

    /**
     * Called when we've determined this user is either behind a symmetric
     * UDP firewall or is not behind any firewall or NAT.
     * 
     * @param report The report to add NAT data to before returning.
     * @param serverAddress The address of the STUN server to use.
     * @return The report containing the NAT type.
     * @throws StunException If there's an error getting data from the STUN
     * server.
     */
    private StunDiscoveryReport determineUdpFirewallOrOpenInternet(
        final StunDiscoveryReport report, final StunAddress serverAddress) 
        throws StunException
        {
        final StunMessageEvent evt = doTestII(serverAddress);
        if (evt == null)
            {
            // Sym UDP Firewall
            LOG.trace("NAT is SYMMETRIC UDP FIREWALL");
            report.setNatType(StunDiscoveryReport.SYMMETRIC_UDP_FIREWALL);
            return report;
            }
        else
            {
            // open internet
            LOG.trace("MACHINE IS ON THE OPEN INTERNET");
            report.setNatType(StunDiscoveryReport.OPEN_INTERNET);
            return report;
            }
        }

    /**
     * Sends a binding request to the specified server address. Both change IP
     * and change port flags are set to false.
     * @param serverAddress the address where to send the bindingRequest.
     * @return The returned message encapsulating event or null if no message
     * was received.
     * @throws StunException if an exception occurs while sending the messge
     */
    private StunMessageEvent doTestI(final StunAddress serverAddress)
        throws StunException
        {
        LOG.trace("Running TEST I");
        return sendStunRequest(serverAddress, false, false);
        }

    /**
     * Sends a binding request to the specified server address with both change
     * IP and change port flags are set to true.
     * @param serverAddress the address where to send the bindingRequest.
     * @return The returned message encapsulating event or null if no message
     * was received.
     * @throws StunException if an exception occurs while sending the messge
     */
    private StunMessageEvent doTestII(final StunAddress serverAddress)
        throws StunException
        {
        LOG.trace("Running TEST II");
        return sendStunRequest(serverAddress, true, true);
        }

    /**
     * Sends a binding request to the specified server address with only change
     * port flag set to true and change IP flag - to false.
     * @param serverAddress the address where to send the bindingRequest.
     * @return The returned message encapsulating event or null if no message
     * was received.
     * @throws StunException if an exception occurs while sending the messge
     */
    private StunMessageEvent doTestIII(final StunAddress serverAddress)
        throws StunException
        {
        LOG.trace("Running TEST III");
        return sendStunRequest(serverAddress, false, true);
        }

    /**
     * Sends a STUN message to the specified server with the specified flags
     * for changing the IP and port on the STUN server.
     * @param serverAddress The address of the STUN server.
     * @param changeIpFlag Whether or not the server should send a response
     * from a different IP address than the address to which the request was
     * sent.
     * @param changePortFlag Whether or not the server should send a response
     * from a different port than the port to which the request was sent.
     * @return The STUN message event containing data retrieved for this test.
     * @throws StunException If an unexpected STUN error occurs.
     */
    private StunMessageEvent sendStunRequest(final StunAddress serverAddress, 
        final boolean changeIpFlag, final boolean changePortFlag) 
        throws StunException
        {
        final Request request = MessageFactory.createBindingRequest();

        final ChangeRequestAttribute changeRequest = 
            (ChangeRequestAttribute)request.getAttribute(
                Attribute.CHANGE_REQUEST);
        changeRequest.setChangeIpFlag(changeIpFlag);
        changeRequest.setChangePortFlag(changePortFlag);

        final StunMessageEvent evt =
            m_requestSender.sendRequestAndWaitForResponse(request, 
                serverAddress);
        if(evt != null)
            {
            LOG.trace("Test res="+evt.getRemoteAddress() + " - " + 
                evt.getRemoteAddress().getHostName());
            }
        else
            {
            LOG.trace("NO RESPONSE received to Test.");
            }
        return evt;
        }
    
    /**
     * A listener for the MAPPED-ADDRESS STUN attribute that takes no action.
     */
    private static final class NoOpMappedAddressListener 
        implements MappedAddressListener
        {

        public void mappedAddress(final InetSocketAddress mappedAddress)
            {
            // No op.
            }
    
        }
    }

package net.java.stun4j.client;

import net.java.stun4j.NetAccessPointDescriptor;
import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.StunMessageEvent;
import net.java.stun4j.attribute.Attribute;
import net.java.stun4j.attribute.MappedAddressAttribute;
import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Response;
import net.java.stun4j.stack.StunProvider;
import net.java.stun4j.stack.StunStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class provides basic means of discovering a public IP address. All it
 * does is send a binding request through a specified port and return the mapped
 * address it got back or null if there was no reponse
 * 
 * <p>
 * Organisation:
 * <p>
 * Louis Pasteur University, Strasbourg, France
 * </p>
 * <p>
 * Network Research Team (http://www-r2.u-strasbg.fr)
 * </p>
 * </p>
 * 
 * @author Emil Ivov
 * @version 0.1
 */
public final class SimpleAddressDetector
    {

    /**
     * Logger for this class.
     */
    private static final Log LOG = 
        LogFactory.getLog(SimpleAddressDetector.class);
    
    /**
     * The stack to use for STUN communication.
     */
    private final StunStack m_stunStack;

    /**
     * The provider to send our messages through
     */
    private final StunProvider m_stunProvider;

    /**
     * The address of the stun server
     */
    private final StunAddress m_serverAddress;

    /**
     * Creates a StunAddressDiscoverer. In order to use it one must start the
     * discoverer.
     * 
     * @param serverAddress The address of the server to interrogate.
     */
    public SimpleAddressDetector(final StunAddress serverAddress, 
        final StunStack stack)
        {
        this.m_serverAddress = serverAddress;
        this.m_stunStack = stack;
        this.m_stunProvider = this.m_stunStack.getProvider();
        }

    /**
     * Creates a listening point from the specified port and attempts to
     * discover how it is being mapped.
     * 
     * @param port The local port where to send the request from.
     * @return a StunAddress object containing the mapped address or null if
     * discovery failed.
     * @throws StunException If something fails along the way.
     */
    public StunAddress getMappingFor(final int port) throws StunException
        {
        LOG.debug("Accessing mapping...");
        final StunAddress address = new StunAddress(port);
        final NetAccessPointDescriptor apDesc = 
            new NetAccessPointDescriptor(address);

        this.m_stunStack.installNetAccessPoint(apDesc);
        return getMappingFor(apDesc);
        }

    private StunAddress getMappingFor(final NetAccessPointDescriptor apDesc) 
        throws StunException
        {
        final BlockingRequestSender requestSender = 
            new BlockingRequestSender(this.m_stunProvider, apDesc);
        final StunMessageEvent stunMessageEvent;
        try
            {
            stunMessageEvent = requestSender.sendRequestAndWaitForResponse(
                MessageFactory.createBindingRequest(), this.m_serverAddress);
            }
        finally
            {
            // Free the port to allow the application to use it.
            LOG.trace("Removing net access point: "+apDesc.getAddress());
            this.m_stunStack.removeNetAccessPoint(apDesc);
            }

        if (stunMessageEvent == null)
            {
            LOG.warn("Could not access address: "+this.m_serverAddress);
            throw new StunException("Could not access address: "+
                this.m_serverAddress);
            }
        
        final Response res = (Response) stunMessageEvent.getMessage();
        final MappedAddressAttribute mappedAddress = 
            (MappedAddressAttribute) res.getAttribute(Attribute.MAPPED_ADDRESS);
        
        if (mappedAddress == null)
            {
            LOG.warn("No MAPPED-ADDRESS attribute in STUN response");
            throw new StunException("Mapped address attribute is null."); 
            }
        
        return mappedAddress.getAddress();
        
        }
    }

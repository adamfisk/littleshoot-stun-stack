package net.java.stun4j.client;

import net.java.stun4j.StunAddress;
import net.java.stun4j.StunException;
import net.java.stun4j.message.MessageFactory;
import net.java.stun4j.message.Response;
import net.java.stun4j.stack.StunStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class StunAddressDiscovererTest 
    extends AbstractDependencyInjectionSpringContextTests {
    
    /**
     * Logger for this class.
     */
    private static final Log LOG = 
        LogFactory.getLog(StunAddressDiscovererTest.class);
    
    private NetworkConfigurationDiscoveryProcess m_stunAddressDiscoverer;
    
    private StunAddress m_discovererAddress = 
        new StunAddress("127.0.0.1", 5555);

    private ResponseSequenceServer m_responseServer;
    private StunAddress m_responseServerAddress = 
        new StunAddress("127.0.0.1", 9999);
    private StunAddress m_mappedClientAddress = 
        new StunAddress("212.56.4.10", 5612);
    private StunAddress m_mappedClientAddressPort2 = 
        new StunAddress("212.56.4.10", 5611);

    protected String[] getConfigLocations()
        {
        return new String[] {"stunBeans.xml"};
        }
    
    protected void onSetUp() throws Exception 
        {
        LOG.trace("Setting up test...");
        
        final StunStack stack = 
            (StunStack) applicationContext.getBean("stun-stack");
        
        //stack.start();
        m_responseServer = 
            new ResponseSequenceServer(m_responseServerAddress, stack);
        m_stunAddressDiscoverer = 
            new NetworkConfigurationDiscoveryProcess(m_discovererAddress, 
                 m_responseServerAddress, stack);

        m_stunAddressDiscoverer.start();
        m_responseServer.start();
        }

    protected void onTearDown() throws Exception 
        {
        LOG.trace("Tearing down the test...");
        m_responseServer.shutDown();
        //m_stunAddressDiscoverer.shutDown();
        m_stunAddressDiscoverer = null;
        }

    /**
     * Performs a test where no responces are given the STUN client so that
     * it concludes it's in a network where UDP is blocked.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeBlockedUDP() throws StunException 
        {
        LOG.trace("Testing blocked UPD");
        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.UDP_BLOCKING_FIREWALL);
        expectedReturn.setPublicAddress(null);

        StunDiscoveryReport actualReturn = m_stunAddressDiscoverer.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a no-udp environment.",
                     expectedReturn, actualReturn);
        }


    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Symmetric NAT.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeSymmetricNat() throws StunException
        {
        LOG.trace("Testing symmetric NAT...");
        //define the server response sequence
        Response testIResponse1 = 
            MessageFactory.createBindingResponse(
        		m_mappedClientAddress, m_responseServerAddress, 
                m_responseServerAddress);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
        	m_mappedClientAddressPort2, m_responseServerAddress, 
            m_responseServerAddress);

        m_responseServer.addMessage(testIResponse1);
        m_responseServer.addMessage(testIResponse2);
        m_responseServer.addMessage(testIResponse3);


        final StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.SYMMETRIC_NAT);
        expectedReturn.setPublicAddress(m_mappedClientAddress);

        StunDiscoveryReport actualReturn = 
            m_stunAddressDiscoverer.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a no-udp environment.",
                     expectedReturn, actualReturn);

        }

    /**
     * Performs a test where no responses are given the stun client so that
     * it concludes it is behind a Port Restricted Cone.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizePortRestrictedCone() throws StunException
        {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            m_mappedClientAddress, m_responseServerAddress, m_responseServerAddress);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
            m_mappedClientAddress, m_responseServerAddress, m_responseServerAddress);
        Response testIResponse4 = null;

        m_responseServer.addMessage(testIResponse1);
        m_responseServer.addMessage(testIResponse2);
        m_responseServer.addMessage(testIResponse3);
        m_responseServer.addMessage(testIResponse4);


        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.PORT_RESTRICTED_CONE_NAT);
        expectedReturn.setPublicAddress(m_mappedClientAddress);

        StunDiscoveryReport actualReturn = m_stunAddressDiscoverer.determineAddress();
        assertEquals("The StunAddressDiscoverer failed for a no-udp environment.",
                     expectedReturn, actualReturn);

        }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Restricted Cone.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeRestrictedCone() throws StunException
        {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            m_mappedClientAddress, m_responseServerAddress, m_responseServerAddress);
        Response testIResponse2 = null;
        Response testIResponse3 = MessageFactory.createBindingResponse(
            m_mappedClientAddress, m_responseServerAddress, m_responseServerAddress);
        Response testIResponse4 = MessageFactory.createBindingResponse(
            m_mappedClientAddress, m_responseServerAddress, m_responseServerAddress);

        m_responseServer.addMessage(testIResponse1);
        m_responseServer.addMessage(testIResponse2);
        m_responseServer.addMessage(testIResponse3);
        m_responseServer.addMessage(testIResponse4);

        final StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.RESTRICTED_CONE_NAT);
        expectedReturn.setPublicAddress(m_mappedClientAddress);

        final StunDiscoveryReport actualReturn = 
            m_stunAddressDiscoverer.determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);
        }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a Full Cone.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeFullCone() throws StunException
        {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            m_mappedClientAddress, m_responseServerAddress, m_responseServerAddress);
        Response testIResponse2 = MessageFactory.createBindingResponse(
            m_mappedClientAddress, m_responseServerAddress, m_responseServerAddress);

        m_responseServer.addMessage(testIResponse1);
        m_responseServer.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.FULL_CONE_NAT);
        expectedReturn.setPublicAddress(m_mappedClientAddress);

        StunDiscoveryReport actualReturn = m_stunAddressDiscoverer.
            determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);

        }

    /**
     * Performs a test where no responces are given the stun client so that
     * it concludes it is behind a UDP Symmetric Firewall.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeUdpSymmetricFirewall() throws StunException
        {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            m_discovererAddress, m_responseServerAddress, m_responseServerAddress);
        Response testIResponse2 = null;

        m_responseServer.addMessage(testIResponse1);
        m_responseServer.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.SYMMETRIC_UDP_FIREWALL);
        expectedReturn.setPublicAddress(m_discovererAddress);

        StunDiscoveryReport actualReturn = m_stunAddressDiscoverer.
            determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);

        }
    /**
     * Performs a test where responces are given the stun client so that
     * it concludes it is behind a Open Internet.
     * @throws StunException if anything goes wrong ( surprised? ).
     */
    public void testRecognizeOpenInternet() throws StunException
        {
        //define the server response sequence
        Response testIResponse1 = MessageFactory.createBindingResponse(
            m_discovererAddress, m_responseServerAddress, m_responseServerAddress);
        Response testIResponse2 = MessageFactory.createBindingResponse(
            m_discovererAddress, m_responseServerAddress, m_responseServerAddress);

        m_responseServer.addMessage(testIResponse1);
        m_responseServer.addMessage(testIResponse2);

        StunDiscoveryReport expectedReturn = new StunDiscoveryReport();

        expectedReturn.setNatType(StunDiscoveryReport.OPEN_INTERNET);
        expectedReturn.setPublicAddress(m_discovererAddress);

        final StunDiscoveryReport actualReturn = 
            m_stunAddressDiscoverer.determineAddress();
        assertEquals(
            "The StunAddressDiscoverer failed for a no-udp environment.",
            expectedReturn, actualReturn);
        }
    }

package net.java.stun4j.attribute;

import junit.framework.*;
import net.java.stun4j.*;
import java.util.Arrays;

/**
 *
 * <p>Title: Stun4J</p>
 * <p>Description: Simple Traversal of UDP Through NAT</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organisation:  <p> Organisation: Louis Pasteur University, Strasbourg, France</p>
 *                   <p> Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
public class AddressAttributeTest extends TestCase {
    private AddressAttribute addressAttribute = null;
    private MsgFixture msgFixture;

    public AddressAttributeTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        addressAttribute = new MappedAddressAttribute();
        msgFixture = new MsgFixture(this);

        msgFixture.setUp();
    }

    protected void tearDown() throws Exception {
        addressAttribute = null;
        msgFixture.tearDown();

        msgFixture = null;
        super.tearDown();
    }

    /**
     * Verify that AddressAttribute descendants have correctly set types and names.
     */
    public void testAddressAttributeDescendants() {
        char expectedType;
        char actualType;
        String expectedName;
        String actualName;

        //MAPPED-ADDRESS
        addressAttribute = new MappedAddressAttribute();

        expectedType = Attribute.MAPPED_ADDRESS;
        actualType = addressAttribute.getAttributeType();

        expectedName = "MAPPED-ADDRESS";
        actualName = addressAttribute.getName();

        assertEquals("MappedAddressAttribute does not the right type.",
                     expectedType, actualType);
        assertEquals("MappedAddressAttribute does not the right name.",
                     expectedName, actualName);


        //SOURCE-ADDRESS
        addressAttribute = new SourceAddressAttribute();

        expectedType = Attribute.SOURCE_ADDRESS;
        actualType = addressAttribute.getAttributeType();

        expectedName = "SOURCE-ADDRESS";
        actualName = addressAttribute.getName();

        assertEquals("SourceAddressAttribute does not the right type.",
                     expectedType, actualType);
        assertEquals("SourceAddressAttribute does not the right name.",
                     expectedName, actualName);


        //CHANGED-ADDRESS
        addressAttribute = new ChangedAddressAttribute();

        expectedType = Attribute.CHANGED_ADDRESS;
        actualType = addressAttribute.getAttributeType();

        expectedName = "CHANGED-ADDRESS";
        actualName = addressAttribute.getName();

        assertEquals("ChangedAddressAttribute does not the right type.",
                     expectedType, actualType);
        assertEquals("ChangedAddressAttribute does not the right name.",
                     expectedName, actualName);


        //RESPONSE-ADDRESS
        addressAttribute = new ResponseAddressAttribute();

        expectedType = Attribute.RESPONSE_ADDRESS;
        actualType = addressAttribute.getAttributeType();

        expectedName = "RESPONSE-ADDRESS";
        actualName = addressAttribute.getName();

        assertEquals("ResponseAddressAttribute does not the right type.",
                     expectedType, actualType);
        assertEquals("ResponseAddressAttribute does not the right name.",
                     expectedName, actualName);


        //REFLECTED-FROM
        addressAttribute = new ReflectedFromAttribute();

        expectedType = Attribute.REFLECTED_FROM;
        actualType = addressAttribute.getAttributeType();

        expectedName = "REFLECTED-FROM";
        actualName = addressAttribute.getName();

        assertEquals("ReflectedFromAttribute does not the right type.",
                     expectedType, actualType);
        assertEquals("ReflectedFromAttribute does not the right name.",
                     expectedName, actualName);

    }

    /**
     * Test whetner sample binary arrays are correctly decoded.
     * @throws StunException
     */
    public void testDecodeAttributeBody() throws StunException {
        byte[] attributeValue = msgFixture.mappedAddress;
        char offset = Attribute.HEADER_LENGTH;
        char length = (char)(attributeValue.length - offset);

        addressAttribute.decodeAttributeBody(attributeValue, offset, length);


        assertEquals("AddressAttribute.decode() did not properly decode the port field.",
                     msgFixture.ADDRESS_ATTRIBUTE_PORT,
                     addressAttribute.getPort());
        assertTrue("AddressAttribute.decode() did not properly decode the address field.",
                     Arrays.equals( msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                    addressAttribute.getAddressBytes()));


    }

    /**
     * Test whether attributes are properly encoded.
     *
     * @throws StunException java.lang.Exception if we fail
     */
    public void testEncode()
        throws StunException
    {
        byte[] expectedReturn = msgFixture.mappedAddress;

        addressAttribute.setAddress(
                            new StunAddress(msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                        msgFixture.ADDRESS_ATTRIBUTE_PORT));

        byte[] actualReturn = addressAttribute.encode();
        assertTrue("AddressAttribute.encode() did not properly encode a sample attribute",
                     Arrays.equals( expectedReturn, actualReturn));
    }

    /**
     * Tests the equals method against a null, a different and an identical
     * object.
     *
     * @throws StunException java.lang.Exception if we fail
     */
    public void testEquals()
        throws StunException
    {
        //null test
        AddressAttribute target = null;
        boolean expectedReturn = false;
        boolean actualReturn = addressAttribute.equals(target);

        assertEquals("AddressAttribute.equals() failed against a null target.",
                     expectedReturn, actualReturn);

        //difference test
        target = new MappedAddressAttribute();

        char port = (char)(msgFixture.ADDRESS_ATTRIBUTE_PORT + 1 );
        target.setAddress(  new StunAddress(msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                        port));

        addressAttribute.setAddress(
                             new StunAddress(msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                (char)msgFixture.ADDRESS_ATTRIBUTE_PORT ));

        expectedReturn = false;
        actualReturn = addressAttribute.equals(target);
        assertEquals("AddressAttribute.equals() failed against a different target.",
                     expectedReturn, actualReturn);

        //equality test
        target.setAddress( new StunAddress( msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                       (char)msgFixture.ADDRESS_ATTRIBUTE_PORT ));

        expectedReturn = true;
        actualReturn = addressAttribute.equals(target);
        assertEquals("AddressAttribute.equals() failed against an equal target.",
                     expectedReturn, actualReturn);



    }

    /**
     * Tests whether data length is properly calculated.
     *
     * @throws StunException java.lang.Exception if we fail
     */
    public void testGetDataLength()
        throws StunException
    {
        char expectedReturn = 8;//1-padding + 1-family + 2-port + 4-address

        addressAttribute.setAddress(
                            new StunAddress( msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                         msgFixture.ADDRESS_ATTRIBUTE_PORT));

        char actualReturn = addressAttribute.getDataLength();

        assertEquals("Datalength is not propoerly calculated",
                     expectedReturn, actualReturn);
    }

    /**
     * Tests that the address family is always 1.
     */
    public void testGetFamily() {
        byte expectedReturn = 1;
        byte actualReturn = addressAttribute.getFamily();
        assertEquals("Address family was different that 1",
                     expectedReturn, actualReturn);

    }

}

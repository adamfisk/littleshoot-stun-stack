package net.java.stun4j.attribute;

import junit.framework.*;
import net.java.stun4j.*;


/**
 * We have already tested individual decode methods, so our job here
 * is to verify that that AttributeDecoder.decode distributes the right way.
 */
public class AttributeDecoderTest extends TestCase {
    private AttributeDecoder attributeDecoder = null;
    private MsgFixture msgFixture;

    public AttributeDecoderTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();

        attributeDecoder = new AttributeDecoder();
        msgFixture = new MsgFixture(this);

        msgFixture.setUp();
    }

    protected void tearDown() throws Exception {
        attributeDecoder = null;
        msgFixture.tearDown();

        msgFixture = null;
        super.tearDown();
    }

    public void testDecodeMappedAddress()
        throws StunException
    {
        //
        byte[] bytes = msgFixture.mappedAddress;
        char offset = 0;
        char length = (char)bytes.length;

        //create the message
        MappedAddressAttribute expectedReturn = new MappedAddressAttribute();

        expectedReturn.setAddress(
                            new StunAddress(msgFixture.ADDRESS_ATTRIBUTE_ADDRESS,
                                        msgFixture.ADDRESS_ATTRIBUTE_PORT));

        Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
        assertEquals("AttributeDecoder.decode() failed for a MAPPED-ADDRESS attribute",
                     expectedReturn, actualReturn);


    }

    public void testDecodeChangeRequest()
        throws StunException
    {
        //
        byte[] bytes = msgFixture.chngReqTestValue1;
        char offset = 0;
        char length = (char)bytes.length;

        //create the message
        ChangeRequestAttribute expectedReturn = new ChangeRequestAttribute();
        expectedReturn.setChangeIpFlag(msgFixture.CHANGE_IP_FLAG_1);
        expectedReturn.setChangePortFlag(msgFixture.CHANGE_PORT_FLAG_1);

        Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
        assertEquals("AttributeDecoder.decode() failed for a CHANGE-REQUEST attribute",
                     expectedReturn, actualReturn);

    }


   public void testDecodeErrorCode()
       throws StunException
   {
       //
       byte[] bytes = msgFixture.errCodeTestValue;
       char offset = 0;
       char length = (char)bytes.length;

       //create the message
       ErrorCodeAttribute expectedReturn = new ErrorCodeAttribute();
       expectedReturn.setErrorClass(msgFixture.ERROR_CLASS);
       expectedReturn.setErrorNumber(msgFixture.ERROR_NUMBER);
       expectedReturn.setReasonPhrase(msgFixture.REASON_PHRASE);

       Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
       assertEquals("AttributeDecoder.decode() failed for a ERROR-CODE attribute",
                    expectedReturn, actualReturn);

   }

   public void testDecodeUnknownAttributes()
       throws StunException
   {
       //unknown attributes
       byte[] bytes = msgFixture.unknownAttsDecodeTestValue;
       char offset = 0;
       char length = (char)msgFixture.mappedAddress.length;

       //create the message
       UnknownAttributesAttribute expectedReturn = new UnknownAttributesAttribute();
       expectedReturn.addAttributeID(msgFixture.UNKNOWN_ATTRIBUTES_1ST_ATT);
       expectedReturn.addAttributeID(msgFixture.UNKNOWN_ATTRIBUTES_2ND_ATT);
       expectedReturn.addAttributeID(msgFixture.UNKNOWN_ATTRIBUTES_3D_ATT);

       Attribute actualReturn = attributeDecoder.decode(bytes, offset, length);
       assertEquals("AttributeDecoder.decode() failed for a ERROR-CODE attribute",
                    expectedReturn, actualReturn);

   }




}

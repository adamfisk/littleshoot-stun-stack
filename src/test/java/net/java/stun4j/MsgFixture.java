package net.java.stun4j;


public class MsgFixture
{
    //---------------------- Attributes ---------------------------------------
    public static final char ADDRESS_ATTRIBUTE_PORT = 1904;
    public static final byte[] ADDRESS_ATTRIBUTE_ADDRESS = new byte[]{(byte)130, 79, (byte)156, (byte)137};

    public byte[] mappedAddress = new byte[]
        {0x00,       0x01,       0x00,       0x08, //Type, Length
         0x00,       0x01,       0x07,       0x70, //00 Family Port(1904)
         (byte)0x82, 0x4f, (byte)0x9c, (byte)0x89  //IP Address 130.79.156.137
        };//

    public static final char ADDRESS_ATTRIBUTE_PORT_2 = 3478;
    public static final byte[] ADDRESS_ATTRIBUTE_ADDRESS_2 = new byte[]{69, 0, (byte)208, 27};
    public static final char ADDRESS_ATTRIBUTE_PORT_3 = 3479;
    public static final byte[] ADDRESS_ATTRIBUTE_ADDRESS_3 = new byte[]{69, 0, (byte)209, 22};
    public byte[] sourceAddress = new byte[]
        {0x00,       0x04,       0x00,       0x08, //Type Length
         0x00,       0x01,       0x0d, (byte)0x96, //00 Family Port(3478)
         0x45,       0x00, (byte)0xd0,       0x1b  //IP Address 69.0.208.27
        };
    public byte[] changedAddress = new byte[]
         {0x00,       0x05,       0x00,       0x08, //Type Length
          0x00,       0x01,       0x0d, (byte)0x97, //00 Family Port(3479)
          0x45,       0x00, (byte)0xd1,       0x16  //IP Address 69.0.209.22
         };

    /**
     * Contains three invalid attribute ids. The 3d is repeated to keep an even
     * number of reports.
     */
     public static final char UNKNOWN_ATTRIBUTES_1ST_ATT = 0x20;
     public static final char UNKNOWN_ATTRIBUTES_2ND_ATT = 0x21;
     public static final char UNKNOWN_ATTRIBUTES_3D_ATT  = 0x22;
     public static final char UNKNOWN_ATTRIBUTES_CNT_DEC_TST   = 3;
     public byte[] unknownAttsDecodeTestValue = new byte[]
     {0x00,       0x0a,       0x00,       0x08, //Type Length
      0x00,       0x20,       0x00,       0x21,
      0x00,       0x22,       0x00,       0x22};


    public static final char UNKNOWN_ATTRIBUTES_CNT_ENC_TST   = 2;
    public byte[] unknownAttsEncodeExpectedResult = new byte[]
    {0x00,        0x0a,       0x00,       0x04, //Type Length
     0x00,	  0x20,       0x00,       0x21};

    //--- change request
    public static final boolean CHANGE_IP_FLAG_1 = false;
    public static final boolean CHANGE_PORT_FLAG_1 = false;
    public byte[] chngReqTestValue1 = new byte[]
    {0x00,        0x03,       0x00,       0x04,
     0x00,        0x00,       0x00,       0x00};

    public static final boolean CHANGE_IP_FLAG_2 = true;
    public static final boolean CHANGE_PORT_FLAG_2 = true;
    public byte[] chngReqTestValue2 = new byte[]
    {0x00,        0x03,       0x00,       0x04,
     0x00,        0x00,       0x00,       0x06};

    //--- error code
    public static final byte ERROR_CLASS = 4;
    public static final byte ERROR_NUMBER = 20;
    public static final char ERROR_CODE = 420;

    public static final String REASON_PHRASE = "Test error reason phrase.";//odd length!

    public byte[] errCodeTestValue = new byte[]
    {0x00,        0x09,        0x00,        0x38,
     0x00,        0x00,        0x04,        0x14,
     0x00,        0x54,        0x00,        0x65,
     0x00,        0x73,        0x00,        0x74,
     0x00,        0x20,        0x00,        0x65,
     0x00,        0x72,        0x00,        0x72,
     0x00,        0x6F,        0x00,        0x72,
     0x00,        0x20,        0x00,        0x72,
     0x00,        0x65,        0x00,        0x61,
     0x00,        0x73,        0x00,        0x6F,
     0x00,        0x6E,        0x00,        0x20,
     0x00,        0x70,        0x00,        0x68,
     0x00,        0x72,        0x00,        0x61,
     0x00,        0x73,        0x00,        0x65,
     0x00,        0x2E,        0x00,        0x20
    };


    //--------------------------- Messages ----------------------------------------
    public static final byte[] TRANSACTION_ID =
            new byte[]{
                   0x01,       0x02,       0x03,       0x04, //Transaction ID
                   0x05,       0x06,       0x07,       0x08,
                   0x09,       0x10,       0x11,       0x12,
                   0x13,       0x14,       0x15,       0x16
        };

    public byte[]          bindingRequest = new byte[]
    {
       0x00,       0x01,       0x00,       0x08, //STUN Msg Type  |  Msg Length
       0x01,       0x02,       0x03,       0x04, //Transaction ID
       0x05,       0x06,       0x07,       0x08,
       0x09,       0x10,       0x11,       0x12,
       0x13,       0x14,       0x15,       0x16,
       0x00,       0x03,       0x00,       0x04,//Type(Change Request) Len
       0x00,       0x00,       0x00,       0x00 //Don't change neither IP nor port
    };

    public byte[] 	   bindingResponse = new byte[]
       {0x01,       0x01,       0x00,       0x24, //Type Length
        0x01,       0x02,       0x03,       0x04, //Transaction ID
        0x05,       0x06,       0x07,       0x08,
        0x09,       0x10,       0x11,       0x12,
        0x13,       0x14,       0x15,       0x16,
        0x00,       0x01,       0x00,       0x08, //AttType(MappedAddress) AttLength
        0x00,       0x01,       0x07,       0x70, //00 Family Port(1904)
  (byte)0x82,       0x4f, (byte)0x9c, (byte)0x89, //IP 130.79.156.37
        0x00,       0x04,       0x00,       0x08, //AttType(Source Address) AttLen
        0x00,       0x01,       0x0d, (byte)0x96, //00 Family Port(3478)
        0x45,       0x00, (byte)0xd0,       0x1b, //IP 69.0.208.27
        0x00,       0x05,       0x00,       0x08, //AttType(ChangedAddress)
        0x00,       0x01,       0x0d, (byte)0x97, //00 Family Port(3479)
        0x45,       0x00, (byte)0xd1,       0x16};//IP Address 69.0.209.22

    public byte[] bindingErrorResponse = new byte[]
        {
        0x01,        0x11,        0x00,        0x24, //Type Length
        0x01,        0x4a,        0x54,        0x32, //Transaction ID
        0x0a,        0x77,        0x6f,        0x64,
        0x58,        0x04, (byte) 0xae,        0x46,
  (byte)0x85,        0x19, (byte) 0xcc,        0x3c,
        0x00,        0x09,        0x00,        0x38,//Error Code Attribute
        0x00,        0x00,        0x04,        0x14,//Reason Phrase
        0x00,        0x54,        0x00,        0x65,
        0x00,        0x73,        0x00,        0x74,
        0x00,        0x20,        0x00,        0x65,
        0x00,        0x72,        0x00,        0x72,
        0x00,        0x6F,        0x00,        0x72,
        0x00,        0x20,        0x00,        0x72,
        0x00,        0x65,        0x00,        0x61,
        0x00,        0x73,        0x00,        0x6F,
        0x00,        0x6E,        0x00,        0x20,
        0x00,        0x70,        0x00,        0x68,
        0x00,        0x72,        0x00,        0x61,
        0x00,        0x73,        0x00,        0x65,
        0x00,        0x2E,        0x00,        0x20,//unknown attributes
        0x00,        0x0a,        0x00,        0x08,//Type Length
        0x00,        0x20,        0x00,        0x21,
        0x00,        0x22,        0x00,        0x22
        };


    public MsgFixture(Object obj)
    {

    }

    public void setUp()
    {

    }

    public void tearDown()
    {
    }

}

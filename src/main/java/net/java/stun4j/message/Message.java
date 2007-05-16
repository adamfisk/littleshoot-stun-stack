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

package net.java.stun4j.message;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import net.java.stun4j.StunException;
import net.java.stun4j.attribute.Attribute;
import net.java.stun4j.attribute.AttributeDecoder;
import net.java.stun4j.attribute.UnknownAttributesAttribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a STUN message. STUN messages are TLV (type-length-value)
 * encoded using big endian (network ordered) binary.  All STUN messages start
 * with a STUN header, followed by a STUN payload.  The payload is a series of
 * STUN attributes, the set of which depends on the message type.  The STUN
 * header contains a STUN message type, transaction ID, and length.
 *
 * <p>Organisation: Louis Pasteur University, Strasbourg, France</p>
 * 					<p>Network Research Team (http://www-r2.u-strasbg.fr)</p>
 * @author Emil Ivov
 * @version 0.1
 */

public abstract class Message
    {
    
    private static final Log LOG = LogFactory.getLog(Message.class);
    
    public static final char BINDING_REQUEST               = 0x0001;
    public static final char BINDING_RESPONSE              = 0x0101;
    public static final char BINDING_ERROR_RESPONSE        = 0x0111;
    public static final char SHARED_SECRET_REQUEST         = 0x0002;
    public static final char SHARED_SECRET_RESPONSE        = 0x0102;
    public static final char SHARED_SECRET_ERROR_RESPONSE  = 0x0112;

    //Message fields
    /**
     * The length of Stun Message Headers in byres
     * = len(Type) + len(DataLength) + len(Transaction ID).
     */
    public static final byte HEADER_LENGTH = 20;

    /**
     * Indicates the type of the message. The message type can be Binding Request,
     * Binding Response, Binding Error Response, Shared Secret Request, Shared
     * Secret Response, or Shared Secret Error Response.
     */
    protected char messageType = 0x0000;

    /**
     * The transaction ID is used to correlate requests and responses.
     */
    protected byte[] transactionID = null;

    /**
     * The length of the transaction id (in bytes).
     */
    public static final byte TRANSACTION_ID_LENGTH = 16;


    /**
     * The list of attributes contained by the message. We are using a hastable
     * rather than a uni-dimensional list, in order to facilitate attribute
     * search (even though it introduces some redundancies). Order is important
     * so we'll be using a LinkedHashMap
     */
    //not sure this is the best solution but I'm trying to keep entry order
    protected LinkedHashMap attributes = new LinkedHashMap();

    /**
     * Desribes which attributes are present in which messages.  An
     * M indicates that inclusion of the attribute in the message is
     * mandatory, O means its optional, C means it's conditional based on
     * some other aspect of the message, and N/A means that the attribute is
     * not applicable to that message type.
     *
     *
     *                                         Binding  Shared  Shared  Shared        <br/>
     *                       Binding  Binding  Error    Secret  Secret  Secret        <br/>
     *   Att.                Req.     Resp.    Resp.    Req.    Resp.   Error         <br/>
     *                                                                  Resp.         <br/>
     *   _____________________________________________________________________        <br/>
     *   MAPPED-ADDRESS      N/A      M        N/A      N/A     N/A     N/A           <br/>
     *   RESPONSE-ADDRESS    O        N/A      N/A      N/A     N/A     N/A           <br/>
     *   CHANGE-REQUEST      O        N/A      N/A      N/A     N/A     N/A           <br/>
     *   SOURCE-ADDRESS      N/A      M        N/A      N/A     N/A     N/A           <br/>
     *   CHANGED-ADDRESS     N/A      M        N/A      N/A     N/A     N/A           <br/>
     *   USERNAME            O        N/A      N/A      N/A     M       N/A           <br/>
     *   PASSWORD            N/A      N/A      N/A      N/A     M       N/A           <br/>
     *   MESSAGE-INTEGRITY   O        O        N/A      N/A     N/A     N/A           <br/>
     *   ERROR-CODE          N/A      N/A      M        N/A     N/A     M             <br/>
     *   UNKNOWN-ATTRIBUTES  N/A      N/A      C        N/A     N/A     C             <br/>
     *   REFLECTED-FROM      N/A      C        N/A      N/A     N/A     N/A           <br/>
     *
     *
     */
    public static final byte N_A = 0;
    public static final byte C   = 1;
    public static final byte O   = 2;
    public static final byte M   = 3;

    //Message indices
    protected static final byte BINDING_REQUEST_PRESENTITY_INDEX              = 0;
    protected static final byte BINDING_RESPONSE_PRESENTITY_INDEX             = 1;
    protected static final byte BINDING_ERROR_RESPONSE_PRESENTITY_INDEX       = 2;
    protected static final byte SHARED_SECRET_REQUEST_PRESENTITY_INDEX        = 3;
    protected static final byte SHARED_SECRET_RESPONSE_PRESENTITY_INDEX       = 4;
    protected static final byte SHARED_SECRET_ERROR_RESPONSE_PRESENTITY_INDEX = 5;


    protected final static byte attributePresentities[][] = new byte[][]{
    //                                            Binding   Shared   Shared   Shared
    //                        Binding   Binding   Error     Secret   Secret   Secret
    //  Att.                  Req.      Resp.     Resp.     Req.     Resp.    Error
    //                                                                        Resp.
    //  _______________________________________________________________________
      /*MAPPED-ADDRESS*/    { N_A,      M,        N_A,      N_A,     N_A,     N_A},
      /*RESPONSE-ADDRESS*/  { O,        N_A,      N_A,      N_A,     N_A,     N_A},
      /*CHANGE-REQUEST*/    { O,        N_A,      N_A,      N_A,     N_A,     N_A},
      /*SOURCE-ADDRESS*/    { N_A,      M,        N_A,      N_A,     N_A,     N_A},
      /*CHANGED-ADDRESS*/   { N_A,      M,        N_A,      N_A,     N_A,     N_A},
      /*USERNAME*/          { O,        N_A,      N_A,      N_A,     M,       N_A},
      /*PASSWORD*/          { N_A,      N_A,      N_A,      N_A,     M,       N_A},
      /*MESSAGE-INTEGRITY*/ { O,        O,        N_A,      N_A,     N_A,     N_A},
      /*ERROR-CODE*/        { N_A,      N_A,      M,        N_A,     N_A,     M},
      /*UNKNOWN-ATTRIBUTES*/{ N_A,      N_A,      C,        N_A,     N_A,     C},
      /*REFLECTED-FROM*/    { N_A,      C,        N_A,      N_A,     N_A,     N_A}};




    /**
     * Creates an empty STUN Mesage.
     */
    protected Message()
    {
    }

    /**
     * Returns the length of this message's body.
     * @return the length of the data in this message.
     */
    public char getDataLength()
    {
        char length = 0;
        Attribute att = null;

        Iterator iter = attributes.entrySet().iterator();
        while (iter.hasNext()) {
            att = (Attribute)((Map.Entry)iter.next()).getValue();
            length += att.getDataLength() + Attribute.HEADER_LENGTH;
        }

        return length;
    }

    /**
     * Adds the specified attribute to this message. If an attribute with that
     * name was already added, it would be replaced.
     * @param attribute the attribute to add to this message.
     * @throws StunException if the message cannot contain
     * such an attribute.
     */
    public void addAttribute(Attribute attribute)
        throws StunException
    {
        Character attributeType = new Character(attribute.getAttributeType());

        if (getAttributePresentity(attributeType.charValue()) == N_A)
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                                    "The attribute "
                                    + attribute.getName()
                                    + " is not allowed in a "
                                    + getName());

        attributes.put(attributeType, attribute);
    }

    /**
     * Returns the attribute with the specified type or null if no such
     * attribute exists.
     *
     * @param attributeType the type of the attribute
     * @return the attribute with the specified type or null if no such attribute
     * exists
     */
    public Attribute getAttribute(char attributeType)
    {
        return (Attribute)attributes.get(new Character(attributeType));
    }

    /*
     * Returns an enumeration containing all message attributes.
     * @return an enumeration containing all message attributes..
     */
    /*
    public Iterator getAttributes()
    {
        return attributes.entrySet().iterator();
    }
    */

    /**
     * Removes the specified attribute.
     * @param attributeType the attribute to remove.
     */
    public void removeAttribute(char attributeType)
    {
        attributes.remove(new Character(attributeType));
    }

    /**
     * Returns the number of attributes, currently contained by the message.
     * @return the number of attributes, currently contained by the message.
     */
    public int getAttributeCount()
    {
        return  attributes.size();
    }



    /**
     * Sets this message's type to be messageType. Method is package access
     * as it should not permit changing the type of message once it has been
     * initialized (could provoke attribute discrepancies). Called by
     * messageFactory.
     * @param messageType the message type.
     * @throws StunException ILLEGAL_ARGUMENT if message type is not valid in
     * the current context (e.g. when trying to set a Response type to a Request
     * and vice versa)
     */
    protected void setMessageType(char messageType)
        throws StunException
    {
        this.messageType = messageType;
    }

    /**
     * The message type of this message.
     * @return the message type of the message.
     */
    public char getMessageType()
    {
        return messageType;
    }

    /**
     * Copies the specified tranID and sets it as this message's transactionID.
     * @param tranID the transaction id to set in this message.
     * @throws StunException ILLEGAL_ARGUMENT if the transaction id is not valid.
     */
    public void setTransactionID(byte[] tranID)
        throws StunException
    {
        if(tranID == null
           || tranID.length != TRANSACTION_ID_LENGTH)
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                                    "Invalid transaction id");

        this.transactionID = new byte[TRANSACTION_ID_LENGTH];
        System.arraycopy(tranID, 0,
                         this.transactionID, 0, TRANSACTION_ID_LENGTH);
    }

    /**
     * Returns a reference to this message's transaction id.
     * @return a reference to this message's transaction id.
     */
    public byte[] getTransactionID()
    {
        return this.transactionID;
    }

    /**
     * Returns whether an attribute could be present in this message.
     * @param attributeID the id of the attribute to check .
     * @return Message.N_A - for not applicable <br/>
     *         Message.C   - for case depending <br/>
     *         Message.N_A - for not applicable <br/>
     */
    protected byte getAttributePresentity(char attributeID)
    {
        byte msgIndex = -1;
        switch (messageType)
        {
            case BINDING_REQUEST:              msgIndex = BINDING_REQUEST_PRESENTITY_INDEX; break;
            case BINDING_RESPONSE:             msgIndex = BINDING_RESPONSE_PRESENTITY_INDEX; break;
            case BINDING_ERROR_RESPONSE:       msgIndex = BINDING_ERROR_RESPONSE_PRESENTITY_INDEX; break;
            case SHARED_SECRET_REQUEST:        msgIndex = SHARED_SECRET_REQUEST_PRESENTITY_INDEX; break;
            case SHARED_SECRET_RESPONSE:       msgIndex = SHARED_SECRET_RESPONSE_PRESENTITY_INDEX; break;
            case SHARED_SECRET_ERROR_RESPONSE: msgIndex = SHARED_SECRET_ERROR_RESPONSE_PRESENTITY_INDEX; break;
        }

        LOG.trace("Getting attribute appropriateness for message: "+getName(messageType));
        //LOG.trace("Attribute ID: "+attributeID);
        return attributePresentities[ attributeID - 1 ][ msgIndex ];
    }

    /**
     * Returns the human readable name of this message. Message names do
     * not really matter from the protocol point of view. They are only used
     * for debugging and readability.
     * @return this message's name.
     */
    public String getName()
        {
        return getName(messageType);
        }
    
    private static String getName(final char messageTypeParam)
        {
        switch (messageTypeParam)
            {
            case BINDING_REQUEST:              return "BINDING REQUEST";
            case BINDING_RESPONSE:             return "BINDING RESPONSE";
            case BINDING_ERROR_RESPONSE:       return "BINDING ERROR RESPONSE";
            case SHARED_SECRET_REQUEST:        return "SHARED SECRET REQUEST";
            case SHARED_SECRET_RESPONSE:       return "SHARED SECRET RESPONSE";
            case SHARED_SECRET_ERROR_RESPONSE: return "SHARED SECRET ERROR RESPONSE";
            }
    
        return "UNKNOWN MESSAGE";    
        }

    /**
     * Compares two STUN Messages. Messages are considered equal when their
     * type, length, and all their attributes are equal.
     *
     * @param obj the object to compare this message with.
     * @return true if the messages are equal and false otherwise.
     */
    public boolean equals(Object obj)
        {
        if(!(obj instanceof Message)
           || obj == null)
            return false;

        if(obj == this)
            return true;

        Message msg = (Message) obj;
        if( msg.getMessageType()   != getMessageType())
            return false;
        if(msg.getDataLength() != getDataLength())
            return false;

        //compare attributes
        Iterator iter = attributes.entrySet().iterator();
        while (iter.hasNext()) {
            Attribute localAtt = (Attribute)((Map.Entry)iter.next()).getValue();

            if(!localAtt.equals(msg.getAttribute(localAtt.getAttributeType())))
                return false;
        }

        return true;
    }


    /**
     * Returns a binary representation of this message.
     * @return a binary representation of this message.
     * @throws StunException if the message does not have all required
     * attributes.
     */
    public byte[] encode() throws StunException
        {
        //make sure we have everything necessary to encode a proper message
        validateAttributePresentity();
        char dataLength = getDataLength();
        byte binMsg[] = new byte[HEADER_LENGTH + dataLength];
        int offset    = 0;

        binMsg[offset++] = (byte)(getMessageType()>>8);
        binMsg[offset++] = (byte)(getMessageType()&0xFF);

        binMsg[offset++] = (byte)(dataLength >> 8);
        binMsg[offset++] = (byte)(dataLength & 0xFF);

        System.arraycopy(getTransactionID(), 0, binMsg, offset, TRANSACTION_ID_LENGTH);
        offset+=TRANSACTION_ID_LENGTH;

        Iterator iter = attributes.entrySet().iterator();
        while (iter.hasNext()) 
            {
            Attribute attribute = (Attribute)((Map.Entry)iter.next()).getValue();

            byte[] attBinValue = attribute.encode();
            System.arraycopy(attBinValue, 0, binMsg, offset, attBinValue.length);
            offset += attBinValue.length;
            }

        return binMsg;
        }

    /**
     * Constructs a message from its binary representation.
     * @param binMessage the binary array that contains the encoded message
     * @param offset the index where the message starts.
     * @param arrayLen the length of the message
     * @return a Message object constructed from the binMessage array
     * @throws StunException ILLEGAL_ARGUMENT if one or more of the arguments
     * have invalid values.
     */
    public static Message decode(final byte binMessage[], char offset, 
        char arrayLen) throws StunException
        {
        if (binMessage == null)
            {
            LOG.error("Null byte array");
            throw new NullPointerException("null binary data");
            }
        arrayLen = (char)Math.min(binMessage.length, arrayLen);

        if (arrayLen - offset < Message.HEADER_LENGTH)
            {
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                "The given binary array is not a valid StunMessage");
            }

        final char messageType = 
            (char)((binMessage[offset++]<<8) | (binMessage[offset++]&0xFF));
        
        LOG.trace("Reading message type: "+getName(messageType));
        final Message message;
        if (Message.isResponseType(messageType))
            {
            LOG.trace("Reading response...");
            message = new Response();
            }
        else
            {
            message = new Request();
            }
        message.setMessageType(messageType);

        final int length = 
            (char)((binMessage[offset++]<<8) | (binMessage[offset++]&0xFF));

        LOG.trace("Message length is: "+length);
        
        if(arrayLen - offset - TRANSACTION_ID_LENGTH < length)
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                                    "The given binary array does not seem to "
                                    +"contain a whole StunMessage");

        final byte tranID[] = new byte[TRANSACTION_ID_LENGTH];
        System.arraycopy(binMessage, offset, tranID, 0, TRANSACTION_ID_LENGTH);
        message.setTransactionID(tranID);
        offset+=TRANSACTION_ID_LENGTH;

        while(offset - Message.HEADER_LENGTH < length)
            {
            final Attribute att = 
                AttributeDecoder.decode(binMessage, offset, 
                    (char)(length - offset));
            
            // Ignore unknown attributes...
            if (att != null && !(att instanceof UnknownAttributesAttribute))
                {
                message.addAttribute(att);
                }
            offset += att.getDataLength() + Attribute.HEADER_LENGTH;
            }

        return message;
        }

    /**
     * Verify that the message has all obligatory attributes and throw an
     * exception if this is not the case.
     *
     * @return true if the message has all obligatory attributes, false
     * otherwise.
     * @throws StunException (ILLEGAL_STATE)if the message does not have all
     * required attributes.
     */
    protected void validateAttributePresentity()
        throws StunException
        {
        for(char i = Attribute.MAPPED_ADDRESS; i < Attribute.REFLECTED_FROM; i++)
            if(getAttributePresentity(i) == M && getAttribute(i) == null)
                throw new StunException(StunException.ILLEGAL_STATE,
                                        "A mandatory attribute (type="
                                        +(int)i
                                        + ") is missing!");

        }

    /**
     * Determines whether type could be the type of a STUN Response (as opposed
     * to STUN Request).
     * @param type the type to test.
     * @return true if type is a valid response type.
     */
    public static boolean isResponseType(char type)
        {
        return (((type >> 8) & 1) != 0);
        }

    /**
     * Determines whether type could be the type of a STUN Request (as opposed
     * to STUN Response).
     * @param type the type to test.
     * @return true if type is a valid request type.
     */
    public static boolean isRequestType(char type)
        {
        return !isResponseType(type);
        }
    }

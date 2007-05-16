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
package net.java.stun4j.attribute;

import net.java.stun4j.StunException;
import net.java.stun4j.StunAddress;
import java.util.Arrays;

/**
 * This class is used to represent Stun attributes that contain an address. Such
 * attributes are:
 *
 * MAPPED-ADDRESS <br/>
 * RESPONSE-ADDRESS <br/>
 * SOURCE-ADDRESS <br/>
 * CHANGED-ADDRESS <br/>
 * REFLECTED-FROM <br/>
 *
 * The different attributes are distinguished by the attributeType of
 * net.java.stun4j.attribute.Attribute.
 *
 * Address attributes indicate the mapped IP address and
 * port.  They consist of an eight bit address family, and a sixteen bit
 * port, followed by a fixed length value representing the IP address.
 *
 *  0                   1                   2                   3          <br/>
 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1        <br/>
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+       <br/>
 * |x x x x x x x x|    Family     |           Port                |       <br/>
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+       <br/>
 * |                             Address                           |       <br/>
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+       <br/>
 *                                                                         <br/>
 * The port is a network byte ordered representation of the mapped port.
 * The address family is always 0x01, corresponding to IPv4.  The first
 * 8 bits of the MAPPED-ADDRESS are ignored, for the purposes of
 * aligning parameters on natural boundaries.  The IPv4 address is 32
 * bits.
 *
 *
 * <p>Organisation: Louis Pasteur University, Strasbourg, France</p>
 * <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */
abstract class AddressAttribute extends Attribute
{

    /**
     * The family of the address contained by this attribute. The address family
     * is always 0x01, corresponding to IPv4.
     */
     static final byte family = 0x01;

     /**
      * The address represented by this message;
      */
     protected StunAddress address = null;

     /**
      * The length of the data contained by this attribute.
      */
     public static final char DATA_LENGTH = 8;

    /**
     * Constructs an address attribute with the specified type.
     *
     * @param attributeType the type of the address attribute.
     */
    AddressAttribute(char attributeType)
    {
        super(attributeType);
    }
    /**
     * Verifies that type is a valid address attribute type.
     * @param type the type to test
     * @return true if the type is a valid address attribute type and false
     * otherwise
     */
    private boolean isTypeValid(char type)
    {
        return (type == MAPPED_ADDRESS || type == RESPONSE_ADDRESS
                || type == SOURCE_ADDRESS || type == CHANGED_ADDRESS
                || type == REFLECTED_FROM);

    }

    /**
     * Sets it as this attribute's type.
     *
     * @param type the new type of the attribute.
     */
    protected void setAttributeType(char  type)
    {
        if (!isTypeValid(type))
            throw new IllegalArgumentException(((int)type) + "is not a valid address attribute!");

        super.setAttributeType(type);
    }

    /**
     * Returns the human readable name of this attribute. Attribute names do
     * not really matter from the protocol point of view. They are only used
     * for debugging and readability.
     * @return this attribute's name.
     */
    public String getName()
    {
        switch(getAttributeType())
        {
            case MAPPED_ADDRESS:   return MappedAddressAttribute.NAME;
            case RESPONSE_ADDRESS: return ResponseAddressAttribute.NAME;
            case SOURCE_ADDRESS:   return SourceAddressAttribute.NAME;
            case CHANGED_ADDRESS:  return ChangedAddressAttribute.NAME;
            case REFLECTED_FROM:   return ReflectedFromAttribute.NAME;
        }

        return "UNKNOWN MESSAGE";
    }

   /**
    * Compares two STUN Attributes. Attributeas are considered equal when their
    * type, length, and all data are the same.
    *
    * @param obj the object to compare this attribute with.
    * @return true if the attributes are equal and false otherwise.
    */
    public boolean equals(Object obj)
    {
        if (! (obj instanceof AddressAttribute)
            || obj == null)
            return false;

        if (obj == this)
            return true;

        AddressAttribute att = (AddressAttribute) obj;
        if (att.getAttributeType() != getAttributeType()
            || att.getDataLength() != getDataLength()
            //compare data
            || att.getFamily()     != getFamily()
            || (att.getAddress()   != null
                && !address.equals(att.getAddress()))
            )
            return false;

        //addresses
        if( att.getAddress() == null && getAddress() == null)
            return true;

        return true;
    }

    /**
     * Returns the length of this attribute's body.
     * @return the length of this attribute's value (8 bytes).
     */
    public char getDataLength()
    {
        return this.DATA_LENGTH;
    }

    /**
     * Returns a binary representation of this attribute.
     * @return a binary representation of this attribute.
     */
    public byte[] encode()
    {
        char type = getAttributeType();
        if (!isTypeValid(type))
            throw new IllegalStateException(((int)type) + "is not a valid address attribute!");
        byte binValue[] = new byte[HEADER_LENGTH + DATA_LENGTH];

        //Type
        binValue[0] = (byte)(type>>8);
        binValue[1] = (byte)(type&0x00FF);
        //Length
        binValue[2] = (byte)(getDataLength()>>8);
        binValue[3] = (byte)(getDataLength()&0x00FF);
        //Not used
        binValue[4] = 0x00;
        //Family
        binValue[5] = getFamily();
        //port
        binValue[6] = (byte)(getPort()>>8);
        binValue[7] = (byte)(getPort()&0x00FF);
        //address
        binValue[8]  = getAddressBytes()[0];
        binValue[9]  = getAddressBytes()[1];
        binValue[10] = getAddressBytes()[2];
        binValue[11] = getAddressBytes()[3];

        return binValue;
    }

    /**
     * Sets address to be the address transported by this attribute.
     * @param address that this attribute should encapsulate.
     */
    public void setAddress(StunAddress address)
    {
        this.address = address;
    }

    /**
     * Returns the address encapsulated by this attribute.
     * @return the address encapsulated by this attribute.
     */
    public StunAddress getAddress()
    {
        return address;
    }

    public byte[] getAddressBytes()
    {
        return address.getAddressBytes();
    }

    /**
     * Returns the family that the this.address belongs to.
     * @return the family that the this.address belongs to.
     */
    public byte getFamily()
    {
        return family;
    }

    /**
     * Returns the port associated with the address contained by the attribute.
     * @return the port associated with the address contained by the attribute.
     */
    public char getPort()
    {
        return address.getPort();
    }

    /**
      * Sets this attribute's fields according to attributeValue array.
      *
      * @param attributeValue a binary array containing this attribute's field
      *                       values and NOT containing the attribute header.
      * @param offset the position where attribute values begin (most often
      * 				 offset is equal to the index of the first byte after
      * 				 length)
      * @param length the length of the binary array.
      * @throws StunException if attrubteValue contains invalid data.
      */
    void decodeAttributeBody(byte[] attributeValue, char offset, char length)
        throws StunException
    {
        //skip through family and padding
        offset += 2;
        //port
        char port = ((char)((attributeValue[offset++] << 8 ) | (attributeValue[offset++]&0xFF) ));
        //address
        byte address[] = ( new byte[]{attributeValue[offset++], attributeValue[offset++],
                               attributeValue[offset++], attributeValue[offset++]});

        setAddress(new StunAddress(address, port));

    }

}
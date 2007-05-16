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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class provides utilities for decoding a binary stream into an Attribute
 * class.
 *
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organisation: Louis Pasteur University, Strasbourg, France</p>
 *                   <p>Network Research Team (http://www-r2.u-strasbg.fr)</p></p>
 * @author Emil Ivov
 * @version 0.1
 */

public class AttributeDecoder
    {
    
    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(AttributeDecoder.class);
    
    /**
     * Decodes the specified binary array and returns the corresponding
     * attribute object.
     * @param bytes the binary array that should be decoded.
     * @param offset the index where the message starts.
     * @param length the number of bytes that the message is long.
     * @return An object representing the attribute encoded in bytes or null if
     *         the attribute was not recognized.
     * @throws StunException if bytes does is not a valid STUN attribute.
     */
    public static Attribute decode(final byte bytes[], char offset, char length)
        throws StunException
        {
        LOG.trace("Decoding attribute...");
        if (bytes == null)
            {
            LOG.error("Null attribute bytes...");
            throw new NullPointerException("Cannot process null bytes");
            }
        if  (bytes.length < Attribute.HEADER_LENGTH)
            {
            LOG.warn("Unexpected message length: "+bytes.length);
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                "Could not decode the specified binary array.");
            }

        //Discover attribute type
        char attributeType   = (char)((bytes[offset]<<8)|bytes[offset + 1]);
        char attributeLength = (char)((bytes[offset + 2]<<8)|bytes[offset + 3]);

        if (attributeLength > bytes.length - offset)
            {
            LOG.warn("Unexpected length: "+attributeLength);
            throw new StunException(StunException.ILLEGAL_ARGUMENT,
                "The indicated attribute length ("+attributeLength+") "
                +"does not match the length of the passed binary array");
            }

        final Attribute decodedAttribute;

        switch(attributeType)
            {
            case Attribute.CHANGE_REQUEST:
                LOG.trace("Processing change request...");
                decodedAttribute = new ChangeRequestAttribute(); break;
            case Attribute.CHANGED_ADDRESS:
                LOG.trace("Processing changed address...");
                decodedAttribute = new ChangedAddressAttribute(); break;
            case Attribute.MAPPED_ADDRESS:
                LOG.trace("Processing mapped address...");
                decodedAttribute = new MappedAddressAttribute(); break;
            case Attribute.ERROR_CODE:
                LOG.trace("Processing error code...");
                decodedAttribute = new ErrorCodeAttribute(); break;
            case Attribute.MESSAGE_INTEGRITY:
                LOG.trace("Processing message integrity...");
                throw new UnsupportedOperationException(
                    "The MESSAGE-INTEGRITY Attribute is not yet implemented.");
            case Attribute.PASSWORD:
                LOG.trace("Processing password...");
                throw new UnsupportedOperationException(
                    "The PASSWORD Attribute is not yet implemented.");
            case Attribute.REFLECTED_FROM:
                LOG.trace("Processing reflected from...");  
                decodedAttribute = new ReflectedFromAttribute(); break;
            case Attribute.RESPONSE_ADDRESS:
                LOG.trace("Processing response address...");
                decodedAttribute = new ResponseAddressAttribute(); break;
            case Attribute.SOURCE_ADDRESS:
                LOG.trace("Processing source address...");
                decodedAttribute = new SourceAddressAttribute(); break;
            case Attribute.UNKNOWN_ATTRIBUTES:
                LOG.trace("Processing unknown...");
                decodedAttribute = new UnknownAttributesAttribute(); break;
            case Attribute.USERNAME:
                LOG.trace("Processing username...");
                throw new UnsupportedOperationException(
                    "The USERNAME Attribute is not yet implemented.");

            //According to rfc3489 we should silently ignore unknown attributes.
            default: 
                LOG.debug("Unrecognized attribute type: "+attributeType);
                decodedAttribute = new UnknownAttributesAttribute(); break; 
                //return null;
            }

        decodedAttribute.setAttributeType(attributeType);

        decodedAttribute.decodeAttributeBody(bytes, 
            (char)(Attribute.HEADER_LENGTH + offset), attributeLength);

        return decodedAttribute;
        }
    }
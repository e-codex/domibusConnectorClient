package eu.domibus.connector.client.mapping;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import eu.domibus.connector.client.mapping.exception.DomibusConnectorClientContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * 
 * Util class with static final methods to help prepare and validate {@link DomibusConnectorMessageType} message objects for the mapping of
 * the businessContent with implementation of {@link DomibusConnectorClientContentMapper}.
 * 
 * @author riederb
 *
 */
public final class DomibusConnectorClientContentMapperUtil {

	/**
	 * Method to convert a Stream of XML content into bytes using a {@link Transformer}.
	 * 
	 * @param xmlInput - the Stream of the XML as {@link Source}.
	 * @return the XML as bytes.
	 * @throws DomibusConnectorClientContentMapperException
	 */
	public static final byte[] convertXmlSourceToByteArray(Source xmlInput) throws DomibusConnectorClientContentMapperException{
		try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            StreamResult xmlOutput = new StreamResult(new OutputStreamWriter(output));
            transformer.transform(xmlInput, xmlOutput);
            return output.toByteArray();
        } catch (TransformerException e) {
            throw new DomibusConnectorClientContentMapperException("Exception occured during transforming xml into byte[]", e);
        }
		
	}
	
	/**
	 * Validates a {@link DomibusConnectorMessageType} message object in regards of the business content.
	 * Checks if a {@link DomibusConnectorMessageContentType} object is inside the message, if the xmlContent of the contentType
	 * is filled, if the xmlContent can be converted from Stream to bytes using the method {@link #convertXmlSourceToByteArray(Source)}
	 * and if the xmlContent afterwards is not null or empty. 
	 * 
	 * @param message
	 * @throws DomibusConnectorClientContentMapperException
	 */
	public static final void validateBusinessContent(DomibusConnectorMessageType message) throws DomibusConnectorClientContentMapperException {
    	try {
    		DomibusConnectorMessageContentType content = message.getMessageContent();
    		Source xmlContent = content.getXmlContent();
    		byte[] businessContent = null;
    		try {
    			businessContent = convertXmlSourceToByteArray(xmlContent);
    		}catch(DomibusConnectorClientContentMapperException ex) {
    			throw new DomibusConnectorClientContentMapperException("XML Source object could not be transformed to bytes!", ex);
    		}
    		if(businessContent!=null && businessContent.length > 0) {
    			return;
    		}else {
    			throw new DomibusConnectorClientContentMapperException("businessContent after transformation null or empty!");
    		}
    	}catch(NullPointerException e) {
    		throw new DomibusConnectorClientContentMapperException("The message is not properly constructed.");
    	}
    	
    }
}

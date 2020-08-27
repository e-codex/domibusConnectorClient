package eu.domibus.connector.client.schema.validation;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface that may be implemented to validate a messages' business content against a schema.
 * If an implementation is present, it is called when a message is submitted or received before the content mapping takes place.
 * 
 * @author riederb
 *
 */
public interface DCCBeforeMappingSchemaValidator {
	
	/**
	 * Method to validate the business content XML before the mapping takes place.
	 * 
	 * @param message - The message object holding the business content XML at message/MessageContent/contentXML
	 * 
	 * @return a ValidationResult holding single results.
	 */
	ValidationResult validateBusinessContentBeforeMapping(DomibusConnectorMessageType message);
}

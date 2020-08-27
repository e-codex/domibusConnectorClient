package eu.domibus.connector.client.schema.validation;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface that may be implemented to validate a messages' business content against a schema.
 * If an implementation is present, it is called when a message is submitted or received after the content mapping took place.
 * 
 * @author riederb
 *
 */
public interface DCCAfterMappingSchemaValidator {

	/**
	 * Method to validate the business content XML after the mapping took place.
	 * 
	 * @param message - The message object holding the business content XML at message/MessageContent/contentXML
	 * 
	 * @return a ValidationResult holding single results.
	 */
	ValidationResult validateBusinessContentAfterMapping(DomibusConnectorMessageType message);
	
}

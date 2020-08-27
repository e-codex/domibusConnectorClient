package eu.domibus.connector.client.schema.validation;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DCCAfterMappingSchemaValidator {

	ValidationResult validateBusinessContentAfterMapping(DomibusConnectorMessageType message);
	
}

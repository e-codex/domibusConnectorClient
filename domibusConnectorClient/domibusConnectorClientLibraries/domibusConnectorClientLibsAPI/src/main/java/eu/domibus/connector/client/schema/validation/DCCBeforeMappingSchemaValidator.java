package eu.domibus.connector.client.schema.validation;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DCCBeforeMappingSchemaValidator {
	
	ValidationResult validateBusinessContentBeforeMapping(DomibusConnectorMessageType message);
}

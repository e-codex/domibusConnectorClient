package eu.domibus.connector.client.schema.validation;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientSchemaValidator {

	ValidationResult validateBusinessContentAfterMapping(DomibusConnectorMessageType message);
	
	ValidationResult validateBusinessContentBeforeMapping(DomibusConnectorMessageType message);
}

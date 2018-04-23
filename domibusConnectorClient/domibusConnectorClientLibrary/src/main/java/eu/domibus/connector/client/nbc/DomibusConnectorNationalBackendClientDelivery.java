package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

import java.util.List;

public interface DomibusConnectorNationalBackendClientDelivery {

	public void processMessageFromConnector(DomibusConnectorMessageType message) throws DomibusConnectorNationalBackendClientException, ImplementationMissingException;

}

package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

import java.util.List;

public interface DomibusConnectorNationalBackendPushClient {

	public void processMessagesFromConnector(List<DomibusConnectorMessageType> messages) throws DomibusConnectorNationalBackendClientException,
    ImplementationMissingException;

}

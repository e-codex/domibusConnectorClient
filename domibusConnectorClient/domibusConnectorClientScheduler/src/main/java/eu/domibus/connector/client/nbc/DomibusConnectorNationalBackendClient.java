package eu.domibus.connector.client.nbc;

import java.util.List;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorNationalBackendClient {

	public void processMessagesFromConnector(List<DomibusConnectorMessageType> messages)throws DomibusConnectorNationalBackendClientException,
    ImplementationMissingException;
	
	public List<DomibusConnectorMessageType> checkForMessagesOnNationalBackend()throws DomibusConnectorNationalBackendClientException,
    ImplementationMissingException;
	
}

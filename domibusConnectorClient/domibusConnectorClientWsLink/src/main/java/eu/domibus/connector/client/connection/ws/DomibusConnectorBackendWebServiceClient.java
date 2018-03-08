package eu.domibus.connector.client.connection.ws;

import java.util.List;

import eu.domibus.connector.client.connection.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorBackendWebServiceClient {

	public void submitMessage(DomibusConnectorMessageType messageDTO) throws DomibusConnectorBackendWebServiceClientException;
	
	public List<DomibusConnectorMessageType> requestMessages() throws DomibusConnectorBackendWebServiceClientException;
	
}

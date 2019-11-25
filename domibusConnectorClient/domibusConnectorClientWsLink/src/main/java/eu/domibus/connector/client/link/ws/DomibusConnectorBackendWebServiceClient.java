package eu.domibus.connector.client.link.ws;

import java.util.List;

import eu.domibus.connector.client.link.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorBackendWebServiceClient {

	public DomibusConnectorMessageResponseType submitMessage(DomibusConnectorMessageType messageDTO) throws DomibusConnectorBackendWebServiceClientException;
	
	public List<DomibusConnectorMessageType> requestMessages() throws DomibusConnectorBackendWebServiceClientException;
	
}

package eu.domibus.connector.client.service;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

import java.util.List;

public interface DomibusConnectorClientService {

	public List<DomibusConnectorMessageType> requestMessagesFromConnector()throws DomibusConnectorClientException;
	
	public void submitMessageToConnector(DomibusConnectorMessageType message)throws DomibusConnectorClientException;
}

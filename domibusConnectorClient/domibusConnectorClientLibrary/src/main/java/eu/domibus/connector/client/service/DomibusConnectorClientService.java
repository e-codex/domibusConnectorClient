package eu.domibus.connector.client.service;

import java.util.List;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientService {

	public List<DomibusConnectorMessageType> requestMessagesFromConnector()throws DomibusConnectorClientException;
	
	public void submitMessageToConnector(DomibusConnectorMessageType message)throws DomibusConnectorClientException;
}

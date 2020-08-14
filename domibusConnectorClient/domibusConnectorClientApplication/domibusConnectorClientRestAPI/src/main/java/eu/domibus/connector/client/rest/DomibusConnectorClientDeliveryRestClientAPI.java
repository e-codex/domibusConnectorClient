package eu.domibus.connector.client.rest;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientDeliveryRestClientAPI {

//	void deliverNewMessageFromConnectorClientToBackend(DomibusConnectorMessageType newMessage) throws Exception;
	
	void deliverNewConfirmationFromConnectorClientToBackend(DomibusConnectorMessageType newMessage) throws Exception;

	void deliverNewMessageFromConnectorClientToBackend(DomibusConnectorClientMessage msg) throws Exception;
}

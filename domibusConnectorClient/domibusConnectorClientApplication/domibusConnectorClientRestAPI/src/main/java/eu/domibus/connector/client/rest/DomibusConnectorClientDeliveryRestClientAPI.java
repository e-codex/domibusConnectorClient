package eu.domibus.connector.client.rest;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * This interface should be used if a backend application should receive messages via push. In this case the backend application
 * must implement a REST service that implements this interface. Certain properties have to be set properly that the domibusConnectorClient
 * can initialize a REST client that also implements this interface.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientDeliveryRestClientAPI {
	
	/**
	 * This method is called by the domibusConnectorClient to push a new confirmation to the backend application.
	 * 
	 * @param newMessage
	 * @throws Exception
	 */
	void deliverNewConfirmationFromConnectorClientToBackend(DomibusConnectorMessageType newMessage) throws Exception;

	/**
	 * This method is called by the domibusConnectorClient to push a new message to the backend application.
	 * 
	 * @param newMessage
	 * @throws Exception
	 */
	void deliverNewMessageFromConnectorClientToBackend(DomibusConnectorClientMessage msg) throws Exception;
}

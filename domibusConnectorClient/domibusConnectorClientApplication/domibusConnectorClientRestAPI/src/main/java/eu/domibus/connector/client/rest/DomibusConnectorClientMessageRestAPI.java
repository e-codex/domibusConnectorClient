package eu.domibus.connector.client.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

/**
 * This REST interface allows a backend application that is connected to the domibusConnectorClient to request messages and confirmations that have been
 * received by the domibusConnectorClient.
 * 
 * @author riederb
 *
 */
@RequestMapping("/messagerestservice")
public interface DomibusConnectorClientMessageRestAPI {

	/**
	 * This method returns all messages that have been successfully received and processed by the domibusConnectorClient.
	 * Therefore the message status of the messages requested by this method is RECEIVED. After collecting those messages, the message status
	 * changes to DELIVERED_BACKEND.
	 *  
	 * @return all messages with status RECEIVED.
	 */
	@GetMapping("/requestNewMessagesFromConnectorClient")
	DomibusConnectorClientMessageList requestNewMessagesFromConnectorClient();
	
	/**
	 * This method returns all messages and their confirmations attached with the message status REJECTED or CONFIRMED. In those states, a confirmation
	 * have been received from the domibusConnector. After collecting the messages, the status of those messages changes to CONFIRMATION_DELIVERED_BACKEND.
	 * 
	 * @return all messages with their confirmations with the status REJECTED or CONFIRMED.
	 */
	@GetMapping("/requestRejectedOrConfirmedMessagesFromConnectorClient")
	DomibusConnectorClientMessageList requestRejectedOrConfirmedMessagesFromConnectorClient();
}

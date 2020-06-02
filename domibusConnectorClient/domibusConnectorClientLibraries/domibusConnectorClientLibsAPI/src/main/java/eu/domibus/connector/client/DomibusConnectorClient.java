package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This is the interface containing methods to be implemented when the domibusConnectorClientLibrary is used. It is
 * the main link between the domibusConnectorClientWSLink and the domibusConnectorClientBackend.
 * 
 * @author Bernhard Rieder
 *
 */
public interface DomibusConnectorClient {
	
	/**
	 * This message submits a new message from the client to the connector.
	 * 
	 * @param message - The {@link DomibusConnectorMessageType} object containing the message information. 
	 * 					May be built using the {@link DomibusConnectorClientMessageBuilder}.
	 * @throws DomibusConnectorClientException if the submission fails
	 */
	public void submitNewMessageToConnector (
			DomibusConnectorMessageType message) throws DomibusConnectorClientException;

	
	/**
	 * Requests all new messages that are provided by the domibusConnector's backend. Before returning the received messages, all messages
	 * are mapped calling the configured implementation of {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper}. If no content mapper is configured, the default
	 * content mapper is called.
	 * 
	 * @return The {@link DomibusConnectorMessagesType} containing the new messages.
	 * @throws DomibusConnectorClientException if the fetching of new messages from the connector fails
	 */
	public DomibusConnectorMessagesType requestNewMessagesFromConnector () throws DomibusConnectorClientException;
	
	/**
	 * Triggers the submission of a message confirmation at the domibusConnector.
	 * 
	 * @param confirmationMessage - The message that contains the details and confirmationType that should be submitted. Can be generated 
	 * 								at the backend side using the {@link DomibusConnectorClientMessageBuilder}.
	 * @throws DomibusConnectorClientException if the triggering of the confirmation fails
	 */
	void triggerConfirmationForMessage(DomibusConnectorMessageType confirmationMessage)
			throws DomibusConnectorClientException;
	
	
	
}

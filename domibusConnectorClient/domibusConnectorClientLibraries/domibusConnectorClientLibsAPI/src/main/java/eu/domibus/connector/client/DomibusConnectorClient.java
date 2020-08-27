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
	 * Before submitting a message, the implementation of {@link eu.domibus.connector.client.schema.validation.DCCBeforeMappingSchemaValidator} is called if present.
	 * Then, the message is mapped calling the implementation of {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper} if present. 
	 * Last, the message gets again validated with an implementation of {@link eu.domibus.connector.client.schema.validation.DCCAfterMappingSchemaValidator} is called if present.
	 * 
	 * 
	 * @param message - The {@link DomibusConnectorMessageType} object containing the message information. 
	 * 					May be built using the {@link DomibusConnectorClientMessageBuilder}.
	 * @throws DomibusConnectorClientException if the submission fails
	 */
	public void submitNewMessageToConnector (
			DomibusConnectorMessageType message) throws DomibusConnectorClientException;

	
	/**
	 * Requests all new messages that are provided by the domibusConnector's backend. 
	 * Before returning a message, the implementation of {@link eu.domibus.connector.client.schema.validation.DCCBeforeMappingSchemaValidator} is called if present.
	 * Then, the message is mapped calling the implementation of {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper} if present. 
	 * Last, the message gets again validated with an implementation of {@link eu.domibus.connector.client.schema.validation.DCCAfterMappingSchemaValidator} is called if present.
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

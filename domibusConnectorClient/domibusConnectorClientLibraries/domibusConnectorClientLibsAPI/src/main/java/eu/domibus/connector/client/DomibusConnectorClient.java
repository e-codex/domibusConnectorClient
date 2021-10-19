package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DCCConnectorAcknowledgementException;
import eu.domibus.connector.client.exception.DCCContentMappingException;
import eu.domibus.connector.client.exception.DCCMessageDataInvalid;
import eu.domibus.connector.client.exception.DCCMessageValidationException;
import eu.domibus.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
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
	 * Before submitted, the {@link eu.domibus.connector.client.DomibusConnectorClientMessageHandler} is called.
	 * 
	 * @param message - The {@link DomibusConnectorMessageType} object containing the message information. 
	 * 					May be built using the {@link DomibusConnectorClientMessageBuilder}.
	 * @throws DomibusConnectorBackendWebServiceClientException if the web service client communicating with the domibusConnector throws an error
	 * @throws DCCConnectorAcknowledgementException if the {@link DomibsConnectorAcknowledgementType} is null or negative
	 * @throws DCCContentMappingException if the mapping of the content fails
	 * @throws DCCMessageValidationException if the schema validation of the message content fails
	 */
	public void submitNewMessageToConnector (
			DomibusConnectorMessageType message) throws DomibusConnectorBackendWebServiceClientException, DCCConnectorAcknowledgementException, DCCMessageValidationException, DCCContentMappingException;

	
	/**
	 * Requests all new messages that are provided by the domibusConnector's backend. 
	 * Before delivered, the {@link eu.domibus.connector.client.DomibusConnectorClientMessageHandler} is called for each message received.
	 * 
	 * @return The {@link DomibusConnectorMessagesType} containing the new messages.
	 * @throws DCCContentMappingException if the mapping of the content fails
	 * @throws DCCMessageValidationException if the schema validation of the message content fails
	 * @throws DomibusConnectorBackendWebServiceClientException if the web service client communicating with the domibusConnector throws an error
	 */
	public DomibusConnectorMessagesType requestNewMessagesFromConnector () throws DomibusConnectorClientException, DomibusConnectorBackendWebServiceClientException, DCCMessageValidationException, DCCContentMappingException;
	
	/**
	 * Triggers the submission of a message confirmation at the domibusConnector.
	 * 
	 * @param confirmationMessage - The message that contains the details and confirmationType that should be submitted. Can be generated 
	 * 								at the backend side using the {@link DomibusConnectorClientMessageBuilder}.
	 * @throws DomibusConnectorBackendWebServiceClientException if the web service client communicating with the domibusConnector throws an error
	 * @throws DCCConnectorAcknowledgementException if the {@link DomibsConnectorAcknowledgementType} is null or negative
	 * @throws DCCMessageDataInvalid 
	 */
	void triggerConfirmationForMessage(DomibusConnectorMessageType confirmationMessage)
			throws DCCMessageDataInvalid, DCCConnectorAcknowledgementException, DomibusConnectorBackendWebServiceClientException;
	
	
	
}

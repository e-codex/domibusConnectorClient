package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This is the interface containing methods to be implemented when the domibusConnectorClientLibrary is used. 
 * 
 * If the implementing application is intended to recieve messages actively (due to configuration) and provides the necessary
 * web service container to run the {@link DomibusConnectorClientDeliveryWSLink} web service, additionally the interface 
 * {@link DomibusConnectorDeliveryClient} needs to be implemented. In that case, the method {@link #requestNewMessagesFromConnector()}
 * does not have to be implemented (method should be left empty).
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
	 * @throws DomibusConnectorClientException
	 */
	public void submitNewMessageToConnector (
			DomibusConnectorMessageType message) throws DomibusConnectorClientException;

	
	/**
	 * Requests all new messages that are provided by the domibusConnector's backend. Before returning the received messages, all messages
	 * are mapped calling the configured implementation of {@link DomibusConnectorClientContentMapper}. If no content mapper is configured, the default
	 * content mapper is called.
	 * 
	 * @return The {@link DomibusConnectorMessagesType} containing the new messages.
	 * @throws DomibusConnectorClientException
	 */
	public DomibusConnectorMessagesType requestNewMessagesFromConnector () throws DomibusConnectorClientException;
	
	/**
	 * Triggers the generation and submission of a message confirmation at the domibusConnector.
	 * 
	 * @param originalMessage - The message that should be confirmed.
	 * @param confirmationType - The type of confirmation that should be generated and submitted.
	 * @throws DomibusConnectorClientException
	 */
	public void triggerConfirmationForMessage(
			DomibusConnectorMessageType originalMessage, 
			DomibusConnectorConfirmationType confirmationType) throws DomibusConnectorClientException;
	
	
	
}

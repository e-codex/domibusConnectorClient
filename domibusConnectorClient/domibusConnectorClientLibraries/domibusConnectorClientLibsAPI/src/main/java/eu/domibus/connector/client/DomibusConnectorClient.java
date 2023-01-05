package eu.domibus.connector.client;

import java.util.Map;

import eu.domibus.connector.client.exception.DCCConnectorAcknowledgementException;
import eu.domibus.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
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
	 * Before submitted, the {@link eu.domibus.connector.client.DomibusConnectorClientMessageHandler} is called.
	 * 
	 * @param message - The {@link DomibusConnectorMessageType} object containing the message information. 
	 * 					May be built using the {@link DomibusConnectorClientMessageBuilder}.
	 * @throws DomibusConnectorClientException if the submission fails. 
	 * Wrapped exception type {@link DCCConnectorAcknowledgementException} if the message was rejected by the connector or result from connector not available.
	 * Wrapped exception type {@link DomibusConnectorBackendWebServiceClientException} if the message could not be submitted to the connector.
	 */
	public void submitNewMessageToConnector (
			DomibusConnectorMessageType message) throws DomibusConnectorClientException;

	
	/**
	 * Requests all new messages that are provided by the domibusConnector's backend. 
	 * Before delivered, the {@link eu.domibus.connector.client.DomibusConnectorClientMessageHandler} is called for each message received.
	 * 
	 * @return The {@link DomibusConnectorMessagesType} containing the new messages.
	 * @throws DomibusConnectorClientException if the fetching of new messages from the connector fails
	 * 
	 * @deprecated Since this method is a single hit call that requests all messages pending at the domibusConnector. Also, messages received
	 * via this method do not need acknowledgement and are therefore deleted permanently from the domibusConnector.
	 * Use {@link DomibusConnectorClient#requestNewMessagesFromConnector(Integer, boolean)} instead.
	 */
	@Deprecated
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


	/**
	 * Replaces  {@link DomibusConnectorClient#requestNewMessagesFromConnector()}
	 * 
	 * Allows to limit the number of messages that are to be received at one call and also to either self-acknowledge messages to the domibusConnector,
	 * or do that in a seperate, asynchronous call via {@link DomibusConnectorClient#acknowledgeMessage(String, boolean, String)}.
	 * 
	 * @param maxFetchCount The number of messages to be received in one call. If there are more than that messages pending at the
	 * domibusConnector, the other messages exceeding this limit will be received with the next call. If null, all messages will be requested at once.
	 * @param acknowledgeAutomatically If true, the domibusConnector immediately acknowledges messages received to the domibusConnector.
	 * The messages acknowledged are then finished by the domibusConnector and cannot be sent again to the backend client.
	 * If false, messages have to be acknowledged manually by calling the {@link DomibusConnectorClient#acknowledgeMessage(String, boolean, String)}
	 * method. 
	 * @return a Map where the key is the messageTransportId required to acknowledge the message in a second step. The value is the message itself.
	 * @throws DomibusConnectorClientException any error
	 */
	Map<String, DomibusConnectorMessageType> requestNewMessagesFromConnector(Integer maxFetchCount,
			boolean acknowledgeAutomatically) throws DomibusConnectorClientException;


	/**
	 * Method to acknowledge received messages via the {@link DomibusConnectorClient#requestNewMessagesFromConnector(Integer, boolean)} method
	 * if not acknowledged automatically.
	 * 
	 * @param messageTransportId The key of the Map.Entry to the message returned from  {@link DomibusConnectorClient#requestNewMessagesFromConnector(Integer, boolean)}
	 * @param result If true, the message will be treated as finished successfully by the domibusConnector. If false, the domibusConnector treats the message as failed.
	 * @param backendMessageId The message ID which the business message has been given by the backend.
	 */
	void acknowledgeMessage(String messageTransportId, boolean result, String backendMessageId);
	
	
	
}

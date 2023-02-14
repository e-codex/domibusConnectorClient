package eu.domibus.connector.client.link;

import java.util.List;

import eu.domibus.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This Interface class is an abstraction layer between the domibusConnectorClientWSLink module and the domibusConnectorClientLibrary.
 * It delegates methods through the WSLink to the domibusConnectorAPI backend.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientLink {

	
	/**
	 * Delegate method to request new messages via domibusConnectorAPI from the backend of the domibusConnector to the domibusConnectorClientLibrary.
	 * 
	 * @return the {@link DomibusConnectorMessagesType} holding all messages which are pending for this client.
	 * @throws DomibusConnectorBackendWebServiceClientException DomibusConnectorBackendWebServiceClientException
	 */
	public DomibusConnectorMessagesType requestMessagesFromConnector() throws DomibusConnectorBackendWebServiceClientException;
	
	/**
	 * Delegate method to submit a message from the domibusConnectorClientLibrary to the backend of the domibusConnector.
	 * 
	 * @param message message
	 * @return the result of the submission
	 * @throws DomibusConnectorBackendWebServiceClientException  DomibusConnectorBackendWebServiceClientException
	 */
	public DomibsConnectorAcknowledgementType submitMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorBackendWebServiceClientException;

	/**
	 * Returns transport IDs generated by the domibusConnector for messages on the domibusConnector which are pending for this client.
	 * 
	 * @return a list of transport IDs required to get and acknowledge messages.
	 * @throws DomibusConnectorBackendWebServiceClientException DomibusConnectorBackendWebServiceClientException
	 */
	List<String> listPendingMessages() throws DomibusConnectorBackendWebServiceClientException;

	/**
	 * Requests the message for the given message transport ID. Transport ID is received when calling listPendingMessages.
	 * 
	 * @param messageTransportId messageTransportId
	 * @return the message
	 * @throws DomibusConnectorBackendWebServiceClientException DomibusConnectorBackendWebServiceClientException
	 */
	DomibusConnectorMessageType getMessageById(String messageTransportId)
			throws DomibusConnectorBackendWebServiceClientException;

	/**
	 * If a received message is completely processed by the client the message can be acknowledged to the domibusConnector.
	 * In case of positive result, the domibusConnector finishes the message. In case of negative result, the domibusConnector
	 * moves the message to its internal DLQ.
	 * 
	 * @param result of type {@link DomibusConnectorMessageResponseType} MUST contain message transport ID as responseForMessageId!
	 * @throws DomibusConnectorBackendWebServiceClientException 
	 */
	 void acknowledgeMessage(DomibusConnectorMessageResponseType result) throws DomibusConnectorBackendWebServiceClientException;
	
}

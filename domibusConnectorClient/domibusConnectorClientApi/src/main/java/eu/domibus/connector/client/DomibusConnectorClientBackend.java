package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This interface must be implemented if the domibusConnectorClientScheduler is used and/or the client is set up in push/pull mode!
 * 
 * @author Bernhard Rieder
 *
 */
public interface DomibusConnectorClientBackend {

	/**
	 * This method asks the backend of the client if new messages are to submit to the connector.
	 * Must be implemented if domibusConnectorClientScheduler is used, or if the client implementation is not self aware to 
	 * recognize new messages at its backend.
	 *  
	 * @return messages object holding a Collection of messages.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public DomibusConnectorMessagesType checkClientForNewMessagesToSubmit() throws DomibusConnectorClientBackendException;
	
	/**
	 * This method triggers the client's backend to store/put/forward messages received.
	 * Must be implemented if domibusConnectorClientScheduler is used, or if the client is set up in push/pull mode.
	 * 
	 * @param message - The message object received from the connector.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public void deliverNewMessageToClientBackend(DomibusConnectorMessageType message) throws DomibusConnectorClientBackendException;
	
	/**
	 * This method triggers the client's backend to store/put/forward confirmation received.
	 * Must be implemented if domibusConnectorClientScheduler is used, or if the client is set up in push/pull mode.
	 * 
	 * @param message - The message object containing the confirmation received from the connector.
	 * @throws DomibusConnectorClientBackendException 
	 */
	public void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message) throws DomibusConnectorClientBackendException;
	
	void triggerConfirmationForMessage(DomibusConnectorMessageType originalMessage,
			DomibusConnectorConfirmationType confirmationType, String confirmationAction)
			throws DomibusConnectorClientBackendException;

	void submitStoredClientBackendMessage(String storageLocation) throws DomibusConnectorClientBackendException, DomibusConnectorClientStorageException, IllegalArgumentException;
	
}

package eu.domibus.connector.client.storage;

import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

public interface DomibusConnectorClientStorage {

	DomibusConnectorMessagesType checkStorageForNewMessages();

	DomibusConnectorMessagesType getAllStoredMessages();
	
	String storeMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException;
	
	void storeConfirmationToMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException;

}

package eu.domibus.connector.client.storage;

import java.util.List;
import java.util.Map;

import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

public interface DomibusConnectorClientStorage {

	Map<String, DomibusConnectorMessageType> checkStorageForNewMessages();

	DomibusConnectorMessagesType getAllStoredMessages();
	
	String storeMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException;
	
	void storeConfirmationToMessage(DomibusConnectorMessageType message, String storageLocation) throws DomibusConnectorClientStorageException;

	DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation);
	
	byte[] loadContentFromStorageLocation(String storageLocation, String name);
	
	Map<String, DomibusConnectorClientMessageFileType> listContentAtStorageLocation(String storageLocation);
	
}

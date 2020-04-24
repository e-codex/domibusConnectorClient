package eu.domibus.connector.client.storage;

import java.util.Map;

import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientStorage {

	Map<String, DomibusConnectorMessageType> checkStorageForNewMessages();

	Map<String, DomibusConnectorMessageType> getAllStoredMessages();
	
	String storeMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException;
	
	void storeConfirmationToMessage(DomibusConnectorMessageType message, String storageLocation) throws DomibusConnectorClientStorageException;

	DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation);
	
	byte[] loadContentFromStorageLocation(String storageLocation, String name);
	
	Map<String, DomibusConnectorClientMessageFileType> listContentAtStorageLocation(String storageLocation);
	
	void deleteFromStorage(String storageLocation) throws DomibusConnectorClientStorageException;
	
	boolean storeFileIntoStorage(String storageLocation, String fileName, DomibusConnectorClientMessageFileType fileType, byte[] content);
	
	DomibusConnectorMessageType getStoredMessage(String storageLocation) throws DomibusConnectorClientStorageException;
	
	String updateStoredMessageToSent(String storageLocation) throws DomibusConnectorClientStorageException;

	boolean deleteFileFromStorage(String storageLocation, String fileName,
			DomibusConnectorClientMessageFileType fileType);
}

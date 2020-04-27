package eu.domibus.connector.client.storage;

import java.util.Map;

import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientStorage {

	Map<String, DomibusConnectorMessageType> checkStorageForNewMessages();

	Map<String, DomibusConnectorMessageType> getAllStoredMessages();
	
	String storeMessage(DomibusConnectorMessageType message) 
			throws DomibusConnectorClientStorageException;
	
	void storeConfirmationToMessage(DomibusConnectorMessageType message, String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation);
	
	byte[] loadFileContentFromStorageLocation(String storageLocation, String name) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
	
	Map<String, DomibusConnectorClientMessageFileType> listContentAtStorageLocation(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
	
	DomibusConnectorMessageType getStoredMessage(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
	
	String updateStoredMessageToSent(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	void storeFileIntoStorage(String storageLocation, String fileName, DomibusConnectorClientMessageFileType fileType, byte[] content) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	void deleteMessageFromStorage(String storageLocation) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;

	void deleteFileFromStorage(String storageLocation, String fileName,	DomibusConnectorClientMessageFileType fileType) 
			throws DomibusConnectorClientStorageException, IllegalArgumentException;
}

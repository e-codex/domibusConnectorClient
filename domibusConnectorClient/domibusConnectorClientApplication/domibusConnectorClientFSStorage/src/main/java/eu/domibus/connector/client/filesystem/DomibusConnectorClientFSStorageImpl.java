package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

public class DomibusConnectorClientFSStorageImpl implements DomibusConnectorClientFSStorage {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientFSStorageImpl.class);
	
	@Autowired
	DomibusConnectorClientFileSystemReader fileSystemReader;
	
	@Autowired
	DomibusConnectorClientFileSystemWriter fileSystemWriter;
	
	File messagesDir;
	
	@Override
	public String storeMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException {
		
		LOGGER.debug("#storeMessage: storing message [{}]...", message);
		
		String messageLocation = null;
		try {
			messageLocation = fileSystemWriter.writeMessageToFileSystem(message, messagesDir);
		} catch (DomibusConnectorClientFileSystemException e) {
			LOGGER.error("Exception storing message [{}] from connector... ", message, e);
			throw new DomibusConnectorClientStorageException(e);
		}
		LOGGER.debug("#storeMessage: message [{}] successfully stored.", message);
		
		return messageLocation;
	}

	@Override
	public void storeConfirmationToMessage(DomibusConnectorMessageType message, String storageLocation) throws DomibusConnectorClientStorageException {
		DomibusConnectorMessageConfirmationType confirmation = message.getMessageConfirmations().get(0);
		String type = confirmation.getConfirmationType().name();
		
		LOGGER.debug("#storeMessage: storing confirmation of type [{}] to message [{}]...", type, message.getMessageDetails().getRefToMessageId());
		
		try {
			fileSystemWriter.writeConfirmationToFileSystem(message, storageLocation);
		} catch (DomibusConnectorClientFileSystemException e) {
			LOGGER.error("Exception storing confirmation [{}] to message from connector... ",type, e);
			throw new DomibusConnectorClientStorageException(e);
		}
		
		LOGGER.debug("#storeMessage: confirmation [{}] to message [{}] successfully stored.", type, message.getMessageDetails().getRefToMessageId());
	}


	@Override
	public Map<String, DomibusConnectorMessageType> checkStorageForNewMessages()
			{
		LOGGER.debug("#checkStorageForNewMessages: Start searching dir {} for unsent messages.", messagesDir.getAbsolutePath());
		List<File> messagesUnsent = fileSystemReader.readUnsentMessages(messagesDir);

		if (!messagesUnsent.isEmpty()) {
			Map<String, DomibusConnectorMessageType> newMessages = new HashMap<String,DomibusConnectorMessageType>();
			LOGGER.info("#checkStorageForNewMessages: Found {} new outgoing messages to process!", messagesUnsent.size());
			for (File messageFolder : messagesUnsent) {
				LOGGER.debug("#checkStorageForNewMessages: Processing new message folder {}", messageFolder.getAbsolutePath());
				if (messageFolder.listFiles().length > 0) {
					
					DomibusConnectorMessageType message = null;
					try {
						message = fileSystemReader.readMessageFromFolder(messageFolder);
					} catch (DomibusConnectorClientFileSystemException e) {
						LOGGER.error(""+e);
						continue;
					}
					
					if(message!=null) {
						newMessages.put(messageFolder.getAbsolutePath(), message);
					}
				}
			}
			return newMessages;
		} else {
			LOGGER.debug("#checkStorageForNewMessages: No new messages found!");
			return null;
		}
	}
	
	@Override
	public DomibusConnectorClientStorageStatus checkStorageStatus(String storageLocation) {
		return fileSystemReader.checkStorageStatus(storageLocation);
	}
	
	@Override
	public DomibusConnectorMessagesType getAllStoredMessages() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public byte[] loadContentFromStorageLocation(String storageLocation, String fileName) {
		File messageFolder = new File(storageLocation);
		
		return fileSystemReader.loadContentFromMessageFolder(messageFolder, fileName);
	}

	@Override
	public Map<String, DomibusConnectorClientMessageFileType> listContentAtStorageLocation(String storageLocation) {
		File messageFolder = new File(storageLocation);
		return fileSystemReader.getFileListFromMessageFolder(messageFolder);
	}

	public File getMessagesDir() {
		return messagesDir;
	}

	@Override
	public void setMessagesDir(File messagesDir) {
		this.messagesDir = messagesDir;
	}

	@Override
	public void deleteFromStorage(String storageLocation) throws DomibusConnectorClientStorageException{
		LOGGER.debug("#deleteFromStorage: called with storageLocation {}", storageLocation);
		try {
			this.fileSystemWriter.deleteFromStorage(storageLocation);
		} catch (DomibusConnectorClientFileSystemException e) {
			throw new DomibusConnectorClientStorageException(e);
		}
		
		LOGGER.debug("#deleteFromStorage: successfully deleted storageLocation {}", storageLocation);
	}



}

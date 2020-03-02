package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

public class DomibusConnectorClientFSStorageImpl implements DomibusConnectorClientFSStorage {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientFSStorageImpl.class);
	
	@Autowired
	DomibusConnectorClientFileSystemReader fileSystemReader;
	
	@Autowired
	DomibusConnectorClientFileSystemWriter fileSystemWriter;
	
	File incomingMessagesDir;

	File outgoingMessagesDir;
	
	@Override
	public String storeMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException {
		
		LOGGER.debug("#storeMessage: storing message [{}]...", message);
		
		String messageLocation = null;
		try {
			messageLocation = fileSystemWriter.writeMessageToFileSystem(message, incomingMessagesDir);
		} catch (DomibusConnectorClientFileSystemException e) {
			LOGGER.error("Exception storing message [{}] from connector... ", message, e);
			throw new DomibusConnectorClientStorageException(e);
		}
		LOGGER.debug("#storeMessage: message [{}] successfully stored.", message);
		
		return messageLocation;
	}

	@Override
	public void storeConfirmationToMessage(DomibusConnectorMessageType message) throws DomibusConnectorClientStorageException {
		DomibusConnectorMessageConfirmationType confirmation = message.getMessageConfirmations().get(0);
		String type = confirmation.getConfirmationType().name();
		
		LOGGER.debug("#storeMessage: storing confirmation of type [{}] to message [{}]...", type, message.getMessageDetails().getBackendMessageId());
		
		try {
			fileSystemWriter.writeConfirmationToFileSystem(message, incomingMessagesDir, outgoingMessagesDir);
		} catch (DomibusConnectorClientFileSystemException e) {
			LOGGER.error("Exception storing confirmation [{}] to message from connector... ",type, e);
			throw new DomibusConnectorClientStorageException(e);
		}
		
		LOGGER.debug("#storeMessage: confirmation [{}] to message [{}] successfully stored.", type, message.getMessageDetails().getBackendMessageId());
	}


	@Override
	public DomibusConnectorMessagesType checkStorageForNewMessages()
			{
		LOGGER.debug("#checkStorageForNewMessages: Start searching dir {} for unsent messages.", outgoingMessagesDir.getAbsolutePath());
		List<File> messagesUnsent = fileSystemReader.readUnsentMessages(outgoingMessagesDir);

		if (!messagesUnsent.isEmpty()) {
			DomibusConnectorMessagesType messages = new DomibusConnectorMessagesType();
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
						messages.getMessages().add(message);
					}
				}
			}
			return messages;
		} else {
			LOGGER.debug("#checkStorageForNewMessages: No new messages found!");
			return null;
		}
	}
	
	
	@Override
	public DomibusConnectorMessagesType getAllStoredMessages() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	

	public File getIncomingMessagesDir() {
		return incomingMessagesDir;
	}

	public void setIncomingMessagesDir(File incomingMessagesDir) {
		this.incomingMessagesDir = incomingMessagesDir;
	}

	public File getOutgoingMessagesDir() {
		return outgoingMessagesDir;
	}

	public void setOutgoingMessagesDir(File outgoingMessagesDir) {
		this.outgoingMessagesDir = outgoingMessagesDir;
	}



}

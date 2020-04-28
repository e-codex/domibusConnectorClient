package eu.domibus.connector.client.controller.rest.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.client.rest.DomibusConnectorClientRestAPI;
import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.exception.ParameterException;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@RestController
@RequestMapping("/restservice")
public class DomibusConnectorClientRestAPIImpl implements DomibusConnectorClientRestAPI {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientRestAPIImpl.class);

	@Autowired
	private IDomibusConnectorClientPersistenceService persistenceService;

	@Autowired
	private DomibusConnectorClientStorage storage;

	@Autowired
	private DomibusConnectorClientMessageBuilder messageBuilder;

	@Autowired
	private DomibusConnectorClientBackend connectorClientBackend;

	@Override
	public DomibusConnectorClientMessageList getAllMessages() {
		Iterable<PDomibusConnectorClientMessage> findAll = persistenceService.getMessageDao().findAll();

		DomibusConnectorClientMessageList messages = mapMessagesFromModel(findAll);

		return messages;
	}

	@Override
	public DomibusConnectorClientMessage getMessageById(Long id) throws MessageNotFoundException {
		Optional<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findById(id);

		if(msg.isPresent()) {
			DomibusConnectorClientMessage message = mapMessageFromModel(msg.get());
			return message;
		}else {
			throw new MessageNotFoundException("No message with id found in database: "+id);
		}
	}

	@Override
	public DomibusConnectorClientMessage getMessageByBackendMessageId(String backendMessageId) throws MessageNotFoundException {
		Optional<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findOneByBackendMessageId(backendMessageId);

		if(msg.isPresent()) {
			DomibusConnectorClientMessage message = mapMessageFromModel(msg.get());
			return message;
		}else {
			throw new MessageNotFoundException("No message with backendMessageId found in database: "+backendMessageId);
		}

	}

	@Override
	public DomibusConnectorClientMessage getMessageByEbmsMessageId(String ebmsMessageId) throws MessageNotFoundException {
		Optional<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findOneByEbmsMessageId(ebmsMessageId);

		if(msg.isPresent()) {
			DomibusConnectorClientMessage message = mapMessageFromModel(msg.get());
			return message;
		}else {
			throw new MessageNotFoundException("No message with ebmsMessageId found in database: "+ebmsMessageId);
		}

	}

	@Override
	public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId) throws MessageNotFoundException {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByConversationId(conversationId);
		
		if(CollectionUtils.isEmpty(msg)) {
			throw new MessageNotFoundException("No messages with conversationId found in database: "+conversationId);
		}

		DomibusConnectorClientMessageList messages = mapMessagesFromModel(msg);

		return messages;
	}

	@Override
	public DomibusConnectorClientMessageList getMessagesByPeriod(Date from, Date to) throws MessageNotFoundException {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByPeriod(from, to);

		if(CollectionUtils.isEmpty(msg)) {
			throw new MessageNotFoundException("No messages found in database using given period ");
		}
		
		DomibusConnectorClientMessageList messages = mapMessagesFromModel(msg);

		return messages;
	}

	@Override
	public byte[] loadFileContentFromStorage(String storageLocation, String fileName) throws ParameterException {

		byte[] content = null;
		try {
			content = storage.loadFileContentFromStorageLocation(storageLocation, fileName);
		} catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
			throw new ParameterException("Parameter failure: "+e.getMessage(), e);
		}
		return content;
	}


	@Override
	public DomibusConnectorClientMessage saveMessage(DomibusConnectorClientMessage message) throws ParameterException {
		if(StringUtils.isEmpty(message.getBackendMessageId())) {
			String backendMessageId = generateBackendMessageId();
			message.setBackendMessageId(backendMessageId);
		}

		DomibusConnectorMessageType msg = mapMessageToTransition(message);

		PDomibusConnectorClientMessage pMessage = null;
		if(message.getId()!=null) {
			Optional<PDomibusConnectorClientMessage> findById = persistenceService.getMessageDao().findById(message.getId());
			if(findById==null || findById.get()==null) {
				pMessage = persistenceService.persistNewMessage(msg, PDomibusConnectorClientMessageStatus.PREPARED);
			}else {
				pMessage = findById.get();
			}
		}else {
			pMessage = persistenceService.persistNewMessage(msg, PDomibusConnectorClientMessageStatus.PREPARED);
		}
		message.setCreated(pMessage.getCreated());
		message.setId(pMessage.getId());
		pMessage = mapMessageToModel(message, pMessage);

		String storageLocation = null;
		try {
			storageLocation = storage.storeMessage(msg);
		} catch (DomibusConnectorClientStorageException e) {
			throw new ResponseStatusException(
					HttpStatus.SEE_OTHER, "Storage failure: "+e.getMessage(), e);
		}
		pMessage.setStorageInfo(storageLocation);
		pMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);

		persistenceService.mergeClientMessage(pMessage);

		message.setStorageInfo(pMessage.getStorageInfo());
		message.setStorageStatus(pMessage.getStorageStatus().name());

		return message;
	}

	@Override
	public Boolean deleteMessageById(Long id) throws ParameterException {
		Optional<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findById(id);


		if(msg.get() !=null) {
			String storageInfo = msg.get().getStorageInfo();
			if(storageInfo!=null && !storageInfo.isEmpty()) {
				try {
					storage.deleteMessageFromStorage(msg.get().getStorageInfo());
				} catch (DomibusConnectorClientStorageException e) {
					throw new ResponseStatusException(
							HttpStatus.SEE_OTHER, "Storage failure: "+e.getMessage(), e);
				} catch (IllegalArgumentException e) {
					throw new ParameterException("Parameter failure: "+e.getMessage(), e);
				}
			}

			persistenceService.deleteMessage(msg.get());
		}
		return Boolean.TRUE;

	}

	@Override
	public Boolean submitStoredClientMessage(DomibusConnectorClientMessage message) throws ParameterException {
		try {
			connectorClientBackend.submitStoredClientBackendMessage(message.getStorageInfo());
		} catch (DomibusConnectorClientBackendException e) {
			throw new ResponseStatusException(
					HttpStatus.SEE_OTHER, "Client backend failure", e);
		} catch (IllegalArgumentException e) {
			throw new ParameterException("Parameter failure: "+e.getMessage(), e);
		} catch (DomibusConnectorClientStorageException e) {
			throw new ResponseStatusException(
					HttpStatus.SEE_OTHER, "Storage failure: "+e.getMessage(), e);
		}
		return Boolean.TRUE;
	}

	@Override
	public Boolean uploadMessageFile(DomibusConnectorClientMessageFile messageFile) throws ParameterException {

		try {
			storage.storeFileIntoStorage(messageFile.getStorageLocation(), messageFile.getFileName(), messageFile.getFileType(), messageFile.getFileContent());
		} catch (DomibusConnectorClientStorageException e) {
			throw new ResponseStatusException(
					HttpStatus.SEE_OTHER, "Storage failure: "+e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ParameterException("Parameter failure: "+e.getMessage(), e);
		}
		return Boolean.TRUE;
	}

	@Override
	public Boolean deleteMessageFile(DomibusConnectorClientMessageFile messageFile) throws ParameterException {
		try {
			storage.deleteFileFromStorage(messageFile.getStorageLocation(), messageFile.getFileName(), messageFile.getFileType());
		} catch (DomibusConnectorClientStorageException e) {
			throw new ResponseStatusException(
					HttpStatus.SEE_OTHER, "Storage failure: "+e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ParameterException("Parameter failure: "+e.getMessage(), e);
		}
		return Boolean.TRUE;
	}

	private DomibusConnectorClientMessageList mapMessagesFromModel(Iterable<PDomibusConnectorClientMessage> findAll)  {
		DomibusConnectorClientMessageList messages = new DomibusConnectorClientMessageList();

		findAll.forEach(message -> {
			DomibusConnectorClientMessage msg = mapMessageFromModel(message);
			messages.getMessages().add(msg);
		});

		return messages;
	}

	private DomibusConnectorClientMessage mapMessageFromModel(PDomibusConnectorClientMessage message) {
		DomibusConnectorClientMessage msg = new DomibusConnectorClientMessage();
		Set<PDomibusConnectorClientConfirmation> confirmations = message.getConfirmations();
		confirmations.forEach(confirmation -> {
			DomibusConnectorClientConfirmation evidence = new DomibusConnectorClientConfirmation();
			BeanUtils.copyProperties(confirmation, evidence);
			//			evidence.setStorageStatus(confirmation.getStorageStatus().name());
			msg.getEvidences().add(evidence);
		});
		BeanUtils.copyProperties(message, msg);
		msg.setStorageStatus(message.getStorageStatus().name());
		msg.setMessageStatus(message.getMessageStatus().name());

		if(filesReadable(message)) {
			Map<String, DomibusConnectorClientMessageFileType> files = null;
			try {
				files = storage.listContentAtStorageLocation(message.getStorageInfo());
			} catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
				DomibusConnectorClientStorageStatus checkStorageStatus = storage.checkStorageStatus(message.getStorageInfo());
				message.setStorageStatus(checkStorageStatus);
				persistenceService.mergeClientMessage(message);
				return msg;
				
			}
			files.entrySet().forEach(file -> {
				DomibusConnectorClientMessageFile file2 = new DomibusConnectorClientMessageFile(file.getKey(), file.getValue());
				file2.setStorageLocation(message.getStorageInfo());
				msg.getFiles().add(file2);
			});
		}

		return msg;
	}

	private PDomibusConnectorClientMessage mapMessageToModel(DomibusConnectorClientMessage msg, PDomibusConnectorClientMessage pMessage) {
		if(pMessage == null) {
			pMessage = new PDomibusConnectorClientMessage();
		}
		BeanUtils.copyProperties(msg, pMessage);

		return pMessage;
	}

	private DomibusConnectorMessageType mapMessageToTransition(DomibusConnectorClientMessage clientMessage){
		DomibusConnectorMessageType message = messageBuilder.createNewMessage(
				clientMessage.getBackendMessageId(), 
				clientMessage.getEbmsMessageId(), 
				clientMessage.getConversationId(), 
				clientMessage.getService(), null, 
				clientMessage.getAction(), 
				clientMessage.getFromPartyId(), clientMessage.getFromPartyType(), clientMessage.getFromPartyRole(), 
				clientMessage.getToPartyId(), clientMessage.getToPartyType(), clientMessage.getToPartyRole(), 
				clientMessage.getFinalRecipient(), clientMessage.getOriginalSender());

		if(clientMessage.getFiles()!=null && clientMessage.getFiles().getFiles()!=null && !clientMessage.getFiles().getFiles().isEmpty()) {
			clientMessage.getFiles().getFiles().forEach(file -> {
				byte[] fileContent = file.getFileContent();
				if(fileContent==null) {
					try {
						fileContent = storage.loadFileContentFromStorageLocation(clientMessage.getStorageInfo(), file.getFileName());
					} catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
						LOGGER.error("Exception called storage.loadFileContentFromStorageLocation with storageLocation {} and fileName {}", clientMessage.getStorageInfo(), file.getFileName(), e);
						
					}
				}
				if(fileContent != null && fileContent.length > 0) {
					if(file.getFileType().equals(DomibusConnectorClientMessageFileType.BUSINESS_CONTENT)) {
						messageBuilder.addBusinessContentXMLAsBinary(message, fileContent);
					}else if(file.getFileType().equals(DomibusConnectorClientMessageFileType.BUSINESS_DOCUMENT)) {
						messageBuilder.addBusinessDocumentAsBinary(message, fileContent, file.getFileName());
					}else {
						messageBuilder.addBusinessAttachmentAsBinaryToMessage(message, file.getFileName(), fileContent, file.getFileName(), null, file.getFileName());
					}

				}
			});
		}
		return message;
	}

	private boolean filesReadable(PDomibusConnectorClientMessage message) {
		return message!=null && message.getStorageInfo()!=null && !message.getStorageInfo().isEmpty() && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED);
	}

	private String generateBackendMessageId() {
		return UUID.randomUUID().toString() + "@connector-client.eu";
	}




}

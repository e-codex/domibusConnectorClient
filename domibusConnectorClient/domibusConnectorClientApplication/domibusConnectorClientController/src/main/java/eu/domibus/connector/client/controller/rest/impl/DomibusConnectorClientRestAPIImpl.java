package eu.domibus.connector.client.controller.rest.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.client.rest.DomibusConnectorClientRestAPI;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFileList;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@RestController
@RequestMapping("/restservice")
public class DomibusConnectorClientRestAPIImpl implements DomibusConnectorClientRestAPI {

	@Autowired
	private IDomibusConnectorClientPersistenceService persistenceService;

	@Autowired
	private DomibusConnectorClientStorage storage;
	
	@Autowired
	private DomibusConnectorClientMessageBuilder messageBuilder;

	public DomibusConnectorClientRestAPIImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getAllMessages()
	 */
	@Override
	public DomibusConnectorClientMessageList getAllMessages(){
		Iterable<PDomibusConnectorClientMessage> findAll = persistenceService.getMessageDao().findAll();

		DomibusConnectorClientMessageList messages = mapMessagesFromModel(findAll);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessageById(java.lang.Long)
	 */
	@Override
	public DomibusConnectorClientMessage getMessageById(Long id) {
		Optional<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findById(id);

		if(msg.get() !=null) {
			DomibusConnectorClientMessage message = mapMessageFromModel(msg.get());
			return message;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessagesByBackendMessageId(java.lang.String)
	 */
	@Override
	public DomibusConnectorClientMessageList getMessagesByBackendMessageId(String backendMessageId) {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByBackendMessageId(backendMessageId);

		DomibusConnectorClientMessageList messages = mapMessagesFromModel(msg);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessagesByEbmsMessageId(java.lang.String)
	 */
	@Override
	public DomibusConnectorClientMessageList getMessagesByEbmsMessageId(String ebmsMessageId) {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByEbmsMessageId(ebmsMessageId);

		DomibusConnectorClientMessageList messages = mapMessagesFromModel(msg);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessagesByConversationId(java.lang.String)
	 */
	@Override
	public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId) {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByConversationId(conversationId);

		DomibusConnectorClientMessageList messages = mapMessagesFromModel(msg);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessagesByPeriod(java.util.Date, java.util.Date)
	 */
	@Override
	public DomibusConnectorClientMessageList getMessagesByPeriod(Date from, Date to) {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByPeriod(from, to);

		DomibusConnectorClientMessageList messages = mapMessagesFromModel(msg);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#loadContentFromStorage(java.lang.String, java.lang.String)
	 */
	@Override
	public byte[] loadContentFromStorage(String storageLocation, String contentName) {

		byte[] content = storage.loadContentFromStorageLocation(storageLocation, contentName);
		return content;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#listContentAtStorage(java.lang.String)
	 */
	@Override
	public Map<String, DomibusConnectorClientMessageFileType> listContentAtStorage(String storageLocation) {

		return storage.listContentAtStorageLocation(storageLocation);
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#createNewMessage(eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage)
	 */
	@Override
	public DomibusConnectorClientMessage createNewMessage(DomibusConnectorClientMessage newMessage) {
		DomibusConnectorMessageType message = mapMessageToTransition(newMessage);
		
//		message = mapMessageFilesToTransition(files.getFiles(), message);
		
		try {
			String storageLocation = storage.storeMessage(message);
			PDomibusConnectorClientMessage pMessage = persistenceService.persistNewMessage(message, PDomibusConnectorClientMessageStatus.PREPARED);
			newMessage.setId(pMessage.getId());
			pMessage.setStorageInfo(storageLocation);
			pMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
			persistenceService.mergeClientMessage(pMessage);
			newMessage.setStorageInfo(pMessage.getStorageInfo());
			newMessage.setStorageStatus(pMessage.getStorageStatus().name());
		} catch (DomibusConnectorClientStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newMessage;
	}

//	private DomibusConnectorMessageType mapMessageFilesToTransition(List<DomibusConnectorClientMessageFile> files,
//			final DomibusConnectorMessageType message) {
//		if(files!=null && !files.isEmpty()) {
//			files.forEach(file -> {
//				byte[] fileContent = storage.loadContentFromStorageLocation(file.getStorageLocation(), file.getFileName());
//				if(fileContent.length > 0) {
//					if(file.getFileType().equals(DomibusConnectorClientMessageFileType.BUSINESS_CONTENT)) {
//						messageBuilder.addBusinessContentXMLAsBinary(message, fileContent);
//					}else if(file.getFileType().equals(DomibusConnectorClientMessageFileType.BUSINESS_DOCUMENT)) {
//						messageBuilder.addBusinessDocumentAsBinary(message, fileContent, file.getFileName());
//					}else {
//						messageBuilder.addBusinessAttachmentAsBinaryToMessage(message, file.getFileName(), fileContent, file.getFileName(), null, file.getFileName());
//					}
//					
//				}
//			});
//		}
//		return message;
//	}

	private DomibusConnectorClientMessageList mapMessagesFromModel(Iterable<PDomibusConnectorClientMessage> findAll) {
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
			Map<String, DomibusConnectorClientMessageFileType> files = storage.listContentAtStorageLocation(message.getStorageInfo());
			files.entrySet().forEach(file -> {
				msg.getFiles().add(new DomibusConnectorClientMessageFile(file.getKey(), file.getValue(), message.getStorageInfo()));
			});
		}
		
		return msg;
	}
	
	private DomibusConnectorMessageType mapMessageToTransition(DomibusConnectorClientMessage clientMessage) {
		DomibusConnectorMessageType message = messageBuilder.createNewMessage(
				clientMessage.getBackendMessageId(), 
				clientMessage.getEbmsMessageId(), 
				clientMessage.getConversationId(), 
				clientMessage.getService(), null, 
				clientMessage.getAction(), 
				clientMessage.getFromPartyId(), clientMessage.getFromPartyType(), clientMessage.getFromPartyRole(), 
				clientMessage.getToPartyId(), clientMessage.getToPartyType(), clientMessage.getToPartyRole(), 
				clientMessage.getFinalRecipient(), clientMessage.getOriginalSender());
		
		if(clientMessage.getFiles()!=null && !clientMessage.getFiles().getFiles().isEmpty()) {
			clientMessage.getFiles().getFiles().forEach(file -> {
				byte[] fileContent = storage.loadContentFromStorageLocation(file.getStorageLocation(), file.getFileName());
				if(fileContent.length > 0) {
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
}

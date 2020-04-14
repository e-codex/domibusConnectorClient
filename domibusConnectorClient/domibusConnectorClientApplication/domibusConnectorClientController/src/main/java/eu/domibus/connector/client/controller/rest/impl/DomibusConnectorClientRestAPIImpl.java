package eu.domibus.connector.client.controller.rest.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.rest.DomibusConnectorClientRestAPI;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;

@RestController
@RequestMapping("/restservice")
public class DomibusConnectorClientRestAPIImpl implements DomibusConnectorClientRestAPI {

	@Autowired
	private IDomibusConnectorClientPersistenceService persistenceService;

	@Autowired
	private DomibusConnectorClientStorage storage;

	public DomibusConnectorClientRestAPIImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getAllMessages()
	 */
	@Override
	public DomibusConnectorClientMessageList getAllMessages(){
		Iterable<PDomibusConnectorClientMessage> findAll = persistenceService.getMessageDao().findAll();

		DomibusConnectorClientMessageList messages = mapMessages(findAll);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessageById(java.lang.Long)
	 */
	@Override
	public DomibusConnectorClientMessage getMessageById(Long id) {
		Optional<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findById(id);

		if(msg.get() !=null) {
			DomibusConnectorClientMessage message = mapMessage(msg.get());
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

		DomibusConnectorClientMessageList messages = mapMessages(msg);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessagesByEbmsMessageId(java.lang.String)
	 */
	@Override
	public DomibusConnectorClientMessageList getMessagesByEbmsMessageId(String ebmsMessageId) {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByEbmsMessageId(ebmsMessageId);

		DomibusConnectorClientMessageList messages = mapMessages(msg);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessagesByConversationId(java.lang.String)
	 */
	@Override
	public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId) {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByConversationId(conversationId);

		DomibusConnectorClientMessageList messages = mapMessages(msg);

		return messages;
	}

	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.rest.IDomibusConnectorClientRestAPI#getMessagesByPeriod(java.util.Date, java.util.Date)
	 */
	@Override
	public DomibusConnectorClientMessageList getMessagesByPeriod(Date from, Date to) {
		List<PDomibusConnectorClientMessage> msg = persistenceService.getMessageDao().findByPeriod(from, to);

		DomibusConnectorClientMessageList messages = mapMessages(msg);

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
		return newMessage;
	}

	private DomibusConnectorClientMessageList mapMessages(Iterable<PDomibusConnectorClientMessage> findAll) {
		DomibusConnectorClientMessageList messages = new DomibusConnectorClientMessageList();

		findAll.forEach(message -> {
			DomibusConnectorClientMessage msg = mapMessage(message);
			messages.getMessages().add(msg);
		});

		return messages;
	}

	private DomibusConnectorClientMessage mapMessage(PDomibusConnectorClientMessage message) {
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
		
		if(filesReadable(message)) {
			Map<String, DomibusConnectorClientMessageFileType> files = storage.listContentAtStorageLocation(message.getStorageInfo());
			files.entrySet().forEach(file -> {
				msg.getFiles().add(new DomibusConnectorClientMessageFile(file.getKey(), file.getValue().name()));
			});
		}
		
		return msg;
	}
	
	private boolean filesReadable(PDomibusConnectorClientMessage message) {
		return message!=null && message.getStorageInfo()!=null && !message.getStorageInfo().isEmpty() && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED);
	}
}

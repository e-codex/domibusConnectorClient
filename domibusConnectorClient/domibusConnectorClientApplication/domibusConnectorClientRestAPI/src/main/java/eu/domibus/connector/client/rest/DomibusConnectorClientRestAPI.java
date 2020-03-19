package eu.domibus.connector.client.rest;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.domibus.connector.client.controller.persistence.dao.PDomibusConnectorClientMessageDao;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

@RestController
public class DomibusConnectorClientRestAPI {
	
	@Autowired
	private PDomibusConnectorClientMessageDao messageDao;

	public DomibusConnectorClientRestAPI() {
		// TODO Auto-generated constructor stub
	}
	
	@GetMapping("/getAllMessages")
	public DomibusConnectorClientMessageList getAllMessages(){
		 Iterable<PDomibusConnectorClientMessage> findAll = messageDao.findAll();
		 
		 DomibusConnectorClientMessageList messages = mapMessages(findAll);
		 
		 return messages;
	}

	@GetMapping("/getMessageById")
	public DomibusConnectorClientMessage getMessageById(@RequestParam Long id) {
		Optional<PDomibusConnectorClientMessage> msg = messageDao.findById(id);
		
		if(msg.get() !=null) {
			DomibusConnectorClientMessage message = mapMessage(msg.get());
			return message;
		}
		return null;
	}
	
	@GetMapping("/getMessagesByBackendMessageId")
	public DomibusConnectorClientMessageList getMessagesByBackendMessageId(@RequestParam String backendMessageId) {
		List<PDomibusConnectorClientMessage> msg = messageDao.findByBackendMessageId(backendMessageId);
		
		DomibusConnectorClientMessageList messages = mapMessages(msg);
		 
		 return messages;
	}
	
	@GetMapping("/getMessagesByEbmsMessageId")
	public DomibusConnectorClientMessageList getMessagesByEbmsMessageId(@RequestParam String ebmsMessageId) {
		List<PDomibusConnectorClientMessage> msg = messageDao.findByEbmsMessageId(ebmsMessageId);
		
		DomibusConnectorClientMessageList messages = mapMessages(msg);
		 
		 return messages;
	}
	
	@GetMapping("/getMessagesByConversationId")
	public DomibusConnectorClientMessageList getMessagesByConversationId(@RequestParam String conversationId) {
		List<PDomibusConnectorClientMessage> msg = messageDao.findByConversationId(conversationId);
		
		DomibusConnectorClientMessageList messages = mapMessages(msg);
		 
		 return messages;
	}
	
	@GetMapping("/getMessagesByPeriod")
	public DomibusConnectorClientMessageList getMessagesByPeriod(@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date from, 
			@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date to) {
		List<PDomibusConnectorClientMessage> msg = messageDao.findByPeriod(from, to);
		
		DomibusConnectorClientMessageList messages = mapMessages(msg);
		 
		 return messages;
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
			 evidence.setStorageStatus(confirmation.getStorageStatus().name());
			 msg.getEvidences().add(evidence);
		 });
		 BeanUtils.copyProperties(message, msg);
		 msg.setStorageStatus(message.getStorageStatus().name());
		 return msg;
	}
}

package eu.domibus.connector.client.rest;

import java.util.Date;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;

@RequestMapping("/restservice")
public interface DomibusConnectorClientRestAPI {

	@GetMapping("/getAllMessages")
	DomibusConnectorClientMessageList getAllMessages();

	@GetMapping("/getMessageById")
	DomibusConnectorClientMessage getMessageById(@RequestParam Long id);

	@GetMapping("/getMessagesByBackendMessageId")
	DomibusConnectorClientMessageList getMessagesByBackendMessageId(@RequestParam String backendMessageId);

	@GetMapping("/getMessagesByEbmsMessageId")
	DomibusConnectorClientMessageList getMessagesByEbmsMessageId(@RequestParam String ebmsMessageId);

	@GetMapping("/getMessagesByConversationId")
	DomibusConnectorClientMessageList getMessagesByConversationId(@RequestParam String conversationId);

	@GetMapping("/getMessagesByPeriod")
	DomibusConnectorClientMessageList getMessagesByPeriod(@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date from, @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date to);

	@GetMapping("/loadContentFromStorage")
	byte[] loadContentFromStorage(@RequestParam String storageLocation, @RequestParam String contentName);

	@GetMapping("/listContentAtStorage")
	Map<String, DomibusConnectorClientMessageFileType> listContentAtStorage(@RequestParam String storageLocation);
	
//	@PostMapping(
//			value = "/createNewMessage", consumes = "application/json", produces = "application/json")
//	DomibusConnectorClientMessage createNewMessage(@RequestBody DomibusConnectorClientMessage newMessage);
	
	@PostMapping(
			value = "/saveMessage", consumes = "application/json", produces = "application/json")
	DomibusConnectorClientMessage saveMessage(@RequestBody DomibusConnectorClientMessage message);
	
	@GetMapping("/deleteMessageById")
	String deleteMessageById(@RequestParam Long id);

}
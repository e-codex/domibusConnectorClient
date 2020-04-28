package eu.domibus.connector.client.rest;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

@RequestMapping("/restservice")
public interface DomibusConnectorClientRestAPI {

	@GetMapping("/getAllMessages")
	DomibusConnectorClientMessageList getAllMessages();

	@GetMapping("/getMessageById")
	DomibusConnectorClientMessage getMessageById(@RequestParam Long id) throws MessageNotFoundException;
	
	@GetMapping("/getMessageByBackendMessageId")
	DomibusConnectorClientMessage getMessageByBackendMessageId(@RequestParam String backendMessageId);

	@GetMapping("/getMessageByEbmsMessageId")
	DomibusConnectorClientMessage getMessageByEbmsMessageId(@RequestParam String ebmsMessageId);
	
	@GetMapping("/getMessagesByConversationId")
	DomibusConnectorClientMessageList getMessagesByConversationId(@RequestParam String conversationId);

	@GetMapping("/getMessagesByPeriod")
	DomibusConnectorClientMessageList getMessagesByPeriod(@RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date from, @RequestParam @DateTimeFormat(pattern = "dd.MM.yyyy") Date to);

	@GetMapping("/loadFileContentFromStorage")
	byte[] loadFileContentFromStorage(@RequestParam String storageLocation, @RequestParam String fileName);
	
	@PostMapping(
			value = "/saveMessage", consumes = "application/json", produces = "application/json")
	DomibusConnectorClientMessage saveMessage(@RequestBody DomibusConnectorClientMessage message);
	
	@PostMapping(
			value = "/uploadMessageFile", consumes = "application/json", produces = "application/json")
	Boolean uploadMessageFile(@RequestBody DomibusConnectorClientMessageFile messageFile);
	
	@GetMapping("/deleteMessageById")
	Boolean deleteMessageById(@RequestParam Long id);
	
	@GetMapping("/submitStoredClientMessage")
	Boolean submitStoredClientMessage(@RequestParam String storageLocation);

	@PostMapping(
			value = "/deleteMessageFile", consumes = "application/json", produces = "application/json")
	Boolean deleteMessageFile(@RequestBody DomibusConnectorClientMessageFile messageFile);

	

}
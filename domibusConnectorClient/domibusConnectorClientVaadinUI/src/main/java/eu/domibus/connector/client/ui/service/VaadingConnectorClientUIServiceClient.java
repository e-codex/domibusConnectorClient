package eu.domibus.connector.client.ui.service;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFileList;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;

@Component
public class VaadingConnectorClientUIServiceClient {

	@Autowired
	private RestTemplate restTemplate;
	
	WebClient client = WebClient.create("http://localhost:8080");
	
	private String url = "http://localhost:8080/restservice";
	
	
	public VaadingConnectorClientUIServiceClient() {
		// TODO Auto-generated constructor stub
	}

	public DomibusConnectorClientMessageList getAllMessages() {
		
		 DomibusConnectorClientMessageList messagesList = new DomibusConnectorClientMessageList();
		 	
				 try {
				 messagesList = restTemplate.getForObject(url+"/getAllMessages", DomibusConnectorClientMessageList.class);
				 }catch (Exception e) {
					 
				 }
		return messagesList;
	}
	
	public DomibusConnectorClientMessage getMessageById(Long id) {
		return restTemplate.getForObject(url+"/getMessageById?id={id}", DomibusConnectorClientMessage.class, id.toString());
	}

	public DomibusConnectorClientMessage getMessageByBackendMessageId(String backendMessageId) {
		return restTemplate.getForObject(url+"/getMessageByBackendMessageId?backendMessageId={id}", DomibusConnectorClientMessage.class, backendMessageId);
	}

	public DomibusConnectorClientMessage getMessageByEbmsId(String ebmsId) {
		return restTemplate.getForObject(url+"/getMessageByEbmsMessageId?ebmsMessageId={id}", DomibusConnectorClientMessage.class, ebmsId);
	}

	public DomibusConnectorClientMessageList getMessagesByPeriod(Date fromDate, Date toDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return restTemplate.getForObject(url+"/getMessagesByPeriod?from={from}&to={to}", DomibusConnectorClientMessageList.class, sdf.format(fromDate), sdf.format(toDate));
	}

	public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId) {
		return restTemplate.getForObject(url+"/getMessagesByConversationId?conversationId={id}", DomibusConnectorClientMessageList.class, conversationId);
	}
	
	public byte[] loadFileContentFromStorageLocation (String storageLocation, String fileName) {
		ResponseEntity<byte[]> result = restTemplate.exchange(url + "/loadContentFromStorage?storageLocation={storageLocation}&fileName={fileName}", HttpMethod.GET, null, byte[].class,storageLocation, fileName);
		return result.getBody();
	}
	
//	public Map<String, DomibusConnectorClientMessageFileType> listContentAtStorage(String storageLocation){
//		ResponseEntity<Map> result = restTemplate.exchange(url + "/listContentAtStorage?storageLocation={storageLocation}", HttpMethod.GET, null, Map.class,storageLocation);
//		return result.getBody();
//	}
//	
//	public DomibusConnectorClientMessage createNewMessage(DomibusConnectorClientMessage newMessage) {
//		newMessage = restTemplate.postForObject(url + "/createNewMessage", newMessage, DomibusConnectorClientMessage.class);
//		return newMessage;
//	}
//	
	public DomibusConnectorClientMessage saveMessage(DomibusConnectorClientMessage message) {
		message = restTemplate.postForObject(url + "/saveMessage", message, DomibusConnectorClientMessage.class);
		return message;
	}
	
	public boolean uploadFileToMessage(DomibusConnectorClientMessageFile messageFile) {
		Boolean result = restTemplate.postForObject(url + "/uploadMessageFile", messageFile, Boolean.class);
		return result.booleanValue();
	}
	
	public boolean deleteFileFromMessage(DomibusConnectorClientMessageFile messageFile) {
		Boolean result = restTemplate.postForObject(url + "/deleteMessageFile", messageFile, Boolean.class);
		return result.booleanValue();
	}
	
	public void deleteMessageById(Long id) {
		restTemplate.getForObject(url+"/deleteMessageById?id={id}", String.class, id.toString());
	}
	
	public String submitStoredMessage(String storageLocation) {
		return restTemplate.getForObject(url+"/submitClientMessage?storageLocation={storageLocation}", String.class, storageLocation);
	}
}

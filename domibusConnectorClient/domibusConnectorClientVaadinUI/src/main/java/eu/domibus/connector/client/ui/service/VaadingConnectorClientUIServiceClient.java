package eu.domibus.connector.client.ui.service;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;

@Component
public class VaadingConnectorClientUIServiceClient {

	@Autowired
	private RestTemplate restTemplate;
	
	private String url = "http://localhost:8080";
	
	
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

	public DomibusConnectorClientMessageList getMessagesByBackendMessageId(String backendMessageId) {
		return restTemplate.getForObject(url+"/getMessagesByBackendMessageId?backendMessageId={id}", DomibusConnectorClientMessageList.class, backendMessageId);
	}

	public DomibusConnectorClientMessageList getMessagesByEbmsId(String ebmsId) {
		return restTemplate.getForObject(url+"/getMessagesByEbmsMessageId?ebmsMessageId={id}", DomibusConnectorClientMessageList.class, ebmsId);
	}

	public DomibusConnectorClientMessageList getMessagesByPeriod(Date fromDate, Date toDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		return restTemplate.getForObject(url+"/getMessagesByPeriod?from={from}&to={to}", DomibusConnectorClientMessageList.class, sdf.format(fromDate), sdf.format(toDate));
	}

	public DomibusConnectorClientMessageList getMessagesByConversationId(String conversationId) {
		return restTemplate.getForObject(url+"/getMessagesByConversationId?conversationId={id}", DomibusConnectorClientMessageList.class, conversationId);
	}
	
	public byte[] loadContentFromStorageLocation (String storageLocation, String contentName) {
		ResponseEntity<byte[]> result = restTemplate.exchange(url + "loadContentFromStorage?storageLocation={storageLocation}&contentName={contentName}", HttpMethod.GET, null, byte[].class,storageLocation, contentName);
		return result.getBody();
	}
	
	public Map<String, DomibusConnectorClientMessageFileType> listContentAtStorage(String storageLocation){
		ResponseEntity<Map> result = restTemplate.exchange(url + "listContentAtStorage?storageLocation={storageLocation}", HttpMethod.GET, null, Map.class,storageLocation);
		return result.getBody();
	}
	
	public DomibusConnectorClientMessage createNewMessage(DomibusConnectorClientMessage newMessage) {
		newMessage = restTemplate.postForObject(url + "/createNewMessage", newMessage, DomibusConnectorClientMessage.class);
		return newMessage;
	}
}

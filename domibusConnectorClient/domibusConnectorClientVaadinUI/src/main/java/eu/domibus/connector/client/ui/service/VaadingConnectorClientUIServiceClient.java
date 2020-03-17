package eu.domibus.connector.client.ui.service;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

@Component
public class VaadingConnectorClientUIServiceClient {

	@Autowired
	private RestTemplate restTemplate;
	
	private String url = "http://localhost:8080";
	
	
	public VaadingConnectorClientUIServiceClient() {
		// TODO Auto-generated constructor stub
	}

	public DomibusConnectorClientMessageList getAllMessages() {
		
		 return restTemplate.getForObject(url+"/getAllMessages", DomibusConnectorClientMessageList.class);
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
}

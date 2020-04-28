package eu.domibus.connector.client.ui.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

@Controller
public class VaadingConnectorClientUIServiceClient {

	@Autowired
	private RestTemplate restTemplate;

	WebClient client = WebClient.create("http://localhost:8080/restservice");

	private String url = "http://localhost:8080/restservice";

	public DomibusConnectorClientMessageList getAllMessages() {

		DomibusConnectorClientMessageList messagesList = this.client.get()
				.uri(uriBuilder -> uriBuilder
						.path("/getAllMessages")
						.build())
				.retrieve()
				.bodyToMono(DomibusConnectorClientMessageList.class)
				.onErrorStop()
				.block();


		return messagesList;
	}

	public DomibusConnectorClientMessage getMessageById(Long id) throws ConnectorClientServiceClientException 
	{
		try {
			DomibusConnectorClientMessage message = this.client.get()
					.uri(uriBuilder -> uriBuilder
							.path("/getMessageById")
							.queryParam("id", id)
							.build(id))
					.retrieve()
					.bodyToMono(DomibusConnectorClientMessage.class)
					.onErrorStop()
					.block();


			return message;
		}catch(WebClientResponseException e) {
			throw new ConnectorClientServiceClientException(e.getResponseBodyAsString());
		}
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
		ResponseEntity<byte[]> result = restTemplate.exchange(url + "/loadFileContentFromStorage?storageLocation={storageLocation}&fileName={fileName}", HttpMethod.GET, null, byte[].class,storageLocation, fileName);
		return result.getBody();
	}

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
		restTemplate.postForObject(url+"/deleteMessageById", id, Boolean.class);
	}

	public boolean submitStoredMessage(DomibusConnectorClientMessage message) {
		Boolean result =  restTemplate.postForObject(url+"/submitStoredClientMessage", message, Boolean.class);
		return result.booleanValue();
	}
}

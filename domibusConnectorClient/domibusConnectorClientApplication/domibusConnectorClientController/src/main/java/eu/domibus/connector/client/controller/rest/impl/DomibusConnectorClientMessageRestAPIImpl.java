package eu.domibus.connector.client.controller.rest.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.controller.rest.util.DomibusConnectorClientRestUtil;
import eu.domibus.connector.client.rest.DomibusConnectorClientMessageRestAPI;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

@RestController
@RequestMapping("/messagerestservice")
public class DomibusConnectorClientMessageRestAPIImpl implements DomibusConnectorClientMessageRestAPI {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientMessageRestAPIImpl.class);

	@Autowired
	private IDomibusConnectorClientPersistenceService persistenceService;
	
	@Autowired
	private DomibusConnectorClientRestUtil util;
	
	@Override
	public DomibusConnectorClientMessageList requestNewMessagesFromConnectorClient() {
		List<PDomibusConnectorClientMessage> receivedMessages = persistenceService.getMessageDao().findReceived();
		
		DomibusConnectorClientMessageList clientMessages = util.mapMessagesFromModel(receivedMessages);
		
		receivedMessages.forEach(message -> {
						
		      message.setMessageStatus(PDomibusConnectorClientMessageStatus.DELIVERED_BACKEND);
		      persistenceService.mergeClientMessage(message);
		});
		return clientMessages;
	}

	@Override
	public DomibusConnectorClientMessageList requestRejectedOrConfirmedMessagesFromConnectorClient() {
		List<PDomibusConnectorClientMessage> rejectedOrConfirmedMessages = persistenceService.getMessageDao().findRejectedConfirmed();
		
		DomibusConnectorClientMessageList clientMessages = util.mapMessagesFromModel(rejectedOrConfirmedMessages);
		
		rejectedOrConfirmedMessages.forEach(message -> {
			
		      message.setMessageStatus(PDomibusConnectorClientMessageStatus.CONFIRMATION_DELIVERED_BACKEND);
		      persistenceService.mergeClientMessage(message);
		});
		return clientMessages;
	}

}

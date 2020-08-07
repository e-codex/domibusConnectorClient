package eu.domibus.connector.client.controller.rest.impl;

import javax.validation.constraints.NotNull;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.domibus.connector.client.DomibusConnectorClientAppBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.rest.DomibusConnectorClientSubmissionRestAPI;
import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.exception.MessageSubmissionException;
import eu.domibus.connector.client.rest.exception.ParameterException;
import eu.domibus.connector.client.rest.exception.StorageException;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@RestController
@RequestMapping("/submissionrestservice")
public class DomibusConnectorClientSubmissionRestAPIImpl implements DomibusConnectorClientSubmissionRestAPI {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientRestAPIImpl.class);

	@Autowired
	private IDomibusConnectorClientPersistenceService persistenceService;

	@Autowired
	private DomibusConnectorClientStorage storage;
	
	@Autowired
	private DomibusConnectorClientAppBackend connectorClientBackend;
	
	@Autowired
	@NotNull
    private DomibusConnectorClientMessageBuilder messageBuilder;

	
	@Override
	public Boolean submitNewMessageFromBackendToConnectorClient(DomibusConnectorMessageType message) throws MessageSubmissionException, StorageException, ParameterException {
		
		//persist the message into the connector client database...
		PDomibusConnectorClientMessage pMessage = persistenceService.persistNewMessage(message, PDomibusConnectorClientMessageStatus.PREPARED);
		
		//store the message into storage...
		String storageLocation = null;
		try {
			storageLocation = storage.storeMessage(message);
		} catch (DomibusConnectorClientStorageException e) {
			throw new StorageException("Storage failure: "+e.getMessage(), e);
		}
		
		//update database object with storage info and merge into database...
		pMessage.setStorageInfo(storageLocation);
		pMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);

		persistenceService.mergeClientMessage(pMessage);
		
		
		//submit the message to connector.
		try {
			connectorClientBackend.submitStoredClientBackendMessage(pMessage.getStorageInfo());
		} catch (DomibusConnectorClientBackendException e) {
			throw new MessageSubmissionException("Client backend failure: "+ e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ParameterException("Parameter failure: "+e.getMessage(), e);
		} catch (DomibusConnectorClientStorageException e) {
			throw new StorageException("Storage failure: "+e.getMessage(), e);
		}
		
		return Boolean.TRUE;
	}


	@Override
	public Boolean triggerConfirmationAtConnectorClient(DomibusConnectorMessageType message)
			throws MessageSubmissionException, ParameterException, MessageNotFoundException {
		
		if(message == null || message.getMessageDetails() == null) {
			throw new ParameterException("Message structure not valid! No message details contained!");
		}
		
		if(StringUtils.isEmpty(message.getMessageDetails().getRefToMessageId())) {
			throw new ParameterException("RefToMessageId must not be null or empty!");
		}
		
		if(message.getMessageConfirmations()==null || message.getMessageConfirmations().isEmpty()) {
			throw new ParameterException("Message structure not valid! No confirmationType contained!");
		}
		
		//find original message...
		PDomibusConnectorClientMessage originalClientMessage = persistenceService.findOriginalClientMessage(message);
		
		if(originalClientMessage == null) {
			throw new MessageNotFoundException("Original client message with refToMessageId "+message.getMessageDetails().getRefToMessageId()+" not found!") ;
		}
		
		DomibusConnectorMessageType originalMessage = messageBuilder.createNewMessage(
				originalClientMessage.getBackendMessageId(), 
				originalClientMessage.getEbmsMessageId(), 
				originalClientMessage.getConversationId(), 
				originalClientMessage.getService(), 
				null, 
				originalClientMessage.getAction(), 
				originalClientMessage.getFromPartyId(), 
				originalClientMessage.getFromPartyType(), 
				originalClientMessage.getFromPartyRole(), 
				originalClientMessage.getToPartyId(), 
				originalClientMessage.getToPartyType(), 
				originalClientMessage.getToPartyRole(), 
				originalClientMessage.getFinalRecipient(), 
				originalClientMessage.getOriginalSender());
		
		//extract confirmation type
		DomibusConnectorConfirmationType confirmationType = message.getMessageConfirmations().get(0).getConfirmationType();
		
		
		try {
			connectorClientBackend.triggerConfirmationForMessage(originalMessage, confirmationType, null);
		} catch (DomibusConnectorClientBackendException e) {
			throw new MessageSubmissionException("Client backend failure: "+ e.getMessage(), e);
		}
		return null;
	}

}

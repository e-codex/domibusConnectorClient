package eu.domibus.connector.client.controller.backend.impl;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.configuration.DefaultConfirmationAction;
import eu.domibus.connector.client.controller.configuration.DomibusConnectorClientControllerConfig;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
@ConfigurationProperties(prefix = DomibusConnectorClientControllerConfig.PREFIX)
@PropertySource("classpath:/connector-client-controller-default.properties")
@Validated
@Valid
public class DomibusConnectorClientBackendImpl implements DomibusConnectorClientBackend{
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientBackendImpl.class);
	
	@Autowired
	@NotNull
	private DomibusConnectorClientStorage storage;
	
	@Autowired
	@NotNull
	private IDomibusConnectorClientPersistenceService persistenceService;
	
	@Autowired
	@NotNull
    private DomibusConnectorClient connectorClient;
	
	@Autowired
	@NotNull
    private DomibusConnectorClientMessageBuilder messageBuilder;
	
	  @NotNull
	    private DefaultConfirmationAction confirmationDefaultAction;
	
	public DomibusConnectorClientBackendImpl() {
	}

	@Override
	public DomibusConnectorMessagesType checkClientForNewMessagesToSubmit() throws DomibusConnectorClientBackendException {
		LOGGER.debug("#checkClientForNewMessagesToSubmit: called");
		
		Map<String, DomibusConnectorMessageType> newMessages = storage.checkStorageForNewMessages();
		
		if(newMessages!=null && !newMessages.isEmpty()) {
			DomibusConnectorMessagesType messages = new DomibusConnectorMessagesType();
			newMessages.keySet().forEach(newMessageLocation -> {
				DomibusConnectorMessageType message = newMessages.get(newMessageLocation);
				PDomibusConnectorClientMessage clientMessage = persistenceService.persistNewMessage(message, PDomibusConnectorClientMessageStatus.SENDING);
				clientMessage.setStorageInfo(newMessageLocation);
				clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
				
				submitAndUpdateMessage(message, clientMessage);
			});
			return messages;
		}
		
		return null;
	}
	
	private void submitAndUpdateMessage(DomibusConnectorMessageType message, PDomibusConnectorClientMessage clientMessage) {
		try {
			connectorClient.submitNewMessageToConnector(message);
			clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.SENT);
			try {
				String newStorageLocation = storage.updateStoredMessageToSent(clientMessage.getStorageInfo());
				clientMessage.setStorageInfo(newStorageLocation);
			} catch (DomibusConnectorClientStorageException e) {
				e.printStackTrace();
			}
			
		} catch (DomibusConnectorClientException e) {
			clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.FAILED);
		}
		
		persistenceService.mergeClientMessage(clientMessage);
	}
	
	@Override
	public void submitStoredClientBackendMessage(String storageLocation) throws DomibusConnectorClientBackendException {
		if(storageLocation == null || storageLocation.isEmpty()) {
			throw new DomibusConnectorClientBackendException("Storage location is null or empty! ");
		}
		
		DomibusConnectorMessageType message;
		try {
			message = storage.getStoredMessage(storageLocation);
		} catch (DomibusConnectorClientStorageException e) {
			throw new DomibusConnectorClientBackendException("ClientMessage could not be read from storage! StorageLocation: "+storageLocation);
		}
		
		if(StringUtils.isEmpty(message.getMessageDetails().getBackendMessageId())) {
			throw new DomibusConnectorClientBackendException("ClientMessage at storageLocation contains no backendMessageId! StorageLocation: "+storageLocation);
		}
		
		PDomibusConnectorClientMessage clientMessage = null;
		Optional<PDomibusConnectorClientMessage> finding = persistenceService.getMessageDao().findOneByBackendMessageId(message.getMessageDetails().getBackendMessageId());
		if(finding.isPresent()) {
			clientMessage = finding.get();
		}else {
			clientMessage = this.persistenceService.persistNewMessage(message, PDomibusConnectorClientMessageStatus.PREPARED);
		}
		
		if(clientMessage.getMessageStatus()==null || !clientMessage.getMessageStatus().equals(PDomibusConnectorClientMessageStatus.PREPARED)) {
			throw new DomibusConnectorClientBackendException("ClientMessage to submit must have messageStatus set as PREPARED! Id: "+clientMessage.getId());
		}
		
		submitAndUpdateMessage(message, clientMessage);
	}
	
	@Override
	public void deliverNewMessageToClientBackend(DomibusConnectorMessageType message) throws DomibusConnectorClientBackendException {
		LOGGER.debug("#deliverNewMessageToClientBackend: called");
		PDomibusConnectorClientMessage clientMessage = persistenceService.persistNewMessage(message, PDomibusConnectorClientMessageStatus.RECEIVING);
		if(clientMessage!=null) {
			clientMessage = persistenceService.persistAllConfirmaitonsForMessage(clientMessage, message);
		}
		LOGGER.debug("#deliverNewMessageToClientBackend: persisted delivered message and its confirmations into database");
		
		String storageLocation = null;
		try {
			storageLocation = storage.storeMessage(message);
		} catch (DomibusConnectorClientStorageException e) {
			clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.FAILED);
			persistenceService.mergeClientMessage(clientMessage);
			throw new DomibusConnectorClientBackendException(e);
		}
		
		clientMessage.setStorageInfo(storageLocation);
		clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
		clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.RECEIVED);
		persistenceService.mergeClientMessage(clientMessage);
		LOGGER.debug("#deliverNewMessageToClientBackend: merged delivered message with storageLocation and storageStatus into database");
	}

	@Override
	public void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message)
			throws DomibusConnectorClientBackendException {
		LOGGER.debug("#deliverNewConfirmationToClientBackend: called");
		DomibusConnectorMessageConfirmationType confirmation = message.getMessageConfirmations().get(0);
		
		PDomibusConnectorClientMessage originalClientMessage = persistenceService.findOriginalClientMessage(message);
		if(originalClientMessage == null) {
			throw new DomibusConnectorClientBackendException("Original client message with ebmsId "+message.getMessageDetails().getRefToMessageId()+" not found! Confirmation of type "+confirmation.getConfirmationType().name()+" cannot be stored!") ;
		}
		
		String storageLocation = originalClientMessage.getStorageInfo();
		if(storageLocation == null && !originalClientMessage.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED)) {
			originalClientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.CONFIRMATION_RECEPTION_FAILED);
			persistenceService.mergeClientMessage(originalClientMessage);
			throw new DomibusConnectorClientBackendException("Storage location or status of originalMessage with ebmsId "+message.getMessageDetails().getRefToMessageId()+" not valid! Confirmation of type "+confirmation.getConfirmationType().name()+" cannot be stored!") ;
		}
		
		try {
			storage.storeConfirmationToMessage(message, storageLocation);
		} catch (DomibusConnectorClientStorageException e) {
			originalClientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.CONFIRMATION_RECEPTION_FAILED);
			persistenceService.mergeClientMessage(originalClientMessage);
			throw new DomibusConnectorClientBackendException(e);
		}
		LOGGER.debug("#deliverNewConfirmationToClientBackend: confirmation stored.");
		
		if(originalClientMessage!=null) {
			PDomibusConnectorClientConfirmation newConfirmation = persistenceService.persistNewConfirmation(confirmation, originalClientMessage);
			originalClientMessage.getConfirmations().add(newConfirmation);
			originalClientMessage.setLastConfirmationReceived(newConfirmation.getConfirmationType());
			
			switch (confirmation.getConfirmationType()) {
			case RETRIEVAL:
			case DELIVERY: originalClientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.CONFIRMED);break;
			case NON_DELIVERY:
			case SUBMISSION_REJECTION:
			case RELAY_REMMD_FAILURE:
			case NON_RETRIEVAL:
			case RELAY_REMMD_REJECTION: originalClientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.REJECTED);break;
			case RELAY_REMMD_ACCEPTANCE:
			case SUBMISSION_ACCEPTANCE: originalClientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.ACCEPTED);break;
			default:
				break;
			}
			
//			originalClientMessage.setUpdated(new Date());
			persistenceService.mergeClientMessage(originalClientMessage);
			LOGGER.debug("#deliverNewConfirmationToClientBackend: confirmation persisted into database and merged with original message.");
		}
	}

	@Override
	public void triggerConfirmationForMessage(DomibusConnectorMessageType originalMessage,
			DomibusConnectorConfirmationType confirmationType, String confirmationAction) throws DomibusConnectorClientBackendException {
		if(confirmationAction==null || confirmationAction.isEmpty()) {
			switch(confirmationType) {
			case DELIVERY:
			case NON_DELIVERY: confirmationAction = confirmationDefaultAction.getDeliveryNonDeliveryToRecipient();break;
			case RETRIEVAL:
			case NON_RETRIEVAL: confirmationAction = confirmationDefaultAction.getRetrievalNonRetrievalToRecipient();break;
			default: throw new DomibusConnectorClientBackendException("ConfirmationType invalid for connnectorClient to trigger! "+confirmationType.name());
			}
		}
		
		DomibusConnectorMessageType confirmationMessage = messageBuilder.createNewConfirmationMessage(
				originalMessage.getMessageDetails().getEbmsMessageId(), 
				originalMessage.getMessageDetails().getConversationId(), 
				originalMessage.getMessageDetails().getService(), 
				confirmationAction, 
				originalMessage.getMessageDetails().getToParty(), 
				originalMessage.getMessageDetails().getFromParty(), 
				originalMessage.getMessageDetails().getOriginalSender(), 
				originalMessage.getMessageDetails().getFinalRecipient(), 
				confirmationType
				);
		
		
		try {
			connectorClient.triggerConfirmationForMessage(confirmationMessage);
		} catch (DomibusConnectorClientException e) {
			throw new DomibusConnectorClientBackendException(e);
		}
		
		
	}

	public DefaultConfirmationAction getConfirmationDefaultAction() {
		return confirmationDefaultAction;
	}

	public void setConfirmationDefaultAction(DefaultConfirmationAction confirmationDefaultAction) {
		this.confirmationDefaultAction = confirmationDefaultAction;
	}

	
//	private String generateDomibusConnectorClientBackendId() {
//		return UUID.randomUUID().toString()+"@connector-client.eu";
//	}

}

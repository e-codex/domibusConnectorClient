package eu.domibus.connector.client.controller.backend.impl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientAppBackend;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.controller.configuration.DefaultConfirmationAction;
import eu.domibus.connector.client.controller.configuration.DomibusConnectorClientControllerConfig;
import eu.domibus.connector.client.controller.configuration.DomibusConnectorClientRestClientConfig;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessageStatus;
import eu.domibus.connector.client.controller.persistence.service.IDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.controller.rest.impl.DomibusConnectorClientDeliveryRestClient;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.storage.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
@ConfigurationProperties(prefix = DomibusConnectorClientControllerConfig.PREFIX)
@PropertySource("classpath:/connector-client-controller-default.properties")
@Validated
@Valid
public class DomibusConnectorClientBackendImpl implements DomibusConnectorClientAppBackend{
	
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
	
	@Autowired
	@Nullable
	private DomibusConnectorClientDeliveryRestClient deliveryRestClient;
	
	
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
				
				try {
					submitAndUpdateMessage(message, clientMessage);
				} catch (DomibusConnectorClientBackendException | DomibusConnectorClientStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
			return messages;
		}
		
		return null;
	}
	
	private void submitAndUpdateMessage(DomibusConnectorMessageType message, PDomibusConnectorClientMessage clientMessage) throws DomibusConnectorClientBackendException, DomibusConnectorClientStorageException {
		try {
			connectorClient.submitNewMessageToConnector(message);
			clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.SENT);
			
		} catch (DomibusConnectorClientException e) {
			clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.FAILED);
			persistenceService.mergeClientMessage(clientMessage);
			throw new DomibusConnectorClientBackendException("Exception submitting message through domibusConnectorClientLibrary!",e);
		}

		try {
			String newStorageLocation = storage.updateStoredMessageToSent(clientMessage.getStorageInfo());
			clientMessage.setStorageInfo(newStorageLocation);
		} catch (DomibusConnectorClientStorageException e) {
			throw new DomibusConnectorClientStorageException("Exception updating stored message to \"sent\"!", e);
		}
		
		persistenceService.mergeClientMessage(clientMessage);
	}
	
	@Override
	public void submitStoredClientBackendMessage(String storageLocation) throws DomibusConnectorClientBackendException, DomibusConnectorClientStorageException, IllegalArgumentException {
		if(storageLocation == null || storageLocation.isEmpty()) {
			throw new IllegalArgumentException("Storage location is null or empty! ");
		}
		
		DomibusConnectorMessageType message;
		try {
			message = storage.getStoredMessage(storageLocation);
		} catch (DomibusConnectorClientStorageException e) {
			throw new DomibusConnectorClientStorageException("ClientMessage could not be read from storage! StorageLocation: "+storageLocation);
		}
		
		if(StringUtils.isEmpty(message.getMessageDetails().getBackendMessageId())) {
			throw new DomibusConnectorClientStorageException("ClientMessage at storageLocation contains no backendMessageId! StorageLocation: "+storageLocation);
		}
		
		PDomibusConnectorClientMessage clientMessage = null;
		Optional<PDomibusConnectorClientMessage> finding = persistenceService.getMessageDao().findOneByBackendMessageId(message.getMessageDetails().getBackendMessageId());
		if(finding.isPresent()) {
			clientMessage = finding.get();
		}else {
			LOGGER.info("Message in database using backendMessageId {} not found! Persist as new message...", message.getMessageDetails().getBackendMessageId());
			clientMessage = this.persistenceService.persistNewMessage(message, PDomibusConnectorClientMessageStatus.PREPARED);
		}
		
		if(clientMessage.getMessageStatus()==null || !clientMessage.getMessageStatus().equals(PDomibusConnectorClientMessageStatus.PREPARED)) {
			throw new DomibusConnectorClientBackendException("ClientMessage in database to submit must have the messageStatus set as PREPARED! Database id: "+clientMessage.getId());
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
		
		if(deliveryRestClient!=null) {
			LOGGER.info("Delivery Rest Client to Backend is there... message will be delivered!");
			
			DomibusConnectorClientMessage msg = new DomibusConnectorClientMessage();
			Set<PDomibusConnectorClientConfirmation> confirmations = clientMessage.getConfirmations();
			confirmations.forEach(confirmation -> {
				DomibusConnectorClientConfirmation evidence = new DomibusConnectorClientConfirmation();
				BeanUtils.copyProperties(confirmation, evidence);
				//			evidence.setStorageStatus(confirmation.getStorageStatus().name());
				msg.getEvidences().add(evidence);
			});
			BeanUtils.copyProperties(clientMessage, msg);
			msg.setStorageStatus(clientMessage.getStorageStatus().name());
			msg.setMessageStatus(clientMessage.getMessageStatus().name());
			
			if(filesReadable(clientMessage)) {
				Map<String, DomibusConnectorClientStorageFileType> files = null;
				try {
					files = storage.listContentAtStorageLocation(clientMessage.getStorageInfo());
				} catch (DomibusConnectorClientStorageException | IllegalArgumentException e) {
					DomibusConnectorClientStorageStatus checkStorageStatus = storage.checkStorageStatus(clientMessage.getStorageInfo());
					clientMessage.setStorageStatus(checkStorageStatus);
					persistenceService.mergeClientMessage(clientMessage);
					
				}
				files.entrySet().forEach(file -> {
					DomibusConnectorClientMessageFileType fileType = DomibusConnectorClientMessageFileType.valueOf(file.getValue().name());
					DomibusConnectorClientMessageFile file2 = new DomibusConnectorClientMessageFile(file.getKey(), fileType);
					msg.getFiles().add(file2);
				});
			}
			try {
				deliveryRestClient.deliverNewMessageFromConnectorClientToBackend(msg);
				clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.DELIVERED_BACKEND);
			} catch (Exception e) {
				LOGGER.error("Delivery to client backend via Rest service failed! ", e);
				clientMessage.setMessageStatus(PDomibusConnectorClientMessageStatus.DELIVERY_FAILED);
			}
			
			persistenceService.mergeClientMessage(clientMessage);
		}
	}

	@Override
	public void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message)
			throws DomibusConnectorClientBackendException {
		LOGGER.debug("#deliverNewConfirmationToClientBackend: called");
		DomibusConnectorMessageConfirmationType confirmation = message.getMessageConfirmations().get(0);
		
		PDomibusConnectorClientMessage originalClientMessage = persistenceService.findOriginalClientMessage(message);
		if(originalClientMessage == null) {
			throw new DomibusConnectorClientBackendException("Original client message with refToMessageId "+message.getMessageDetails().getRefToMessageId()+" not found! Confirmation of type "+confirmation.getConfirmationType().name()+" cannot be stored!") ;
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
		
		if(deliveryRestClient!=null) {
			LOGGER.info("Delivery Rest Client to Backend is there... confirmation will be delivered!");
			
			try {
				deliveryRestClient.deliverNewConfirmationFromConnectorClientToBackend(message);
			} catch (Exception e) {
				LOGGER.error("Delivery to client backend via Rest service failed! ", e);
			}
			
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
	
	private boolean filesReadable(PDomibusConnectorClientMessage message) {
		return message!=null && message.getStorageInfo()!=null && !message.getStorageInfo().isEmpty() && message.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED);
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

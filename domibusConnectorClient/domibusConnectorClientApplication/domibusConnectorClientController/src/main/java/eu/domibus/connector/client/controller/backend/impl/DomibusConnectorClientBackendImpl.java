package eu.domibus.connector.client.controller.backend.impl;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientConfirmation;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientMessage;
import eu.domibus.connector.client.controller.persistence.service.PDomibusConnectorClientPersistenceService;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
public class DomibusConnectorClientBackendImpl implements DomibusConnectorClientBackend{
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientBackendImpl.class);
	
	@Autowired
	@NotNull
	private DomibusConnectorClientStorage storage;
	
	@Autowired
	@NotNull
	private PDomibusConnectorClientPersistenceService persistenceService;
	
	public DomibusConnectorClientBackendImpl() {
	}

	@Override
	public DomibusConnectorMessagesType checkClientForNewMessagesToSubmit() {
		LOGGER.debug("#checkClientForNewMessagesToSubmit: called");
		
		Map<String, DomibusConnectorMessageType> newMessages = storage.checkStorageForNewMessages();
		
		if(newMessages!=null && !newMessages.isEmpty()) {
			DomibusConnectorMessagesType messages = new DomibusConnectorMessagesType();
			newMessages.keySet().forEach(newMessageLocation -> {
				DomibusConnectorMessageType message = newMessages.get(newMessageLocation);
				PDomibusConnectorClientMessage clientMessage = persistenceService.persistNewMessage(message);
				clientMessage.setStorageInfo(newMessageLocation);
				clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
				
				persistenceService.mergeClientMessage(clientMessage);
			});
			return messages;
		}
		
		return null;
	}

	@Override
	public void deliverNewMessageToClientBackend(DomibusConnectorMessageType message) throws DomibusConnectorClientBackendException {
		
		PDomibusConnectorClientMessage clientMessage = persistenceService.persistNewMessage(message);
		if(clientMessage!=null) {
			clientMessage = persistenceService.persistAllConfirmaitonsForMessage(clientMessage, message);
		}
		
		String storageLocation = null;
		try {
			storageLocation = storage.storeMessage(message);
		} catch (DomibusConnectorClientStorageException e) {
			throw new DomibusConnectorClientBackendException(e);
		}
		
		clientMessage.setStorageInfo(storageLocation);
		clientMessage.setStorageStatus(DomibusConnectorClientStorageStatus.STORED);
		
		persistenceService.mergeClientMessage(clientMessage);
	}

	@Override
	public void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message)
			throws DomibusConnectorClientBackendException {
		
		DomibusConnectorMessageConfirmationType confirmation = message.getMessageConfirmations().get(0);
		
		PDomibusConnectorClientMessage originalClientMessage = persistenceService.findOriginalClientMessage(message);
		String storageLocation = null;
		if(originalClientMessage!=null)
			storageLocation = originalClientMessage.getStorageInfo() + File.separator + confirmation.getConfirmationType().name();
		
		try {
			storage.storeConfirmationToMessage(message, storageLocation);
		} catch (DomibusConnectorClientStorageException e) {
			throw new DomibusConnectorClientBackendException(e);
		}
		
		if(originalClientMessage!=null) {
			PDomibusConnectorClientConfirmation newConfirmation = persistenceService.persistNewConfirmation(confirmation, originalClientMessage);
			originalClientMessage.getConfirmations().add(newConfirmation);
			originalClientMessage.setLastConfirmationReceived(newConfirmation.getConfirmationType());
			originalClientMessage.setUpdated(new Date());
			persistenceService.mergeClientMessage(originalClientMessage);
		}
	}

	
//	private String generateDomibusConnectorClientBackendId() {
//		return UUID.randomUUID().toString()+"@connector-client.eu";
//	}

}

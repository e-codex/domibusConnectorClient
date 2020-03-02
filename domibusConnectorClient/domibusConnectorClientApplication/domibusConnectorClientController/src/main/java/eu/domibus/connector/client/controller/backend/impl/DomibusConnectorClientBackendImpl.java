package eu.domibus.connector.client.controller.backend.impl;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientStorageException;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
public class DomibusConnectorClientBackendImpl implements DomibusConnectorClientBackend{
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientBackendImpl.class);
	
	@Autowired
	@NotNull
	private DomibusConnectorClientStorage storage;
	
	public DomibusConnectorClientBackendImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public DomibusConnectorMessagesType checkClientForNewMessagesToSubmit() {
		LOGGER.debug("#checkClientForNewMessagesToSubmit: called");
		return storage.checkStorageForNewMessages();
	}

	@Override
	public void deliverNewMessageToClientBackend(DomibusConnectorMessageType message) throws DomibusConnectorClientBackendException {
		
		//TODO: persist message
		
		String storageLocation = null;
		try {
			storageLocation = storage.storeMessage(message);
		} catch (DomibusConnectorClientStorageException e) {
			throw new DomibusConnectorClientBackendException(e);
		}
		
		//TODO: update message with storageLocation
	
	}

	@Override
	public void deliverNewConfirmationToClientBackend(DomibusConnectorMessageType message)
			throws DomibusConnectorClientBackendException {
		
		//TODO: find original message, get destination path
		
		try {
			storage.storeConfirmationToMessage(message);
		} catch (DomibusConnectorClientStorageException e) {
			throw new DomibusConnectorClientBackendException(e);
		}
		
		//TODO: update message status
	}

	
//	private String generateDomibusConnectorClientBackendId() {
//		return UUID.randomUUID().toString()+"@connector-client.eu";
//	}

}

package eu.domibus.connector.client.controller.backend.impl;

import org.springframework.stereotype.Component;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
public class DomibusConnectorClientBackendImpl implements DomibusConnectorClientBackend{

	public DomibusConnectorClientBackendImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public DomibusConnectorMessagesType checkClientForNewMessagesToSubmit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deliverNewMessageToClientBackend(DomibusConnectorMessageType message) {
		// TODO Auto-generated method stub
		
	}

}

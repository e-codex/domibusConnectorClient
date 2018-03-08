package eu.domibus.connector.client.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.connection.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.client.connection.ws.DomibusConnectorBackendWebServiceClient;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
public class DomibusConnectorClientSubmissionController {
	
	@Value("${use.content.mapper}:false")
	private boolean useContentMapper;
	
	@Resource
	private DomibusConnectorContentMapper contentMapper;
	
	@Resource
	private DomibusConnectorBackendWebServiceClient backendWebServiceClient;
	
	public void submitMessageToConnector(DomibusConnectorMessageType message) {
		
		boolean isConfirmation = checkMessageForConfirmation(message);
		
		if(!isConfirmation) {
			if(useContentMapper) {
				try {
					contentMapper.mapNationalToInternational(message);
				} catch (DomibusConnectorContentMapperException | ImplementationMissingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		try {
			backendWebServiceClient.submitMessage(message);
		} catch (DomibusConnectorBackendWebServiceClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private boolean checkMessageForConfirmation(DomibusConnectorMessageType message) {
		return message.getMessageContent()==null && !message.getMessageConfirmations().isEmpty();
	}

}

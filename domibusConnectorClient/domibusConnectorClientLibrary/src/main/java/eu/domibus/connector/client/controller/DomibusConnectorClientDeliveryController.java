package eu.domibus.connector.client.controller;

import java.util.List;

import eu.domibus.connector.client.link.ws.DomibusConnectorBackendWebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.client.link.exception.DomibusConnectorBackendWebServiceClientException;

import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
public class DomibusConnectorClientDeliveryController {

	@Autowired
	private DomibusConnectorContentMapper contentMapper;
	
	@Autowired
	private DomibusConnectorBackendWebServiceClient backendWebServiceClient;
	
	public List<DomibusConnectorMessageType> requestMessages(){
		List<DomibusConnectorMessageType> messages = null;
		try {
			messages = backendWebServiceClient.requestMessages();
		} catch (DomibusConnectorBackendWebServiceClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!CollectionUtils.isEmpty(messages)) {
			for(DomibusConnectorMessageType message:messages) {
					try {
						contentMapper.mapInternationalToNational(message);
					} catch (DomibusConnectorContentMapperException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
		
		return messages;
	}

}

package eu.domibus.connector.client.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import eu.domibus.connector.client.connection.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.client.connection.ws.DomibusConnectorBackendWebServiceClient;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class DomibusConnectorClientDeliveryController {

	@Value("${use.content.mapper}:false")
	private boolean useContentMapper;
	
	@Resource
	private DomibusConnectorContentMapper contentMapper;
	
	@Resource
	private DomibusConnectorBackendWebServiceClient backendWebServiceClient;
	
	public List<DomibusConnectorMessageType> requestMessages(){
		List<DomibusConnectorMessageType> messages = null;
		try {
			messages = backendWebServiceClient.requestMessages();
		} catch (DomibusConnectorBackendWebServiceClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!messages.isEmpty()) {
			for(DomibusConnectorMessageType message:messages) {
				if(useContentMapper) {
					try {
						contentMapper.mapInternationalToNational(message);
					} catch (DomibusConnectorContentMapperException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ImplementationMissingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		return messages;
	}

}

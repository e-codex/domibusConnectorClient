package eu.domibus.connector.client.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import eu.domibus.connector.client.controller.DomibusConnectorClientDeliveryController;
import eu.domibus.connector.client.controller.DomibusConnectorClientSubmissionController;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
public class DomibusConnectorClientServiceImpl implements DomibusConnectorClientService {

	@Resource
	private DomibusConnectorClientSubmissionController submissionController;
	
	@Resource
	private DomibusConnectorClientDeliveryController deliveryController;
	
	@Override
	public List<DomibusConnectorMessageType> requestMessagesFromConnector()
			throws DomibusConnectorClientException {
		List<DomibusConnectorMessageType> messages = deliveryController.requestMessages();
		return messages;
	}

	@Override
	public void submitMessageToConnector(DomibusConnectorMessageType message)
			throws DomibusConnectorClientException {
		submissionController.submitMessageToConnector(message);

	}

}

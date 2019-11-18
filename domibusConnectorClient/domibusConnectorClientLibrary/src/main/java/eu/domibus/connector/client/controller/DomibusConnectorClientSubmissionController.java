package eu.domibus.connector.client.controller;

import eu.domibus.connector.client.link.ws.DomibusConnectorBackendWebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.link.exception.DomibusConnectorBackendWebServiceClientException;

import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


public class DomibusConnectorClientSubmissionController {

    @Autowired
    private DomibusConnectorContentMapper contentMapper;

    @Autowired
    private DomibusConnectorBackendWebServiceClient backendWebServiceClient;

    public void submitMessageToConnector(DomibusConnectorMessageType message) {

        boolean isConfirmation = checkMessageForConfirmation(message);
        if (!isConfirmation) {
            try {
                contentMapper.mapNationalToInternational(message);
            } catch (DomibusConnectorContentMapperException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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
        return message.getMessageContent() == null && !message.getMessageConfirmations().isEmpty();
    }

}

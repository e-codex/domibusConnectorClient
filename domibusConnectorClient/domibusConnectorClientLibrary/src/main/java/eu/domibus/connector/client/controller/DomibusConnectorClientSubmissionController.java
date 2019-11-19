package eu.domibus.connector.client.controller;

import eu.domibus.connector.client.link.ws.DomibusConnectorBackendWebServiceClient;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.link.exception.DomibusConnectorBackendWebServiceClientException;

import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


public class DomibusConnectorClientSubmissionController {

    private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientSubmissionController.class);

    @Autowired
    private DomibusConnectorContentMapper contentMapper;

    @Autowired
    private DomibusConnectorBackendWebServiceClient backendWebServiceClient;

    public DomibusConnectorMessageResponseType submitMessageToConnector(DomibusConnectorMessageType message) {

        boolean isConfirmation = checkMessageForConfirmation(message);
        if (!isConfirmation) {
            try {
                contentMapper.mapNationalToInternational(message);
            } catch (DomibusConnectorContentMapperException e) {
                String error = "Content mapper has failed cannot submit message!";
                throw new DomibusConnectorClientSubmissionException(error, e);
            }
        }
        try {
            DomibusConnectorMessageResponseType domibsConnectorAcknowledgementType = backendWebServiceClient.submitMessage(message);
            return domibsConnectorAcknowledgementType;
        } catch (DomibusConnectorBackendWebServiceClientException e) {
            String error = "backendWebServiceClient has failed, cannot submit message!";
            throw new DomibusConnectorClientSubmissionException(error, e);
        }
    }

    private boolean checkMessageForConfirmation(DomibusConnectorMessageType message) {
        return message.getMessageContent() == null && !message.getMessageConfirmations().isEmpty();
    }

}

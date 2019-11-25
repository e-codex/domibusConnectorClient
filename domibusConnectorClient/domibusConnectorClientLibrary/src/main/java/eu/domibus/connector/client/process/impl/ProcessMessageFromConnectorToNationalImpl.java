package eu.domibus.connector.client.process.impl;

import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.process.ProcessMessageFromConnectorToNational;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessMessageFromConnectorToNationalImpl implements ProcessMessageFromConnectorToNational {

    private static final Logger LOGGER = LogManager.getLogger(ProcessMessageFromConnectorToNationalImpl.class);

    @Autowired
    DomibusConnectorContentMapper contentMapper;

    @Autowired
    DomibusConnectorNationalBackendClient nationalBackendClient;

    @Override
    public DomibusConnectorMessageResponseType processMessageFromConnectorToNational(DomibusConnectorMessageType message) {
        MDC.put("backendmessageid", message.getMessageDetails().getBackendMessageId());
        MDC.put("ebmsid", message.getMessageDetails().getEbmsMessageId());
        DomibusConnectorMessageResponseType messageResponseType = new DomibusConnectorMessageResponseType();
        messageResponseType.setResponseForMessageId(message.getMessageDetails().getEbmsMessageId());
        try {
            contentMapper.mapInternationalToNational(message);
            messageResponseType.setResult(true); //when no exception is thrown message is assumed processed succesfully!
            messageResponseType = nationalBackendClient.pushMessageToNationalBackend(message).orElse(messageResponseType);
        } catch (DomibusConnectorNationalBackendClientException e) {
            messageResponseType.setResult(false);
            messageResponseType.setResultMessage(e.getMessage());
            LOGGER.error("Transporting message [{}] to national system failed!", message);
        } catch (DomibusConnectorContentMapperException e) {
            messageResponseType.setResult(false);
            messageResponseType.setResultMessage(e.getMessage());
            LOGGER.error("Error while mapping message [{}] from international to national failed!", message);
        }
        MDC.remove("backendmessageid");
        MDC.remove("ebmsid");
        return messageResponseType;
    }

    @Override
    public void processResponseFromConnectorToNational(DomibusConnectorMessageResponseType messageResponseType) {
        MDC.put("responsefor", messageResponseType.getResponseForMessageId());
        try {
            nationalBackendClient.setMessageResponse(messageResponseType);
        } catch (DomibusConnectorNationalBackendClientException e) {
            LOGGER.error("Cannot forward messageResponse", e);
        }
        MDC.remove("responsefor");
    }
}

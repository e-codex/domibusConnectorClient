package eu.domibus.connector.client.process.impl;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.DomibusConnectorClientMethodNotSupportedException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.process.ProcessMessageFromNationalToConnector;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessMessageFromNationalToConnectorImpl implements ProcessMessageFromNationalToConnector {

    private static final Logger LOGGER = LogManager.getLogger(ProcessMessageFromNationalToConnectorImpl.class);

    @Autowired
    private DomibusConnectorContentMapper contentMapper;

    @Autowired
    private DomibusConnectorClientService clientService;

    @Override
    public DomibusConnectorMessageResponseType processMessageFromNationalToConnector(DomibusConnectorMessageType message) {
        MDC.put("backendmessageid", message.getMessageDetails().getBackendMessageId());
        MDC.put("ebmsid", message.getMessageDetails().getEbmsMessageId());
        DomibusConnectorMessageResponseType domibusConnectorMessageResponseType = new DomibusConnectorMessageResponseType();
        try {
            contentMapper.mapNationalToInternational(message);
            domibusConnectorMessageResponseType.setResult(true); //when no exception is thrown message is assumed processed successfully!
            domibusConnectorMessageResponseType = clientService.pushMessageToConnector(message).orElse(domibusConnectorMessageResponseType);
        } catch (DomibusConnectorClientException e) {
            domibusConnectorMessageResponseType.setResult(false);
            domibusConnectorMessageResponseType.setResultMessage(e.getMessage());
            LOGGER.error("Exception submitting message to connector: ", e);
        } catch (DomibusConnectorContentMapperException e) {
            domibusConnectorMessageResponseType.setResult(false);
            domibusConnectorMessageResponseType.setResultMessage(e.getMessage());
            LOGGER.error("Exception while mapping message from national to international: ", e);
        }
        MDC.remove("backendmessageid");
        MDC.remove("ebmsid");
        return domibusConnectorMessageResponseType;
    }

    @Override
    public void processResponseFromConnectorToNational(DomibusConnectorMessageResponseType messageResponseType) {
        MDC.put("responsefor", messageResponseType.getResponseForMessageId());
        try {
            clientService.setMessageResponse(messageResponseType);
        } catch (DomibusConnectorClientException e) {
            LOGGER.error("Cannot forward messageResponse", e);
        }
        MDC.remove("responsefor");
    }
}

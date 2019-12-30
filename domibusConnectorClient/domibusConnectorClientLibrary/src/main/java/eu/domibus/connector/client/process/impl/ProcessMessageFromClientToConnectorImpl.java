package eu.domibus.connector.client.process.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.ws.DomibusConnectorClientWSLink;
import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.client.process.ProcessMessageFromClientToConnector;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class ProcessMessageFromClientToConnectorImpl implements ProcessMessageFromClientToConnector {

    private static final Logger LOGGER = LogManager.getLogger(ProcessMessageFromClientToConnectorImpl.class);

    @Autowired
    private DomibusConnectorContentMapper contentMapper;

    @Autowired
    private DomibusConnectorClientWSLink clientService;

    @Override
    public DomibsConnectorAcknowledgementType processMessageFromClientToConnector(DomibusConnectorMessageType message) {
        MDC.put("backendmessageid", message.getMessageDetails().getBackendMessageId());
        MDC.put("ebmsid", message.getMessageDetails().getEbmsMessageId());
        DomibsConnectorAcknowledgementType domibusConnectorAckType = new DomibsConnectorAcknowledgementType();
        try {
            contentMapper.mapNationalToInternational(message);
            domibusConnectorAckType.setResult(true); //when no exception is thrown message is assumed processed successfully!
            domibusConnectorAckType = clientService.submitMessageToConnector(message);
        } catch (DomibusConnectorClientException e) {
            domibusConnectorAckType.setResult(false);
            domibusConnectorAckType.setResultMessage(e.getMessage());
            LOGGER.error("Exception submitting message to connector: ", e);
        } catch (DomibusConnectorContentMapperException e) {
            domibusConnectorAckType.setResult(false);
            domibusConnectorAckType.setResultMessage(e.getMessage());
            LOGGER.error("Exception while mapping message from national to international: ", e);
        }
        MDC.remove("backendmessageid");
        MDC.remove("ebmsid");
        return domibusConnectorAckType;
    }

//    @Override
//    public void processResponseFromConnectorToClient(DomibusConnectorMessageResponseType messageResponseType) {
//        MDC.put("responsefor", messageResponseType.getResponseForMessageId());
//        try {
//            clientService.setMessageResponse(messageResponseType);
//        } catch (DomibusConnectorClientException e) {
//            LOGGER.error("Cannot forward messageResponse", e);
//        }
//        MDC.remove("responsefor");
//    }
}

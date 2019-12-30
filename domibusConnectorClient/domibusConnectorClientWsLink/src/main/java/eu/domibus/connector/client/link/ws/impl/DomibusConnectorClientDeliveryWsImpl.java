package eu.domibus.connector.client.link.ws.impl;

import eu.domibus.connector.client.process.ProcessMessageFromConnectorToClient;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DomibusConnectorClientDeliveryWsImpl implements DomibusConnectorBackendDeliveryWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientDeliveryWsImpl.class);

    @Autowired
    private ProcessMessageFromConnectorToClient processMessageFromConnectorToNational;

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(DomibusConnectorMessageType msg) {
        DomibsConnectorAcknowledgementType ackResponse = new DomibsConnectorAcknowledgementType();

        try {
            DomibusConnectorMessageResponseType responseType = processMessageFromConnectorToNational.processMessageFromConnectorToNational(msg);
            BeanUtils.copyProperties(responseType, ackResponse);
            ackResponse.setResult(true);
        } catch (Exception e) {
            LOGGER.error("Exception occured while processing message from connector", e);
            ackResponse.setResultMessage(e.getMessage());
            ackResponse.setResult(false);
        }
        return ackResponse;
    }
}

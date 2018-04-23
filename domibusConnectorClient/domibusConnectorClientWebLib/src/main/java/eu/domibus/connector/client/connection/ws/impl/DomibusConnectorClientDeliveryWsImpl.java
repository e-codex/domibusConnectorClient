package eu.domibus.connector.client.connection.ws.impl;

import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClientDelivery;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("backendDeliveryWebService")
public class DomibusConnectorClientDeliveryWsImpl implements DomibusConnectorBackendDeliveryWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientDeliveryWsImpl.class);

    private DomibusConnectorNationalBackendClientDelivery nationalBackendClientDelivery;

    @Autowired
    public void setNationalBackendClientDelivery(DomibusConnectorNationalBackendClientDelivery nationalBackendClientDelivery) {
        this.nationalBackendClientDelivery = nationalBackendClientDelivery;
    }

    @Override
    public DomibsConnectorAcknowledgementType deliverMessage(DomibusConnectorMessageType msg) {
        DomibsConnectorAcknowledgementType response = new DomibsConnectorAcknowledgementType();

        try {
            nationalBackendClientDelivery.processMessageFromConnector(msg);
            response.setResult(true);
        } catch (Exception e) {
            LOGGER.error("Exception occured while processing message from connector", e);
            response.setResultMessage(e.getMessage());
            response.setResult(false);
        }
        return response;
    }
}

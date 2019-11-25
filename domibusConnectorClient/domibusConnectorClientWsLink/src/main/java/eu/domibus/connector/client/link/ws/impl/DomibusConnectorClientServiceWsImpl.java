package eu.domibus.connector.client.link.ws.impl;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.DomibusConnectorClientMethodNotSupportedException;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public class DomibusConnectorClientServiceWsImpl implements DomibusConnectorClientService {

    private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientServiceWsImpl.class);

    @Autowired
    DomibusConnectorBackendWebService connectorWsClient;

    @Override
    public List<DomibusConnectorMessageType> requestMessagesFromConnector() throws DomibusConnectorClientException {
        try {
            DomibusConnectorMessagesType domibusConnectorMessagesType = connectorWsClient.requestMessages(new EmptyRequestType());
            return domibusConnectorMessagesType.getMessages();
        } catch (Exception e) {
            LOGGER.error("Something went wrong while requesting message from Connector!");
            throw new DomibusConnectorClientException(e);
        }
    }

    @Override
    public Optional<DomibusConnectorMessageResponseType> pushMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException {
        DomibusConnectorMessageResponseType responseType = new DomibusConnectorMessageResponseType();
        DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType = connectorWsClient.submitMessage(message);
        BeanUtils.copyProperties(domibsConnectorAcknowledgementType, responseType);
        return Optional.of(responseType);
    }

    @Override
    public void setMessageResponse(DomibusConnectorMessageResponseType messageResponseType) throws DomibusConnectorClientException {
        throw new DomibusConnectorClientMethodNotSupportedException("Cannot set message status with extra call within this impl!");
    }

}

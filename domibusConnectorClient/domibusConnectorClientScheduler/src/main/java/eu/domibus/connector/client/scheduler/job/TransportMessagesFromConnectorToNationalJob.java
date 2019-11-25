package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.DomibusConnectorClientMethodNotSupportedException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.process.ProcessMessageFromConnectorToNational;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Component
public class TransportMessagesFromConnectorToNationalJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportMessagesFromConnectorToNationalJob.class);

    @Autowired
    private DomibusConnectorClientService connectorClientService;

    @Autowired
    ProcessMessageFromConnectorToNational connectorToNational;

    public void transportMessageToNational() throws DomibusConnectorClientException {
        List<DomibusConnectorMessageType> messages = null;
        LocalDateTime startTime = LocalDateTime.now();
        LOGGER.info("TransportMessagesFromConnectorToNationalJob started");

        messages = connectorClientService.requestMessagesFromConnector();

        if (!CollectionUtils.isEmpty(messages)) {
            LOGGER.debug("{} new messages from connector to transport to national backend...", messages.size());
            messages.stream().forEach( message -> {

                DomibusConnectorMessageResponseType messageResponseType = connectorToNational.processMessageFromConnectorToNational(message);
                try {
                    connectorClientService.setMessageResponse(messageResponseType);
                } catch (DomibusConnectorClientMethodNotSupportedException e) {
                    LOGGER.error("Cannot send message response status back to connector with this transport impl!", e);
                } catch (DomibusConnectorClientException e) {
                    LOGGER.error("Sending message response status back to connector failed", e);
                }
            });
        }
        LOGGER.info("TransportMessagesFromConnectorToNationalJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
    }

}

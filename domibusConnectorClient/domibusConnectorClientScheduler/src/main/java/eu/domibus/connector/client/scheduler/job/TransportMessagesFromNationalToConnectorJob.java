package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.process.ProcessMessageFromClientToConnector;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TransportMessagesFromNationalToConnectorJob {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TransportMessagesFromNationalToConnectorJob.class);

    @Autowired
    private DomibusConnectorNationalBackendClient nationalBackendClientService;

    @Autowired
    private ProcessMessageFromClientToConnector processMessageFromNationalToConnector;

    public void submitMessageFromNationalToConnector() throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        List<DomibusConnectorMessageType> messages = null;
        LocalDateTime startTime = LocalDateTime.now();
        LOGGER.info("TransportMessagesFromNationalToConnectorJob started");

        messages = nationalBackendClientService.checkForMessagesOnNationalBackend();

        if (!CollectionUtils.isEmpty(messages)) {
            LOGGER.debug("{} new messages from national backend to submit to connector...", messages.size());
            for (DomibusConnectorMessageType message : messages) {
                DomibusConnectorMessageResponseType domibusConnectorMessageResponseType = processMessageFromNationalToConnector.processMessageFromClientToConnector(message);
                try {
                    nationalBackendClientService.setMessageResponse(domibusConnectorMessageResponseType);
                } catch (DomibusConnectorNationalBackendClientException e) {
                    LOGGER.error("Failed to send message response status back to national system! For message [{}]", message);
                }
            }
        }
        LOGGER.info("TransportMessagesFromNationalToConnectorJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
    }
}

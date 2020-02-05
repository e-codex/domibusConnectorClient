package eu.domibus.connector.client.scheduler.job;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
public class SubmitMessagesToConnectorJob {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJob.class);
    
    @Autowired
    private DomibusConnectorClientBackend clientBackend;
    
    @Autowired
    private DomibusConnectorClient connectorClient;

   
    public void checkClientBackendForNewMessagesAndSubmitThemToConnector() throws ImplementationMissingException {
        DomibusConnectorMessagesType messages = null;
        LocalDateTime startTime = LocalDateTime.now();
        LOGGER.info("SubmitMessagesToConnectorJob started");

        messages = clientBackend.checkClientForNewMessagesToSubmit();

        if (messages!=null && !CollectionUtils.isEmpty(messages.getMessages())) {
            LOGGER.debug("{} new messages from client backend to submit to connector...", messages.getMessages().size());
            for (DomibusConnectorMessageType message : messages.getMessages()) {
            	try {
					connectorClient.submitNewMessageToConnector(message);
				} catch (DomibusConnectorClientException e) {
					LOGGER.error("Exception occured trying to submit message to connector: ", e);
					e.printStackTrace();
				}
            }
        }
        LOGGER.info("SubmitMessagesToConnectorJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
    }
}

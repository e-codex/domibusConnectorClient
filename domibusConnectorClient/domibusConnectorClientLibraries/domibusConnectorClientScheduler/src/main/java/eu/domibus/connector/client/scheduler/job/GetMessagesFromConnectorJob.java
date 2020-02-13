package eu.domibus.connector.client.scheduler.job;

import java.time.Duration;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;


@Component
public class GetMessagesFromConnectorJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMessagesFromConnectorJob.class);

    @Autowired
    private DomibusConnectorClientBackend clientBackend;
    
    @Autowired
    private DomibusConnectorClient connectorClient;
    
    
    public void requestNewMessagesFromConnectorAndDeliverThemToClientBackend() throws DomibusConnectorClientException {
        DomibusConnectorMessagesType messages = null;
        LocalDateTime startTime = LocalDateTime.now();
        LOGGER.info("GetMessagesFromConnectorJob started");

        messages = connectorClient.requestNewMessagesFromConnector();

        if (messages!=null && !CollectionUtils.isEmpty(messages.getMessages())) {
            LOGGER.debug("{} new messages from connector to store...", messages.getMessages().size());
            messages.getMessages().stream().forEach( message -> {

                clientBackend.deliverNewMessageToClientBackend(message);
                
            });
        }else {
        	LOGGER.debug("No new messages from connector to store received.");
        }
        LOGGER.info("GetMessagesFromConnectorJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
    }

}

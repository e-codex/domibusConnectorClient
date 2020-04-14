package eu.domibus.connector.client.scheduler.job;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;


@Component
@Validated
@Valid
public class GetMessagesFromConnectorJobService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetMessagesFromConnectorJobService.class);

    @Autowired
    private DomibusConnectorClientBackend clientBackend;
    
    @Autowired
    private DomibusConnectorClient connectorClient;
    
    
    public void requestNewMessagesFromConnectorAndDeliverThemToClientBackend() throws DomibusConnectorClientException {
        DomibusConnectorMessagesType messages = null;
        LocalDateTime startTime = LocalDateTime.now();
        LOGGER.debug("GetMessagesFromConnectorJob started");

        messages = connectorClient.requestNewMessagesFromConnector();

        if (messages!=null && !CollectionUtils.isEmpty(messages.getMessages())) {
            LOGGER.info("{} new messages from connector to store...", messages.getMessages().size());
            messages.getMessages().stream().forEach( message -> {

                try {
					clientBackend.deliverNewMessageToClientBackend(message);
				} catch (DomibusConnectorClientBackendException e1) {
					LOGGER.error("Exception occured delivering new message to the client backend", e1);
					
				}
                
//                try {
//                	clientBackend.triggerConfirmationForMessage(message, DomibusConnectorConfirmationType.DELIVERY, null);
//				} catch (DomibusConnectorClientBackendException e) {
//					LOGGER.error("Exception occured triggering the confirmation for message at the client backend", e);
//				}
                
            });
        }else {
        	LOGGER.debug("No new messages from connector to store received.");
        }
        LOGGER.debug("GetMessagesFromConnectorJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
    }

}

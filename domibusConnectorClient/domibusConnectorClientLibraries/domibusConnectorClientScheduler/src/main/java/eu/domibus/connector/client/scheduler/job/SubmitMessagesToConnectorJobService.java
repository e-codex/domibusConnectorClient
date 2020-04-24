package eu.domibus.connector.client.scheduler.job;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.ImplementationMissingException;

@Component
@Validated
@Valid
public class SubmitMessagesToConnectorJobService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJobService.class);
    
    @Autowired
    private DomibusConnectorClientBackend clientBackend;
    
   
    public void checkClientBackendForNewMessagesAndSubmitThemToConnector() throws ImplementationMissingException {
        LocalDateTime startTime = LocalDateTime.now();
        LOGGER.debug("SubmitMessagesToConnectorJob started");

        try {
			clientBackend.checkClientForNewMessagesToSubmit();
		} catch (DomibusConnectorClientBackendException e1) {
			LOGGER.error("Exception occured at clientBackend.checkClientForNewMessagesToSubmit()",e1);
			e1.printStackTrace();
		}
        
        LOGGER.debug("SubmitMessagesToConnectorJob finished after [{}]", Duration.between(startTime, LocalDateTime.now()));
    }
}

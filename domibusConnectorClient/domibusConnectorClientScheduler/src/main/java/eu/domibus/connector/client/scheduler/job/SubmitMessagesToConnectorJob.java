package eu.domibus.connector.client.scheduler.job;

import java.util.List;

import eu.domibus.connector.client.transport.TransportMessagesFromNationalToConnectorService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerConfiguration;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Configuration("submitMessagesToConnectorJobConfiguration")
//@ConditionalOnProperty(name = "connector.use.evidences.timeout", havingValue="true")
public class SubmitMessagesToConnectorJob implements Job {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJob.class);

	@Autowired
	TransportMessagesFromNationalToConnectorService transportMessagesFromNationalToConnectorService;

	@Value("${connector.client.timer.check.outgoing.messages.ms}")
    private Long repeatInterval;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			transportMessagesFromNationalToConnectorService.submitMessageFromNationalToConnector();
		} catch (DomibusConnectorNationalBackendClientException | ImplementationMissingException e) {
			LOGGER.error("Exception occured while loading transporting messages from national system to connector. Throwing Exception again:", e);
			throw new JobExecutionException(e);
		}
	}

	
	@Bean(name = "submitMessagesToConnectorJob")
	public JobDetailFactoryBean submitMessagesToConnectorJob() {
		return DomibusConnectorClientSchedulerConfiguration.createJobDetail(this.getClass());
	}
	 
	@Bean(name = "submitMessagesToConnectorTrigger")
	public SimpleTriggerFactoryBean submitMessagesToConnectorTrigger(@Qualifier("submitMessagesToConnectorJob") JobDetailFactoryBean jdfb ) {
		return DomibusConnectorClientSchedulerConfiguration.createTrigger(jdfb.getObject(), repeatInterval, 0L);
	}

}

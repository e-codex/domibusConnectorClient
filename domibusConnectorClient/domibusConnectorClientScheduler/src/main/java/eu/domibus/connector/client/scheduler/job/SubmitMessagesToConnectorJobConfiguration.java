package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.client.transport.TransportMessagesFromNationalToConnectorService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerConfiguration;

@EnableConfigurationProperties
@Configuration("submitMessagesToConnectorJobConfiguration")
@ConditionalOnProperty(value = SubmitMessagesToConnectorJobConfigurationProperties.PREFIX + "enabled", havingValue = "true")
public class SubmitMessagesToConnectorJobConfiguration implements Job {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJobConfiguration.class);

	@Autowired
	TransportMessagesFromNationalToConnectorService transportMessagesFromNationalToConnectorService;

//	@Value("${connector.client.timer.check.outgoing.messages.ms}")
//    private Long repeatInterval;

	@Autowired
	SubmitMessagesToConnectorJobConfigurationProperties properties;

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
		return DomibusConnectorClientSchedulerConfiguration.createTrigger(jdfb.getObject(),
				properties.getRepeatInterval().getMilliseconds(), 0L);
	}

}

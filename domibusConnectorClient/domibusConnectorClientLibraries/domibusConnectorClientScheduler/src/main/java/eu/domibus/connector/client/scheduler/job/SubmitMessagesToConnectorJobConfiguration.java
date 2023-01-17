package eu.domibus.connector.client.scheduler.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;

@EnableConfigurationProperties(SubmitMessagesToConnectorJobConfigurationProperties.class)
@Configuration("submitMessagesToConnectorJobConfiguration")
@ConditionalOnProperty(value = SubmitMessagesToConnectorJobConfigurationProperties.PREFIX + ".enabled", havingValue = "true")
public class SubmitMessagesToConnectorJobConfiguration {
	
	static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJobConfiguration.class);
	@Bean
	@Qualifier("submitMessagesToConnectorJob")
	public JobDetailFactoryBean submitMessagesToConnectorJob() {
		return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(SubmitMessagesToConnectorJob.class);
	}

	@Bean(name = "submitMessagesToConnectorTrigger")
	public SimpleTriggerFactoryBean submitMessagesToConnectorTrigger(@Qualifier("submitMessagesToConnectorJob") JobDetailFactoryBean jdfb,
																	 SubmitMessagesToConnectorJobConfigurationProperties properties) {
		LOGGER.debug("create SimpleTriggerFactoryBean: submitMessagesToConnectorTrigger");
		return DomibusConnectorClientSchedulerAutoConfiguration.createTrigger(jdfb.getObject(),
				properties.getRepeatInterval().getMilliseconds(), 0L);
	}

	@DisallowConcurrentExecution
	public static class SubmitMessagesToConnectorJob implements Job {

		@Autowired
		private SubmitMessagesToConnectorJobService submitMessagesToConnectorJob;

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			LOGGER.debug("Running SubmitMessagesToConnectorJob");
			submitMessagesToConnectorJob.checkClientBackendForNewMessagesAndSubmitThemToConnector();
		}
	}

}

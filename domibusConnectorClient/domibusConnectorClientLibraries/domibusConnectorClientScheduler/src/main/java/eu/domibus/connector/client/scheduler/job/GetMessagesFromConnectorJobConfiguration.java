package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@EnableConfigurationProperties(GetMessagesFromConnectorJobConfigurationProperties.class)
@ConditionalOnProperty(prefix = GetMessagesFromConnectorJobConfigurationProperties.PREFIX, value = "enabled", havingValue = "true")
@Configuration("getMessagesFromConnectorJobConfiguration")
public class GetMessagesFromConnectorJobConfiguration {

	private static final Logger LOGGER = LogManager.getLogger(GetMessagesFromConnectorJobConfiguration.class);

	@Bean
	@Qualifier("getMessagesFromConnectorJob")
	public JobDetailFactoryBean getMessagesFromConnectorJob() {
		return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(GetMessagesFromConnectorJob.class);
	}

	@Bean(name = "getMessagesFromConnectorTrigger")
	public SimpleTriggerFactoryBean getMessagesFromConnectorTrigger(@Qualifier("getMessagesFromConnectorJob") JobDetailFactoryBean jdfb,
																	GetMessagesFromConnectorJobConfigurationProperties properties) {
		if (!properties.isEnabled())
			return null;
		return DomibusConnectorClientSchedulerAutoConfiguration.createTrigger(jdfb.getObject(),
				properties.getRepeatInterval().getMilliseconds(),
				properties.getRepeatInterval().getMilliseconds()/2);
	}

	@Bean
	public GetMessagesFromConnectorJobService getMessagesFromConnectorJobService(DomibusConnectorClientBackend clientBackend,
																				 DomibusConnectorClient connectorClient,
																				 GetMessagesFromConnectorJobConfigurationProperties properties) {
		return new GetMessagesFromConnectorJobService(clientBackend, connectorClient, properties);
	}


	@DisallowConcurrentExecution
	public static class GetMessagesFromConnectorJob implements Job {

		@Autowired
		private GetMessagesFromConnectorJobService getMessagesFromConnectorJob;

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			LOGGER.debug("Running GetMessagesFromConnectorJob");
			try {
				getMessagesFromConnectorJob.requestNewMessagesFromConnectorAndDeliverThemToClientBackend();
			} catch (DomibusConnectorClientException e) {
				throw new JobExecutionException(e);
			}
		}
	}

}

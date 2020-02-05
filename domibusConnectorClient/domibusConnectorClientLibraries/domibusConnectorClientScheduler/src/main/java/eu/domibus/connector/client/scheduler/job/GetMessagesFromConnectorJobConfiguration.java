package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerAutoConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

@EnableConfigurationProperties
@ConditionalOnProperty(prefix = GetMessagesFromConnectorJobConfigurationProperties.PREFIX, value = "enabled", havingValue = "true")
@Configuration("getMessagesFromConnectorJobConfiguration")
public class GetMessagesFromConnectorJobConfiguration implements Job {

	private static final Logger LOGGER = LogManager.getLogger(GetMessagesFromConnectorJobConfiguration.class);

	@Autowired
	private GetMessagesFromConnectorJob transportMessagesFromConnectorToNationalService;

	@Autowired
	GetMessagesFromConnectorJobConfigurationProperties properties;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.debug("Running GetMessagesFromConnectorJob");
        try {
            transportMessagesFromConnectorToNationalService.requestNewMessagesFromConnectorAndDeliverThemToClientBackend();
        } catch (DomibusConnectorClientException e) {
            throw new JobExecutionException(e);
        }
	}

	@Bean(name = "getMessagesFromConnectorJob")
	public JobDetailFactoryBean getMessagesFromConnectorJob() {
		return DomibusConnectorClientSchedulerAutoConfiguration.createJobDetail(this.getClass());
	}

	@Bean(name = "getMessagesFromConnectorTrigger")
	public SimpleTriggerFactoryBean getMessagesFromConnectorTrigger(@Qualifier("getMessagesFromConnectorJob") JobDetailFactoryBean jdfb ) {
		if (!properties.isEnabled())
			return null;
		return DomibusConnectorClientSchedulerAutoConfiguration.createTrigger(jdfb.getObject(),
				properties.getRepeatInterval().getMilliseconds(),
				properties.getRepeatInterval().getMilliseconds()/2);
	}

}

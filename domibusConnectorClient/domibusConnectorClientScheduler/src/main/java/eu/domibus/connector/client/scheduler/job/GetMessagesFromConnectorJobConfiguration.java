package eu.domibus.connector.client.scheduler.job;

import java.util.List;

import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClientDelivery;
import eu.domibus.connector.client.transport.TransportMessagesFromConnectorToNationalService;
import eu.domibus.connector.lib.spring.DomibusConnectorDuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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

@EnableConfigurationProperties
@ConditionalOnProperty(value = GetMessagesFromConnectorJobConfigurationProperties.PREFIX + ".enabled", havingValue = "true")
@Configuration("getMessagesFromConnectorJobConfiguration")
public class GetMessagesFromConnectorJobConfiguration implements Job {

	private static final Logger LOGGER = LogManager.getLogger(GetMessagesFromConnectorJobConfiguration.class);

	@Autowired
	private TransportMessagesFromConnectorToNationalService transportMessagesFromConnectorToNationalService;

	@Autowired
	GetMessagesFromConnectorJobConfigurationProperties properties;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		LOGGER.debug("Running GetMessagesFromConnectorJob");
        try {
            transportMessagesFromConnectorToNationalService.transportMessageToNational();
        } catch (DomibusConnectorClientException e) {
            throw new JobExecutionException(e);
        }
	}

	@Bean(name = "getMessagesFromConnectorJob")
	public JobDetailFactoryBean getMessagesFromConnectorJob() {
		return DomibusConnectorClientSchedulerConfiguration.createJobDetail(this.getClass());
	}

	@Bean(name = "getMessagesFromConnectorTrigger")
	public SimpleTriggerFactoryBean getMessagesFromConnectorTrigger(@Qualifier("getMessagesFromConnectorJob") JobDetailFactoryBean jdfb ) {
		if (!properties.isEnabled())
			return null;
		return DomibusConnectorClientSchedulerConfiguration.createTrigger(jdfb.getObject(),
				properties.getRepeatInterval().getMilliseconds(),
				properties.getRepeatInterval().getMilliseconds()/2);
	}

}

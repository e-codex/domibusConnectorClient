package eu.domibus.connector.client.scheduler.job;

import java.util.List;

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

@Configuration("getMessagesFromConnectorJobConfiguration")
public class GetMessagesFromConnectorJob implements Job {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GetMessagesFromConnectorJob.class);

	@Autowired
	private DomibusConnectorClientService clientService;

	@Autowired
	private DomibusConnectorNationalBackendClient nationalBackendClient;

	@Value("${connector.client.timer.check.incoming.messages.ms}")
	private Long repeatInterval;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<DomibusConnectorMessageType> messages = null;

		try {
			messages = clientService.requestMessagesFromConnector();
		} catch (DomibusConnectorClientException e2) {
			throw new JobExecutionException(e2);
		}

		if(!CollectionUtils.isEmpty(messages)) {
			LOGGER.info("{} new messages from connector to transport to national backend...", messages.size());
			try {
				nationalBackendClient.processMessagesFromConnector(messages);
			} catch (DomibusConnectorNationalBackendClientException | ImplementationMissingException e) {
				throw new JobExecutionException(e);
			}
		}
	}

	@Bean(name = "getMessagesFromConnectorJob")
	public JobDetailFactoryBean getMessagesFromConnectorJob() {
		return DomibusConnectorClientSchedulerConfiguration.createJobDetail(this.getClass());
	}

	@Bean(name = "getMessagesFromConnectorTrigger")
	public SimpleTriggerFactoryBean getMessagesFromConnectorTrigger(@Qualifier("getMessagesFromConnectorJob") JobDetailFactoryBean jdfb ) {
		return DomibusConnectorClientSchedulerConfiguration.createTrigger(jdfb.getObject(), repeatInterval, repeatInterval/2);
	}

}

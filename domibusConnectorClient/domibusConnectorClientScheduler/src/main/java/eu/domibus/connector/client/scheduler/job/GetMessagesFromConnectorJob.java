package eu.domibus.connector.client.scheduler.job;

import java.util.List;

import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClientDelivery;
import eu.domibus.connector.client.transport.TransportMessagesFromConnectorToNationalService;
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
	private TransportMessagesFromConnectorToNationalService transportMessagesFromConnectorToNationalService;

	@Value("${connector.client.timer.check.incoming.messages.ms}")
	private Long repeatInterval;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
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
		return DomibusConnectorClientSchedulerConfiguration.createTrigger(jdfb.getObject(), repeatInterval, repeatInterval/2);
	}

}

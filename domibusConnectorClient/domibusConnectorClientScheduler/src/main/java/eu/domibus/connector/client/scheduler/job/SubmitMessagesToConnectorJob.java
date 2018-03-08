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
import eu.domibus.connector.client.nbc.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.scheduler.configuration.DomibusConnectorClientSchedulerConfiguration;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Configuration("submitMessagesToConnectorJobConfiguration")
//@ConditionalOnProperty(name = "connector.use.evidences.timeout", havingValue="true")
public class SubmitMessagesToConnectorJob implements Job {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJob.class);

	@Autowired
	private DomibusConnectorClientService clientService;
	
	@Autowired
	private DomibusConnectorNationalBackendClient nationalBackendClient;
	
	@Value("${connector.client.submit.messages.period.ms}")
    private long repeatInterval;
	

    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
    	List<DomibusConnectorMessageType> messages = null;
		try {
			messages = nationalBackendClient.checkForMessagesOnNationalBackend();
		} catch (DomibusConnectorNationalBackendClientException | ImplementationMissingException e1) {
			throw new JobExecutionException(e1);
		}
    	if(!CollectionUtils.isEmpty(messages)) {
    		LOGGER.info("{} new messages from national backend to submit to connector...", messages.size());
    		for(DomibusConnectorMessageType message: messages) {
    			try {
					clientService.submitMessageToConnector(message);
				} catch (DomibusConnectorClientException e) {
					LOGGER.error("Exception submitting message to connector: ", e);
				}
    		}
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

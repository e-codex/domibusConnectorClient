package eu.domibus.connector.v35client.job;

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

import eu.domibus.connector.v35client.PushMessagesToControllerFrom35Client;
import eu.domibus.connector.v35client.configuration.DomibusConnectorClient35LibraryConfiguration;

@Configuration("submitMessagesToConnectorJobConfiguration")
//@ConditionalOnProperty(name = "connector.use.evidences.timeout", havingValue="true")
public class SubmitMessagesToConnectorJob implements Job {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SubmitMessagesToConnectorJob.class);

	@Autowired
	private PushMessagesToControllerFrom35Client clientService;
	
	@Value("${connector.client.timer.check.outgoing.messages.ms}")
    private Long repeatInterval;
	

    @Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
    	
    	clientService.transportMessagesToController();
    	
	}
	
	@Bean(name = "submitMessagesToConnectorJob")
	public JobDetailFactoryBean submitMessagesToConnectorJob() {
		return DomibusConnectorClient35LibraryConfiguration.createJobDetail(this.getClass());
	}
	 
	@Bean(name = "submitMessagesToConnectorTrigger")
	public SimpleTriggerFactoryBean submitMessagesToConnectorTrigger(@Qualifier("submitMessagesToConnectorJob") JobDetailFactoryBean jdfb ) {
		return DomibusConnectorClient35LibraryConfiguration.createTrigger(jdfb.getObject(), repeatInterval, 0L);
	}

}

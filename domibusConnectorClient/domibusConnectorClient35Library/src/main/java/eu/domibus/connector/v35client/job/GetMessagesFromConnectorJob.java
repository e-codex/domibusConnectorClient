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

import eu.domibus.connector.v35client.PullMessagesFromControllerTo35Client;
import eu.domibus.connector.v35client.configuration.DomibusConnectorClient35LibraryConfiguration;

@Configuration("getMessagesFromConnectorJobConfiguration")
public class GetMessagesFromConnectorJob implements Job {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GetMessagesFromConnectorJob.class);

	@Autowired
	private PullMessagesFromControllerTo35Client clientService;

	@Value("${connector.client.timer.check.incoming.messages.ms}")
	private Long repeatInterval;


	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
			clientService.pullMessagesFromController();
		
		
	}

	@Bean(name = "getMessagesFromConnectorJob")
	public JobDetailFactoryBean getMessagesFromConnectorJob() {
		return DomibusConnectorClient35LibraryConfiguration.createJobDetail(this.getClass());
	}

	@Bean(name = "getMessagesFromConnectorTrigger")
	public SimpleTriggerFactoryBean getMessagesFromConnectorTrigger(@Qualifier("getMessagesFromConnectorJob") JobDetailFactoryBean jdfb ) {
		return DomibusConnectorClient35LibraryConfiguration.createTrigger(jdfb.getObject(), repeatInterval, repeatInterval/2);
	}

}

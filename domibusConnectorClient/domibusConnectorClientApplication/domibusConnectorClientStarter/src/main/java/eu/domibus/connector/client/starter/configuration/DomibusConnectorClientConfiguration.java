
package eu.domibus.connector.client.starter.configuration;

import eu.domibus.connector.client.scheduler.job.GetMessagesFromConnectorJob;
import eu.domibus.connector.client.scheduler.job.SubmitMessagesToConnectorJob;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 *
 * 
 */
@SpringBootApplication(scanBasePackages="eu.domibus.connector")
@PropertySource("file:${connector-client.properties}")
@ImportResource("classpath:META-INF/cxf/cxf.xml")
public class DomibusConnectorClientConfiguration {

//    @Bean
//    DomibusConnectorClientService domibusConnectorClientServiceImpl() {
//        return new DomibusConnectorClientServiceImpl();
//    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		return propertySourcesPlaceholderConfigurer;
    }

//    @Bean
//    DomibusConnectorClientSubmissionController domibusConnectorClientSubmissionController() {
//        return new DomibusConnectorClientSubmissionController();
//    }

    @Bean
    GetMessagesFromConnectorJob transportMessagesFromConnectorToNationalService() {
        return new GetMessagesFromConnectorJob();
    }

    @Bean
    SubmitMessagesToConnectorJob transportMessagesFromNationalToConnectorService() {
        return new SubmitMessagesToConnectorJob();
    }
    
}


package eu.domibus.connector.client.runnable.configuration;

import eu.domibus.connector.client.scheduler.job.TransportMessagesFromConnectorToNationalJob;
import eu.domibus.connector.client.scheduler.job.TransportMessagesFromNationalToConnectorJob;
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
    TransportMessagesFromConnectorToNationalJob transportMessagesFromConnectorToNationalService() {
        return new TransportMessagesFromConnectorToNationalJob();
    }

    @Bean
    TransportMessagesFromNationalToConnectorJob transportMessagesFromNationalToConnectorService() {
        return new TransportMessagesFromNationalToConnectorJob();
    }
    
}

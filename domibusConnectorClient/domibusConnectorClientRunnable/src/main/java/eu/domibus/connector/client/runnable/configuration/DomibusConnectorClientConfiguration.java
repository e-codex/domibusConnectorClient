
package eu.domibus.connector.client.runnable.configuration;

import eu.domibus.connector.client.controller.DomibusConnectorClientSubmissionController;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.client.service.DomibusConnectorClientServiceImpl;
import eu.domibus.connector.client.transport.TransportMessagesFromConnectorToNationalService;
import eu.domibus.connector.client.transport.TransportMessagesFromNationalToConnectorService;
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

    @Bean
    DomibusConnectorClientService domibusConnectorClientServiceImpl() {
        return new DomibusConnectorClientServiceImpl();
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    DomibusConnectorClientSubmissionController domibusConnectorClientSubmissionController() {
        return new DomibusConnectorClientSubmissionController();
    }

    @Bean
    TransportMessagesFromConnectorToNationalService transportMessagesFromConnectorToNationalService() {
        return new TransportMessagesFromConnectorToNationalService();
    }

    @Bean
    TransportMessagesFromNationalToConnectorService transportMessagesFromNationalToConnectorService() {
        return new TransportMessagesFromNationalToConnectorService();
    }
    
}

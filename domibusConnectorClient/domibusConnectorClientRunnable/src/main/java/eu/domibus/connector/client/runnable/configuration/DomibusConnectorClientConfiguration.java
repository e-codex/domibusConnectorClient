
package eu.domibus.connector.client.runnable.configuration;

import eu.domibus.connector.client.transport.TransportMessagesFromConnectorToNationalService;
import eu.domibus.connector.client.transport.TransportMessagesFromNationalToConnectorService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 *
 * 
 */
@SpringBootApplication(scanBasePackages="eu.domibus.connector")
@PropertySource("file:${connector-client.properties}")
public class DomibusConnectorClientConfiguration {

    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		return propertySourcesPlaceholderConfigurer;
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

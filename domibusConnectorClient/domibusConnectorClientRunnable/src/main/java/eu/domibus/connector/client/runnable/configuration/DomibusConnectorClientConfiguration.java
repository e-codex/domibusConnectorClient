
package eu.domibus.connector.client.runnable.configuration;

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
   
    //TODO: set correct scan folder
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		return propertySourcesPlaceholderConfigurer;
    }
    
}


package eu.domibus.connector.client.runnable.configuration;

import java.util.PropertyResourceBundle;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@SpringBootApplication(scanBasePackages="eu.domibus.connector")
@EnableConfigurationProperties(ConnectorClientProperties.class)
@EnableScheduling
public class DomibusConnectorClientConfiguration {
   
    //TODO: set correct scan folder
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    	
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        Resource location = new FileSystemResource(System.getProperty(ConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME));
		propertySourcesPlaceholderConfigurer.setLocation(location );
		return propertySourcesPlaceholderConfigurer;
    }
    
}

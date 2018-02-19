
package eu.domibus.connector.runnable;

import eu.domibus.connector.runnable.util.StandaloneClientProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@SpringBootApplication(scanBasePackages="eu.domibus.connector")
@EnableConfigurationProperties(StandaloneClientProperties.class)
public class DomibusClientGuiConfiguration {
   
    //TODO: set correct scan folder
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
}

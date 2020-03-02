package eu.domibus.connector.client.starter;

import java.io.File;
import java.util.Properties;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client")
@EnableScheduling
@EnableTransactionManagement
@PropertySource({"classpath:/default-connector-client.properties"})
public class DomibusConnectorClient 
//extends SpringBootServletInitializer 
{

	private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClient.class);
	
	public static final String CONNECTOR_CLIENT_CONFIG_FILE = "connector.client.config.file";
	
	public static final String SPRING_CONFIG_LOCATION = "spring.config.location";
	public static final String SPRING_CONFIG_NAME = "spring.config.name";
	
	public static void main(String[] args) {
		runSpringApplication(args);

	}
	
	public static ConfigurableApplicationContext runSpringApplication(String[] args) {
    	SpringApplicationBuilder builder = new SpringApplicationBuilder();
        builder = configureApplicationContext(builder);
    	SpringApplication springApplication = builder.build();
        ConfigurableApplicationContext appContext = springApplication.run(args);
        return appContext;
    }
	
	public static SpringApplicationBuilder configureApplicationContext(SpringApplicationBuilder application) {
        String connectorConfigFile = getConnectorConfigFile();
        Properties springProperties = new Properties();
        if (connectorConfigFile != null) {

            int lastIndex = connectorConfigFile.contains(File.separator)?connectorConfigFile.lastIndexOf(File.separatorChar):connectorConfigFile.lastIndexOf("/");
            lastIndex++;
            String connectorConfigLocation = connectorConfigFile.substring(0, lastIndex);
            String configName = connectorConfigFile.substring(lastIndex);

            LOGGER.info(String.format("Setting:\n%s=%s\n%s=%s\n%s=%s\n%s=%s",
                    SPRING_CONFIG_LOCATION, connectorConfigLocation,
                    SPRING_CONFIG_NAME, configName));

            springProperties.setProperty(SPRING_CONFIG_LOCATION, connectorConfigLocation);
            springProperties.setProperty(SPRING_CONFIG_NAME, configName);

        }else {
            LOGGER.warn("SystemProperty \"{}\" not given or not resolveable! Startup using default spring external configuration!", CONNECTOR_CLIENT_CONFIG_FILE);
        }
        application.properties(springProperties); //pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
        return application.sources(DomibusConnectorClient.class);
    }
	
	public static @Nullable
    String getConnectorConfigFile() {
        String connectorConfigFile = System.getProperty(CONNECTOR_CLIENT_CONFIG_FILE);
        if (connectorConfigFile != null) {
            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
            return connectorConfigFile;
        }
        return null;
    }
	


}

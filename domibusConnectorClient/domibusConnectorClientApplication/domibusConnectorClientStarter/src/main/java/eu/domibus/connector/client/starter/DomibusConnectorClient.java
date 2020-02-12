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
	



	 
//	public static ConfigurableApplicationContext runSpringApplication(String[] args) {
//		String connectorProperties = null;
//		boolean startWithGUI = false;
//		boolean webEnabled = false;
//
//		if (ArrayUtils.contains(args, "-properties")) {
//			int i = ArrayUtils.indexOf(args, "-properties");
//			i = i + 1;
//			if (args.length > i) {
//				String propertiesFileLocationPath = args[i];
//				File connectorPropertiesFile = new File(propertiesFileLocationPath);
//				if (connectorPropertiesFile.isFile()) {
//					//ConnectorClientProperties.CONNECTOR_PROPERTIES_FILE = connectorPropertiesFile;
//					connectorProperties = propertiesFileLocationPath;
//				}
//			}
//		}
//
//
//		if (!StringUtils.hasText(connectorProperties)) {
//			connectorProperties = System.getProperty(DomibusConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME);
//		}
//		if (!StringUtils.hasText(connectorProperties)) {
//			connectorProperties = System.getenv(DomibusConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME);
//		}
//		if (!StringUtils.hasText(connectorProperties)) {
//			connectorProperties = DomibusConnectorClientProperties.CONNECTOR_PROPERTIES_FILE_PATH;
//		}
//		if (StringUtils.hasText(connectorProperties)) {
//			System.setProperty(DomibusConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME, connectorProperties);
//		}
//		try {
//			DomibusConnectorClientProperties.loadConnectorProperties();
//		} catch (Exception e) {
//
//		}
//
//		if (DomibusConnectorClientProperties.CONNECTOR_PROPERTIES_FILE.exists()) {
//			try {
//				DomibusConnectorClientProperties.loadConnectorProperties();
//			} catch (Exception e) {
//				throw new RuntimeException("Connector Client Properties could not be loaded!", e);
//			}
//		}
//
//		String loggingProperties = System.getProperty("logging.properties");
//		if (!StringUtils.hasText(loggingProperties)) {
//			loggingProperties = System.getenv("logging.properties");
//		}
//
//
//		if (!StringUtils.hasText(loggingProperties)) {
//			loggingProperties = DomibusConnectorClientProperties.LOG4J_CONFIG_FILE_PATH;
//
//		}
//		File log4jProperties = new File(loggingProperties);
//
//		if(!log4jProperties.exists()) {
//			loggingProperties= DomibusConnectorClientProperties.LOG4J_CONFIG_FILE_PATH;
//		}
//
//		System.setProperty("log4j.configurationFile", loggingProperties);
//		Configurator.initialize("Properties_Config", loggingProperties);
//		LOGGER.debug("Logging set with properties at {}",loggingProperties);
//
//		ConfigurableApplicationContext context = null;
//
//		System.setProperty("crypto.policy", "unlimited");
//
//		if (DomibusConnectorClientProperties.proxyEnabled) {
//			System.setProperty("http.proxySet", Boolean.toString(DomibusConnectorClientProperties.proxyEnabled));
//			System.setProperty("http.proxyHost", DomibusConnectorClientProperties.proxyHost != null ? DomibusConnectorClientProperties.proxyHost : "");
//			System.setProperty("http.proxyPort", DomibusConnectorClientProperties.proxyPort != null ? DomibusConnectorClientProperties.proxyPort : "");
//
//			System.setProperty("https.proxySet", Boolean.toString(DomibusConnectorClientProperties.proxyEnabled));
//			System.setProperty("https.proxyHost", DomibusConnectorClientProperties.proxyHost);
//			System.setProperty("https.proxyPort", DomibusConnectorClientProperties.proxyPort);
//		}
//		try {
//			UIManager.setLookAndFeel(
//					UIManager.getSystemLookAndFeelClassName());
//			for (Window window : JFrame.getWindows()) {
//				SwingUtilities.updateComponentTreeUI(window);
//			}
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
//				| UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
//
//		SpringApplicationBuilder builder = new SpringApplicationBuilder();
//
//		SpringApplication springApp;
//
//		if (startWithGUI) {
//			builder = builder
//					.profiles("swing-gui")
//					.headless(false);
//		}
//		if (webEnabled) {
//			builder = builder
//					.web(true)
//					.profiles(new String[]{"push", "wslink"});
//		} else {
//			builder = builder.web(false);
//		}
//
//		builder = builder.sources(DomibusConnectorClientConfiguration.class);
//
//		springApp = builder.build();
//		context = springApp.run(args);
//		context.registerShutdownHook();
//		return context;
//
//	}
}

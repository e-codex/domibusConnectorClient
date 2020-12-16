package eu.domibus.connector.client.war;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.SystemPropertyUtils;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client")
@EnableScheduling
@EnableTransactionManagement
@PropertySource({"classpath:/default-connector-client.properties"})
public class DomibusConnectorClientWAR extends SpringBootServletInitializer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientWAR.class);
	
	public static final String SPRING_CONFIG_LOCATION_PROPERTY_NAME = "spring.config.location";
    public static final String SPRING_CONFIG_NAME_PROPERTY_NAME = "spring.config.name";
    
    public static final String CONNECTOR_CLIENT_CONFIG_FILE_PROPERTY_NAME = "connector-client.config.file";
    public static final String CONNECTOR_CLIENT_CONFIG_LOCATION_PROPERTY_NAME = "connector-client.config.location";
    
    public static final String DEFAULT_SPRING_CONFIG_NAME = "connector";
    public static final String DEFAULT_SPRING_CONFIG_LOCATION = "classpath:/config/,file:./conf/,file:./conf/connector/,file:./config/,file:./config/connector/";

    String springConfigLocation = DEFAULT_SPRING_CONFIG_LOCATION;
    String springConfigName = DEFAULT_SPRING_CONFIG_NAME;
    
    Properties springApplicationProperties = new Properties();
	
	 private ServletContext servletContext;

	 public static void main(String[] args) {
	        runSpringApplication(args);
	    }

	    public static ConfigurableApplicationContext runSpringApplication(String[] args) {
	    	DomibusConnectorClientWAR starter = new DomibusConnectorClientWAR();
	        return starter.run(args);
	    }

	    private ConfigurableApplicationContext run(String[] args) {
	        SpringApplicationBuilder builder = new SpringApplicationBuilder();
	        builder = configureApplicationContext(builder);
	        SpringApplication springApplication = builder.build();
	        ConfigurableApplicationContext appContext = springApplication.run(args);
	        return appContext;
	    }


	    public static Properties loadConnectorConfigProperties(String connectorConfigFile) {
	        Properties p = new Properties();
	        if (connectorConfigFile != null) {
	            Path connectorConfigFilePath = Paths.get(connectorConfigFile);
	            if (!Files.exists(connectorConfigFilePath)) {
	                String errorString = String.format("Cannot start because the via System Property [%s] provided config file [%s] mapped to path [%s] does not exist!", CONNECTOR_CLIENT_CONFIG_FILE_PROPERTY_NAME, connectorConfigFile, connectorConfigFilePath);
	                LOGGER.error(errorString);
	                throw new RuntimeException(errorString);
	            }
	            try {
	                p.load(new FileInputStream(connectorConfigFilePath.toFile()));
	                return p;
	            } catch (IOException e) {
	                throw new RuntimeException(String.format("Cannot load properties from file [%s], is it a valid and readable properties file?", connectorConfigFilePath), e);
	            }
	        }
	        return p;
	    }

	    public static @Nullable
	    String getConnectorConfigFilePropertyName() {
	        String connectorConfigFile = System.getProperty(CONNECTOR_CLIENT_CONFIG_FILE_PROPERTY_NAME);
	        Properties springProperties = new Properties();
	        if (connectorConfigFile != null) {
	            connectorConfigFile = SystemPropertyUtils.resolvePlaceholders(connectorConfigFile);
	            return connectorConfigFile;
	        }
	        return null;
	    }


	    public SpringApplicationBuilder configureApplicationContext(SpringApplicationBuilder application) {
	        String connectorConfigFile = getConnectorConfigFilePropertyName();
	        if (connectorConfigFile != null) {

	            int lastIndex = connectorConfigFile.contains(File.separator) ? connectorConfigFile.lastIndexOf(File.separatorChar) : connectorConfigFile.lastIndexOf("/");
	            lastIndex++;
	            String connectorConfigLocation = connectorConfigFile.substring(0, lastIndex);
	            String configName = connectorConfigFile.substring(lastIndex);
	            if(configName.contains("."))
	            	configName = configName.substring(0, configName.lastIndexOf("."));

	            LOGGER.info(String.format("Setting:\n%s=%s\n%s=%s",
	                    SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigLocation,
	                    SPRING_CONFIG_NAME_PROPERTY_NAME, configName));

	            springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, connectorConfigLocation);
	            springApplicationProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, configName);

	        } else {
	            springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);
//	            springApplicationProperties.setProperty(SPRING_CONFIG_NAME_PROPERTY_NAME, springConfigName);
	            LOGGER.warn("SystemProperty \"{}\" not given or not resolvable! Startup using default spring external configuration!", CONNECTOR_CLIENT_CONFIG_FILE_PROPERTY_NAME);
	        }
	        application.properties(springApplicationProperties); //pass the mapped CONNECTOR_CONFIG_FILE to the spring properties...
	        return application.sources(DomibusConnectorClientWAR.class);
	    }

	    /**
	     * Will only be called if the Application is deployed within an web application server
	     * adds to the boostrap and spring config location search path a web application context
	     * dependent search path:
	     *  app deployed under context /connector will look also for config under [workingpath]/config/[webcontext]/,
	     *  [workingpath]/conf/[webcontext]/
	     *
	     * @param servletContext the servlet context
	     * @throws ServletException in case of an error @see {@link SpringBootServletInitializer#onStartup(ServletContext)} 
	     *
	     * {@inheritDoc}
	     *
	     */
	    @Override
	    public void onStartup(ServletContext servletContext) throws ServletException {
	        this.servletContext = servletContext;

	        String connectorConfigFile = getConnectorConfigFilePropertyName();
	        if (servletContext != null) {

	        	if(connectorConfigFile == null)
	        		connectorConfigFile = servletContext.getInitParameter(CONNECTOR_CLIENT_CONFIG_FILE_PROPERTY_NAME);
	           
	            springConfigLocation = springConfigLocation +
	                    ",file:./config/" + servletContext + "/" +
	                    ",file:./conf/" + servletContext + "/";
//	            springApplicationProperties.setProperty(SPRING_CONFIG_LOCATION_PROPERTY_NAME, springConfigLocation);

	        }

	        //read logging.config from connector properties and set it before the application context ist started
	        //so its already available for the spring logging servlet initializer to configure logging!
	        if (connectorConfigFile != null) {
	        	System.setProperty(CONNECTOR_CLIENT_CONFIG_FILE_PROPERTY_NAME, connectorConfigFile);
	            Properties p = loadConnectorConfigProperties(connectorConfigFile);
	            String loggingConfig = p.getProperty("logging.config");
	            if (loggingConfig != null) {
	                servletContext.setInitParameter("logging.config", loggingConfig);
	            }
	        }
	        super.onStartup(servletContext);
	    }

	    private void setFromServletContextIfNotNull(String name, String setPropertyName) {
	        String value = servletContext.getInitParameter(name);
	        LOGGER.info("Config name from servletContext is [{}] value is [{}]", name, value);
	        if (value != null) {
	            LOGGER.info("Setting servletInitParam [{}] to value [{}]", setPropertyName, value);
	            servletContext.setInitParameter(setPropertyName, value);
	        }
	    }


	    /***
	     * {@inheritDoc}
	     *
	     */
	    @Override
	    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	        return configureApplicationContext(application);
	    }

}

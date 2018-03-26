package eu.domibus.connector.client.runnable;

import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;

import eu.domibus.connector.client.runnable.configuration.DomibusConnectorClientConfiguration;
import eu.domibus.connector.client.runnable.configuration.StandaloneClientProperties;

@SpringBootApplication(scanBasePackages="eu.domibus.connector")
@EnableConfigurationProperties(StandaloneClientProperties.class)
@EnableScheduling
public class DomibusConnector {

	/**
	 * @param args
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {

		boolean startWithGUI = false;
		if(ArrayUtils.contains(args, "-gui")){
			startWithGUI=true;
		}
		//    	
		//        String connectorProperties = System.getProperty("connector.properties");
		//        if (!StringUtils.hasText(connectorProperties)) {
		//            connectorProperties = System.getenv("connector.properties");
		//        }
		//        if (!StringUtils.hasText(connectorProperties)) {
		//            connectorProperties = ConnectorProperties.CONNECTOR_PROPERTIES_FILE_PATH;
		//        }
		//        if(StringUtils.hasText(connectorProperties)){
		//        	System.setProperty("connector.properties", connectorProperties);
		//        }
		//        try{
		//        	ConnectorProperties.loadConnectorProperties();
		//        }catch(Exception e){
		//        	
		//        }
		//        if (!ConnectorProperties.CONNECTOR_PROPERTIES_FILE.exists() && startWithGUI) {
		//        	
		//        	try {
		//				Process process = new ProcessBuilder(
		//						"java", "-jar","domibusConnectorConfigurator.jar").start();
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//        	
		//        	System.exit(0);
		//        }
		//        
		//        
		//        if(!ConnectorProperties.CONNECTOR_PROPERTIES_FILE.exists()){
		//        	try{
		//                ConnectorProperties.loadConnectorProperties();
		//                }catch(Exception e){
		//                	throw new RuntimeException("Connector Properties could not be loaded!");
		//                }
		//		}
		//
		//        String loggingProperties = System.getProperty("logging.properties");
		//        if (!StringUtils.hasText(loggingProperties)) {
		//            loggingProperties = System.getenv("logging.properties");
		//        }
		//
		//
		//        if (!StringUtils.hasText(loggingProperties)) {
		//        	DomibusConnector.class.getResource("log4j.properties");
		//        	File classpathLog4j = new File(DomibusConnector.class.getClassLoader().getResource("log4j.properties").getFile());
		//        	if(classpathLog4j.exists())
		//            System.setProperty("logging.properties", classpathLog4j.getAbsolutePath());
		//        	else{
		//        		System.setProperty("logging.properties", ConnectorProperties.LOG4J_CONFIG_FILE_PATH);
		//        	}
		//        		
		//        }

		ConfigurableApplicationContext context = null;

		System.setProperty("crypto.policy", "unlimited");

		//        System.setProperty("http.proxySet", "true");
		//        System.setProperty("http.proxyHost", "172.30.9.12");
		//        System.setProperty("http.proxyPort", "8080");
		//        
		//        System.setProperty("https.proxySet", "true");
		//        System.setProperty("https.proxyHost", "172.30.9.12");
		//        System.setProperty("https.proxyPort", "8080");

		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
			for(Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		SpringApplicationBuilder builder = new SpringApplicationBuilder();

		SpringApplication springApp;

		if(startWithGUI) {
			springApp = builder
					.sources(DomibusConnectorClientConfiguration.class)
					.web(false)
					.profiles("swing-gui")
					.headless(false)
					.build();
		}else {
			springApp = builder
					.sources(DomibusConnectorClientConfiguration.class)
					.web(false)
					.build();

		}

		context = springApp.run(args);

		context.registerShutdownHook();
	}


	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}

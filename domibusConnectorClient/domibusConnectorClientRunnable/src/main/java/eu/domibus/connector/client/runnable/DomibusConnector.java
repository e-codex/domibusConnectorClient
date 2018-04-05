package eu.domibus.connector.client.runnable;

import java.awt.Window;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.runnable.configuration.ConnectorClientProperties;
import eu.domibus.connector.client.runnable.configuration.DomibusConnectorClientConfiguration;

@SpringBootApplication(scanBasePackages="eu.domibus.connector")
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
		    	
		        String connectorProperties = System.getProperty(ConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME);
		        if (!StringUtils.hasText(connectorProperties)) {
		            connectorProperties = System.getenv(ConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME);
		        }
		        if (!StringUtils.hasText(connectorProperties)) {
		            connectorProperties = ConnectorClientProperties.CONNECTOR_PROPERTIES_FILE_PATH;
		        }
		        if(StringUtils.hasText(connectorProperties)){
		        	System.setProperty(ConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME, connectorProperties);
		        }
		        try{
		        	ConnectorClientProperties.loadConnectorProperties();
		        }catch(Exception e){
		        	
		        }
//		        if (!ConnectorClientProperties.CONNECTOR_PROPERTIES_FILE.exists() && startWithGUI) {
//		        	
//		        	try {
//						Process process = new ProcessBuilder(
//								"java", "-jar","domibusConnectorConfigurator.jar").start();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//		        	
//		        	System.exit(0);
//		        }
		        
		        
		        if(!ConnectorClientProperties.CONNECTOR_PROPERTIES_FILE.exists()){
		        	try{
		        		ConnectorClientProperties.loadConnectorProperties();
		                }catch(Exception e){
		                	throw new RuntimeException("Connector Client Properties could not be loaded!");
		                }
				}
		
		        String loggingProperties = System.getProperty("logging.properties");
		        if (!StringUtils.hasText(loggingProperties)) {
		            loggingProperties = System.getenv("logging.properties");
		        }
		
		
		        if (!StringUtils.hasText(loggingProperties)) {
		        	File classpathLog4j = new File(ConnectorClientProperties.LOG4J_CONFIG_FILE_PATH);
//		        	DomibusConnector.class.getResource("log4j.properties");
//		        	File classpathLog4j = new File(DomibusConnector.class.getClassLoader().getResource("log4j.properties").getFile());
		        	if(classpathLog4j.exists())
		            System.setProperty("logging.properties", classpathLog4j.getAbsolutePath());
		        	else{
		        		System.setProperty("logging.properties", ConnectorClientProperties.LOG4J_CONFIG_FILE_PATH);
		        	}
		        		
		        }

		ConfigurableApplicationContext context = null;

		System.setProperty("crypto.policy", "unlimited");

		if(ConnectorClientProperties.proxyEnabled) {
		        System.setProperty("http.proxySet", Boolean.toString(ConnectorClientProperties.proxyEnabled));
		        System.setProperty("http.proxyHost", ConnectorClientProperties.proxyHost!=null?ConnectorClientProperties.proxyHost:"");
		        System.setProperty("http.proxyPort", ConnectorClientProperties.proxyPort!=null?ConnectorClientProperties.proxyPort:"");
		        
		        System.setProperty("https.proxySet", Boolean.toString(ConnectorClientProperties.proxyEnabled));
		        System.setProperty("https.proxyHost", ConnectorClientProperties.proxyHost);
		        System.setProperty("https.proxyPort", ConnectorClientProperties.proxyPort);
		}
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

}

package eu.domibus.connector.client.runnable;

import java.awt.Window;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.runnable.configuration.ConnectorClientProperties;
import eu.domibus.connector.client.runnable.configuration.DomibusConnectorClientConfiguration;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector")
@EnableScheduling
public class DomibusConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnector.class);

    /**
     * @param args
     */
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ApplicationContext ctx = createApplicationContext(args);
    }


    public static ApplicationContext createApplicationContext(String[] args) {
        String connectorProperties = null;
        boolean startWithGUI = false;
        boolean webEnabled = false;

        if (ArrayUtils.contains(args, "-gui")) {
            startWithGUI = true;
        }
        if (ArrayUtils.contains(args, "-web")) {
            webEnabled = true;
        }
        if (ArrayUtils.contains(args, "-properties")) {
            int i = ArrayUtils.indexOf(args, "-properties");
            i = i + 1;
            if (args.length > i) {
                String propertiesFileLocationPath = args[i];
                File connectorPropertiesFile = new File(propertiesFileLocationPath);
                if (connectorPropertiesFile.isFile()) {
                    //ConnectorClientProperties.CONNECTOR_PROPERTIES_FILE = connectorPropertiesFile;
                    connectorProperties = propertiesFileLocationPath;
                }
            }
        }


        if (!StringUtils.hasText(connectorProperties)) {
            connectorProperties = System.getProperty(ConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME);
        }
        if (!StringUtils.hasText(connectorProperties)) {
            connectorProperties = System.getenv(ConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME);
        }
        if (!StringUtils.hasText(connectorProperties)) {
            connectorProperties = ConnectorClientProperties.CONNECTOR_PROPERTIES_FILE_PATH;
        }
        if (StringUtils.hasText(connectorProperties)) {
            System.setProperty(ConnectorClientProperties.CONNECTOR_CLIENT_PROPERTIES_NAME, connectorProperties);
        }
        try {
            ConnectorClientProperties.loadConnectorProperties();
        } catch (Exception e) {

        }

        if (ConnectorClientProperties.CONNECTOR_PROPERTIES_FILE.exists()) {
            try {
                ConnectorClientProperties.loadConnectorProperties();
            } catch (Exception e) {
                throw new RuntimeException("Connector Client Properties could not be loaded!", e);
            }
        }

        String loggingProperties = System.getProperty("logging.properties");
        if (!StringUtils.hasText(loggingProperties)) {
            loggingProperties = System.getenv("logging.properties");
        }


        if (!StringUtils.hasText(loggingProperties)) {
        	loggingProperties = ConnectorClientProperties.LOG4J_CONFIG_FILE_PATH;
        	
        }
        File log4jProperties = new File(loggingProperties);
        
        if(!log4jProperties.exists()) {
            loggingProperties= ConnectorClientProperties.LOG4J_CONFIG_FILE_PATH;
        }
        
        System.setProperty("log4j.configurationFile", loggingProperties);
        Configurator.initialize("Properties_Config", loggingProperties);
        LOGGER.debug("Logging set with properties at {}",loggingProperties);
        
        ConfigurableApplicationContext context = null;

        System.setProperty("crypto.policy", "unlimited");

        if (ConnectorClientProperties.proxyEnabled) {
            System.setProperty("http.proxySet", Boolean.toString(ConnectorClientProperties.proxyEnabled));
            System.setProperty("http.proxyHost", ConnectorClientProperties.proxyHost != null ? ConnectorClientProperties.proxyHost : "");
            System.setProperty("http.proxyPort", ConnectorClientProperties.proxyPort != null ? ConnectorClientProperties.proxyPort : "");

            System.setProperty("https.proxySet", Boolean.toString(ConnectorClientProperties.proxyEnabled));
            System.setProperty("https.proxyHost", ConnectorClientProperties.proxyHost);
            System.setProperty("https.proxyPort", ConnectorClientProperties.proxyPort);
        }
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            for (Window window : JFrame.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        SpringApplication springApp;

        if (startWithGUI) {
            builder = builder
                    .profiles("swing-gui")
                    .headless(false);
        }
        if (webEnabled) {
            builder = builder
                    .web(true)
                    .profiles(new String[]{"push", "wslink"});
        } else {
            builder = builder.web(false);
        }

        builder = builder.sources(DomibusConnectorClientConfiguration.class);

        springApp = builder.build();
        context = springApp.run(args);
        context.registerShutdownHook();
        return context;
    }

}

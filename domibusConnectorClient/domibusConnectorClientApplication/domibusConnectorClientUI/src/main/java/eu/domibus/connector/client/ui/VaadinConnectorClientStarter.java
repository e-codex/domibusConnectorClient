package eu.domibus.connector.client.ui;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import eu.domibus.connector.client.starter.DomibusConnectorClientApplicationStarter;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client")
public class VaadinConnectorClientStarter extends SpringBootServletInitializer {

	public static void main(String[] args) {
        //just call the DomibusConnectorStarter...
		DomibusConnectorClientApplicationStarter.runSpringApplication(args);
    }
}


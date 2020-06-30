package eu.domibus.connector.client.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = "eu.domibus.connector.client.ui")
public class VaadinConnectorClientStarter {

	public VaadinConnectorClientStarter() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		SpringApplication.run(VaadinConnectorClientStarter.class, args);
	}

}

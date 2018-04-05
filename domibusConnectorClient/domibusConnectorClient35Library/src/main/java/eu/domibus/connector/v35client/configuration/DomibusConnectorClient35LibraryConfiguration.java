package eu.domibus.connector.v35client.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:/spring/context/DomibusConnectorNationalBackendClientContext.xml")
public class DomibusConnectorClient35LibraryConfiguration {

	public DomibusConnectorClient35LibraryConfiguration() {
		// TODO Auto-generated constructor stub
	}

}

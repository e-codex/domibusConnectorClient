package eu.domibus.connector.client.ui.configuration;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class VaadinConnectorClientUIConfiguration {
	
	@Value("${connector-client-rest-url}")
	private String connectorClientRestURL;

	@Value("${server.port}")
	private int serverPort;
	
	@Bean
	public WebClient restClient(WebClient.Builder builder) {
		if (serverPort > 1024 && StringUtils.isEmpty(connectorClientRestURL)) {
			return builder.baseUrl("http://localhost:" + serverPort + "/").build();
		}
		return builder.baseUrl(connectorClientRestURL).build();
	}
}

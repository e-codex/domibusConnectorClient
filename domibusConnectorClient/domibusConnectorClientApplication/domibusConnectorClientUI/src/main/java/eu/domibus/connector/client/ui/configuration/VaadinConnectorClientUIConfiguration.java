package eu.domibus.connector.client.ui.configuration;

import eu.domibus.connector.client.rest.DomibusConnectorClientRestAPI;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class VaadinConnectorClientUIConfiguration {

	public static final String CLIENT_UI_REST_CLIENT_QUALIFIER = "restClient";
	@Value("${connector-client-rest-url:#{null}}")
	private String connectorClientRestURL;


	@Bean
	@Qualifier(CLIENT_UI_REST_CLIENT_QUALIFIER)
	public WebClient restClient(WebClient.Builder builder, ServletWebServerApplicationContext webServerAppCtxt) {
		int serverPort = webServerAppCtxt.getWebServer().getPort();
		//if connectorClientRestURL not given just use own port + url
		if (serverPort > 1024 && StringUtils.isEmpty(connectorClientRestURL)) {
			return builder.baseUrl("http://localhost:" + serverPort + DomibusConnectorClientRestAPI.RESTSERVICE_PATH).build();
		}
		return builder.baseUrl(connectorClientRestURL).build();
	}
}

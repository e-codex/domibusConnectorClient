package eu.domibus.connector.client.controller.configuration;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@ConfigurationProperties(prefix = DomibusConnectorClientRestClientConfigurationProperties.PREFIX)
public class DomibusConnectorClientRestClientConfigurationProperties {
	
	public static final String PREFIX = DomibusConnectorClientControllerConfig.PREFIX + ".delivery-rest-client";
	
	@NotNull
	private String url;
	
	@NotNull
	private boolean enabled;
	
	@NotNull
	private String deliverNewMessageMethodUrl;
	
	@NotNull
	private String deliverNewConfirmationMethodUrl;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDeliverNewMessageMethodUrl() {
		return deliverNewMessageMethodUrl;
	}

	public void setDeliverNewMessageMethodUrl(String deliverNewMessageMethodUrl) {
		this.deliverNewMessageMethodUrl = deliverNewMessageMethodUrl;
	}

	public String getDeliverNewConfirmationMethodUrl() {
		return deliverNewConfirmationMethodUrl;
	}

	public void setDeliverNewConfirmationMethodUrl(String deliverNewConfirmationMethodUrl) {
		this.deliverNewConfirmationMethodUrl = deliverNewConfirmationMethodUrl;
	}

}

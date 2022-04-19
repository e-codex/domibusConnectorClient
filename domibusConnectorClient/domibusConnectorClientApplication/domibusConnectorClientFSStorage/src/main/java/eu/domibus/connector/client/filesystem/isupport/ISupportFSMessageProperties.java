package eu.domibus.connector.client.filesystem.isupport;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;

@Component
@ConditionalOnProperty(prefix=DomibusConnectorClientFSStorageConfiguration.PREFIX, name=DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue="true")
@ConfigurationProperties(prefix = ISupportFSMessageProperties.PREFIX)
@Profile("iSupport")
@PropertySource("classpath:/connector-client-fs-isupport-message.properties")
@Validated
@Valid
public class ISupportFSMessageProperties {
	
	public static final String PREFIX = "connector-client.storage.filesystem.message-properties";

	@NotEmpty
	private String fileName;
	@NotEmpty
	private String service;
	@NotEmpty
	private String action;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}

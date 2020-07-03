package eu.domibus.connector.client.filesystem.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.DomibusConnectorClientFSStorage;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFSStorageImpl;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorage;

@Configuration
@ConditionalOnProperty(prefix=DomibusConnectorClientFSStorageConfiguration.PREFIX, name=DomibusConnectorClientFSStorageConfiguration.ENABLED_PROPERTY_NAME, havingValue="true")
@ConfigurationProperties(prefix = DomibusConnectorClientFSStorageConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-fs-storage-default.properties")
@Validated
@Valid
public class DomibusConnectorClientFSStorageConfiguration {
	
	public static final String PREFIX = "connector-client.storage.filesystem";
    public static final String ENABLED_PROPERTY_NAME = "enabled";
    
    @NestedConfigurationProperty
    @NotNull
    private DirectoryConfigurationProperties messages;
    
//    @NestedConfigurationProperty
//    @NotNull
//    private DomibusConnectorClientFSMessageProperties messageProperties;
//    
//    @NestedConfigurationProperty
//    @NotNull
//    private DomibusConnectorClientFSProperties properties;

	public DomibusConnectorClientFSStorageConfiguration() {
		// TODO Auto-generated constructor stub
	}
	
    @Bean
	public DomibusConnectorClientStorage domibusConnectorClientFSStorage() {
    	DomibusConnectorClientFSStorage fsStorage = new DomibusConnectorClientFSStorageImpl();
		
		fsStorage.setMessagesDir(messages.getPath().toFile());
		
		return fsStorage;
	}

	
	public DirectoryConfigurationProperties getMessages() {
		return messages;
	}

	public void setMessages(DirectoryConfigurationProperties messages) {
		this.messages = messages;
	}

//	public DomibusConnectorClientFSMessageProperties getMessageProperties() {
//		return messageProperties;
//	}
//
//	public void setMessageProperties(DomibusConnectorClientFSMessageProperties messageProperties) {
//		this.messageProperties = messageProperties;
//	}
//
//	public DomibusConnectorClientFSProperties getProperties() {
//		return properties;
//	}
//
//	public void setProperties(DomibusConnectorClientFSProperties properties) {
//		this.properties = properties;
//	}

}

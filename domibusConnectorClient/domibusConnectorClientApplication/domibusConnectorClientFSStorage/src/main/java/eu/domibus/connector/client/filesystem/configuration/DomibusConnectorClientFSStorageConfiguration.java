package eu.domibus.connector.client.filesystem.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.DomibusConnectorClientFSStorage;
import eu.domibus.connector.client.filesystem.DomibusConnectorClientFSStorageImpl;
import eu.domibus.connector.client.filesystem.configuration.validation.CheckFolderWriteable;
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
    
    @CheckFolderWriteable
    private DirectoryConfigurationProperties incoming;
    
    @CheckFolderWriteable
    private DirectoryConfigurationProperties outgoing;
    
    @NotNull
    private DomibusConnectorClientFSMessageProperties messageProperties;

	public DomibusConnectorClientFSStorageConfiguration() {
		// TODO Auto-generated constructor stub
	}
	
    @Bean
	public DomibusConnectorClientStorage domibusConnectorClientFSStorage() {
    	DomibusConnectorClientFSStorage fsStorage = new DomibusConnectorClientFSStorageImpl();
		
		fsStorage.setIncomingMessagesDir(incoming.getPath().toFile());
		
		fsStorage.setOutgoingMessagesDir(outgoing.getPath().toFile());
		
		return fsStorage;
	}

}

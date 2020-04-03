package eu.domibus.connector.client.controller.configuration;

import javax.validation.constraints.NotNull;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eu.domibus.connector.client.controller.persistence.dao.PackageDomibusConnectorClientRepositories;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientPersistenceModel;

@Configuration
@EntityScan(basePackageClasses={PDomibusConnectorClientPersistenceModel.class})
@EnableJpaRepositories(basePackageClasses = {PackageDomibusConnectorClientRepositories.class} )
@EnableTransactionManagement
@ConfigurationProperties(prefix = DomibusConnectorClientControllerConfig.PREFIX)
@PropertySource("classpath:/connector-client-controller-default.properties")
public class DomibusConnectorClientControllerConfig {

	public static final String PREFIX = "connector-client.controller";
	
	@NestedConfigurationProperty
    @NotNull
    private DefaultConfirmationAction confirmationDefaultAction;
	
	public DomibusConnectorClientControllerConfig() {
		// TODO Auto-generated constructor stub
	}

	public DefaultConfirmationAction getConfirmationDefaultAction() {
		return confirmationDefaultAction;
	}

	public void setConfirmationDefaultAction(DefaultConfirmationAction confirmationDefaultAction) {
		this.confirmationDefaultAction = confirmationDefaultAction;
	}

}

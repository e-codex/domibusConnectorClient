package eu.domibus.connector.client.controller.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import eu.domibus.connector.client.controller.persistence.dao.PackageDomibusConnectorClientRepositories;
import eu.domibus.connector.client.controller.persistence.model.PDomibusConnectorClientPersistenceModel;

@Configuration
@EntityScan(basePackageClasses={PDomibusConnectorClientPersistenceModel.class})
@EnableJpaRepositories(basePackageClasses = {PackageDomibusConnectorClientRepositories.class} )
@EnableTransactionManagement
public class DomibusConnectorClientControllerConfig {

	public DomibusConnectorClientControllerConfig() {
		// TODO Auto-generated constructor stub
	}

}

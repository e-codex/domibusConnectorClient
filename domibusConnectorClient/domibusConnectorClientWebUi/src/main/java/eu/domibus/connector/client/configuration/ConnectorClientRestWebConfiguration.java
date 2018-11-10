package eu.domibus.connector.client.configuration;

import eu.domibus.connector.client.storage.dao.RepoPackage;
import eu.domibus.connector.client.storage.entity.EntityClassesPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.ws.config.annotation.EnableWs;

@Configuration
@EnableJpaRepositories(basePackageClasses = RepoPackage.class)
@EntityScan(basePackageClasses = EntityClassesPackage.class)
@EnableWs
public class ConnectorClientRestWebConfiguration {

}

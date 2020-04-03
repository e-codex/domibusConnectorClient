
package eu.domibus.connector.client.spring;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapperDefaultImpl;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
@ConfigurationProperties(prefix = ConnectorClientAutoConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-library-default.properties")
@Validated
@Valid
public class ConnectorClientAutoConfiguration {

	public static final String PREFIX = "connector-client.library";

    /**
     * if not available create a default ContentMapper Bean.
     * This implementation does a identity mapping so the business
     * xml is not changed
     * @return - a 1:1 mapping implementation
     */
    @Bean
    @ConditionalOnMissingBean(DomibusConnectorClientContentMapper.class)
    public DomibusConnectorClientContentMapper contentMapper() {
    	return new DomibusConnectorClientContentMapperDefaultImpl();
    }

	

    

}

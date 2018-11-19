
package eu.domibus.connector.client.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.DomibusConnectorContentMapperDefaultImpl;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
public class ConnectorClientConfiguration {


    /**
     * if not available create a default ContentMapper Bean.
     * This implementation does a identity mapping so the business
     * xml is not changed
     * @return - a 1:1 mapping implementation
     */
    @Bean
    @ConditionalOnMissingBean(DomibusConnectorContentMapper.class)
    public DomibusConnectorContentMapper contentMapper() {
    	return new DomibusConnectorContentMapperDefaultImpl();
    }
    
}

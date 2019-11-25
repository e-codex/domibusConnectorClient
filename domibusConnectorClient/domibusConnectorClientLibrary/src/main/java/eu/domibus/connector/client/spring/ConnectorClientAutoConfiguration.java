
package eu.domibus.connector.client.spring;

import eu.domibus.connector.client.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.client.mapping.DomibusConnectorContentMapperDefaultImpl;
import eu.domibus.connector.client.process.ProcessMessageFromConnectorToNational;
import eu.domibus.connector.client.process.ProcessMessageFromNationalToConnector;
import eu.domibus.connector.client.process.impl.ProcessMessageFromConnectorToNationalImpl;
import eu.domibus.connector.client.process.impl.ProcessMessageFromNationalToConnectorImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
public class ConnectorClientAutoConfiguration {


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

    /**
     * Create default
     * @return - the default ProcessMessageFromConnectorToNational, is just calling
     * the content mapper @see DomibusConnectorContentMapper
     */
    @Bean
    @ConditionalOnMissingBean(ProcessMessageFromConnectorToNational.class)
    public ProcessMessageFromConnectorToNationalImpl processMessageFromConnectorToNationalImpl() {
        return new ProcessMessageFromConnectorToNationalImpl();
    }

    /**
     * Create default
     * @return - the default ProcessMessageFromNationalToConnector, is just calling
     * the content mapper @see DomibusConnectorContentMapper
     */
    @Bean
    @ConditionalOnMissingBean(ProcessMessageFromNationalToConnector.class)
    public ProcessMessageFromNationalToConnectorImpl processMessageFromNationalToConnectorImpl() {
        return new ProcessMessageFromNationalToConnectorImpl();
    }

}

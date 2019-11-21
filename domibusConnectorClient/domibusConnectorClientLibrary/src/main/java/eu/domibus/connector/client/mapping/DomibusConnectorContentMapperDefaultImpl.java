package eu.domibus.connector.client.mapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


public class DomibusConnectorContentMapperDefaultImpl implements DomibusConnectorContentMapper {

    private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorContentMapperDefaultImpl.class);

    @Override
    public void mapInternationalToNational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException {
        LOGGER.debug("#mapInternationalToNational: Default Mapper called - doing nothing");
        // Default implementation does nothing!

    }

    @Override
    public void mapNationalToInternational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException {
    	// Default implementation does nothing!
        LOGGER.debug("#mapNationalToInternational: Default Mapper called - doing nothing");
    }

}

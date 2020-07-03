package eu.domibus.connector.client.mapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.domibus.connector.client.mapping.exception.DomibusConnectorClientContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;


public class DomibusConnectorClientContentMapperDefaultImpl implements DomibusConnectorClientContentMapper {

    private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientContentMapperDefaultImpl.class);

    @Override
    public DomibusConnectorMessageType mapInboundBusinessContent(DomibusConnectorMessageType message) throws DomibusConnectorClientContentMapperException {
    	LOGGER.debug("#mapInboundBusinessContent: Default Mapper called - validating businessContent");
    	DomibusConnectorClientContentMapperUtil.validateBusinessContent(message);
    	
    	LOGGER.debug("#mapInboundBusinessContent: Default Mapper called - doing nothing");
        // Default implementation does nothing!
		return message;

    }

    @Override
    public DomibusConnectorMessageType mapOutboundBusinessContent(DomibusConnectorMessageType message) throws DomibusConnectorClientContentMapperException {
    	LOGGER.debug("#mapOutboundBusinessContent: Default Mapper called - validating businessContent");
    	DomibusConnectorClientContentMapperUtil.validateBusinessContent(message);
    	
    	// Default implementation does nothing!
        LOGGER.debug("#mapOutboundBusinessContent: Default Mapper called - doing nothing");
		return message;
    }
    
    

}

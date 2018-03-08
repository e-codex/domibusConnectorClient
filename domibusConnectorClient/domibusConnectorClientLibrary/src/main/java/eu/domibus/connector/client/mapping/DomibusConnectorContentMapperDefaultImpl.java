package eu.domibus.connector.client.mapping;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@ConditionalOnMissingBean(DomibusConnectorContentMapper.class)
public class DomibusConnectorContentMapperDefaultImpl implements DomibusConnectorContentMapper {

    @Override
    public void mapInternationalToNational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorContentMapper", "mapInternationalToNational");

    }

    @Override
    public void mapNationalToInternational(DomibusConnectorMessageType message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorContentMapper", "mapNationalToInternational");

    }

}

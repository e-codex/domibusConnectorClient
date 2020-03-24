package eu.domibus.connector.mapping;

import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.mapping.exception.DomibusConnectorContentMapperException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(DomibusConnectorContentMapper.class)
public class DomibusConnectorContentMapperDefaultImpl implements DomibusConnectorContentMapper {

    @Override
    public void mapInternationalToNational(Message message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorContentMapper", "mapInternationalToNational");

    }

    @Override
    public void mapNationalToInternational(Message message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorContentMapper", "mapNationalToInternational");

    }

}
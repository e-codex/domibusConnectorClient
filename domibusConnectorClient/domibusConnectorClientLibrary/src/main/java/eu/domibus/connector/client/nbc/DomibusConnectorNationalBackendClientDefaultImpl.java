package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(DomibusConnectorNationalBackendClientDelivery.class)
public class DomibusConnectorNationalBackendClientDefaultImpl implements DomibusConnectorNationalBackendClientDelivery {


	@Override
	public DomibusConnectorMessageResponseType processMessageFromConnector(DomibusConnectorMessageType message)
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "processMessagesFromConnector");
	}

}

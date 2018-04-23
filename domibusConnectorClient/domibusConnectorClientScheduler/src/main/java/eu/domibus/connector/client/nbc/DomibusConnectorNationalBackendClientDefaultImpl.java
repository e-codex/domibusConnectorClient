package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnMissingBean(DomibusConnectorNationalBackendClient.class)
public class DomibusConnectorNationalBackendClientDefaultImpl implements DomibusConnectorNationalBackendClient, DomibusConnectorNationalBackendClientDelivery {


	@Override
	public void processMessageFromConnector(DomibusConnectorMessageType message)
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "processMessagesFromConnector");
	}

	@Override
	public List<DomibusConnectorMessageType> checkForMessagesOnNationalBackend()
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "checkForMessagesOnNationalBackend");
	}

}

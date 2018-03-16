package eu.domibus.connector.client.nbc;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@ConditionalOnMissingBean(DomibusConnectorNationalBackendClient.class)
public class DomibusConnectorNationalBackendClientDefaultImpl implements DomibusConnectorNationalBackendClient {


	@Override
	public void processMessagesFromConnector(List<DomibusConnectorMessageType> messages)
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "processMessagesFromConnector");
	}

	@Override
	public List<DomibusConnectorMessageType> checkForMessagesOnNationalBackend()
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "checkForMessagesOnNationalBackend");
	}

}

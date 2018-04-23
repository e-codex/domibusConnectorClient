package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnMissingBean(DomibusConnectorNationalBackendPushClient.class)
public class DomibusConnectorNationalBackendClientDefaultImpl implements DomibusConnectorNationalBackendPushClient {


	@Override
	public void processMessagesFromConnector(List<DomibusConnectorMessageType> messages)
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "processMessagesFromConnector");
	}

}

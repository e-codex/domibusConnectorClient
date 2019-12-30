package eu.domibus.connector.client.link.ws;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface DomibusConnectorClientDeliveryWSLink {

	public void deliverMessageFromConnector(DomibusConnectorMessageType message);
	
}

package eu.domibus.connector.client.process;

import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public interface ProcessMessageFromClientToConnector {

    /**
     *
     * @param messageType
     * @return the message ResponseType
     */
	DomibsConnectorAcknowledgementType processMessageFromClientToConnector(DomibusConnectorMessageType messageType);

    /**
     * this method will be called by the link implementations to send the message response related to a
     * submitted message
     * @param messageResponseType
     */
//    void processResponseFromConnectorToClient(DomibusConnectorMessageResponseType messageResponseType);

}

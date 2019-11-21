package eu.domibus.connector.client.process;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * This interface is called by the multiple Link implementations
 * to start the message transport
 */
public interface ProcessMessageFromConnectorToNational {

    /**
     *
     * @param messageType - the from the link impl received message
     * @return - the Respons of the message, at least the Response result must be set
     */
    DomibusConnectorMessageResponseType processMessageFromConnectorToNational(DomibusConnectorMessageType messageType);

    /**
     * this method will be called by the link implementations to send the message response to a
     * submitted message
     * @param messageResponseType
     */
    void processResponseFromConnectorToNational(DomibusConnectorMessageResponseType messageResponseType);

}

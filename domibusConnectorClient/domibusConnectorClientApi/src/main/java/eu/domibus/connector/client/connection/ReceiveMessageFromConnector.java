
package eu.domibus.connector.client.connection;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * PushMessage Service interface
 * must be implemented if message push from connector to backendClient
 * should be supported
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * 
 */
public interface ReceiveMessageFromConnector {

    public void receiveMessageFromConnector(DomibusConnectorMessageType message);
    
}

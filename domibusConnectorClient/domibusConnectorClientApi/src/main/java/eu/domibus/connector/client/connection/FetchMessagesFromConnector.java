
package eu.domibus.connector.client.connection;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.util.List;

/**
 * Fetches all (not yet fetched) messages from connector
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * 
 */
public interface FetchMessagesFromConnector {

    public List<DomibusConnectorMessageType> fetchMessages();
    
}

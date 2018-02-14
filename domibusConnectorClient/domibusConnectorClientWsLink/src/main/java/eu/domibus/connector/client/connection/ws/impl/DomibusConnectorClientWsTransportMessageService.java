
package eu.domibus.connector.client.connection.ws.impl;

import eu.domibus.connector.client.connection.FetchMessagesFromConnector;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Service
public class DomibusConnectorClientWsTransportMessageService implements FetchMessagesFromConnector {

    @Override
    public List<DomibusConnectorMessage> fetchMessages() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

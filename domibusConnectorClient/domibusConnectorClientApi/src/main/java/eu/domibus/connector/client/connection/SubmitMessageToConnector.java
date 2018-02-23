
package eu.domibus.connector.client.connection;

import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * This interface is implemented by the transporter service
 *  and transports the message to the connector via (Webservice, JMS, ...)
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface SubmitMessageToConnector {
    
    /**
     * 
     * @param message the message to send
     * @return - the message ack, where the message id
     */
    public DomibsConnectorAcknowledgementType submitMessage(DomibusConnectorMessageType message);
        
}

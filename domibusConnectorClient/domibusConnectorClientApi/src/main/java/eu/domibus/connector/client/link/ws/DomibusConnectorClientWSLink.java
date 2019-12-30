package eu.domibus.connector.client.link.ws;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This Interface class is an abstraction layer between the domibusConnectorClientWSLink module and the domibusConnectorClientLibrary.
 * It delegates methods to through the WSLink to the domibusConnectorAPI backend.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientWSLink {

	
	/**
	 * Delegate method to request via domibusConnector API new messages from the backend of the connector to the domibusConnectorClientLibrary.
	 * 
	 * @return
	 * @throws DomibusConnectorClientException
	 */
	public DomibusConnectorMessagesType requestMessagesFromConnector() throws DomibusConnectorClientException;
	
	/**
	 * Delegate method to submit a message from the domibusConnectorClientLibrary to the backend of the connector.
	 * 
	 * @param message
	 * @return
	 * @throws DomibusConnectorClientException
	 */
	public DomibsConnectorAcknowledgementType submitMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException;
	
}

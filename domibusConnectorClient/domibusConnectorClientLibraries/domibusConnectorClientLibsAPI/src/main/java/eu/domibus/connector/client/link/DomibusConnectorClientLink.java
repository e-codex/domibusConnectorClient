package eu.domibus.connector.client.link;

import eu.domibus.connector.client.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

/**
 * This Interface class is an abstraction layer between the domibusConnectorClientWSLink module and the domibusConnectorClientLibrary.
 * It delegates methods through the WSLink to the domibusConnectorAPI backend.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientLink {

	
	/**
	 * Delegate method to request new messages via domibusConnectorAPI from the backend of the domibusConnector to the domibusConnectorClientLibrary.
	 * 
	 * @return
	 * @throws DomibusConnectorBackendWebServiceClientException
	 */
	public DomibusConnectorMessagesType requestMessagesFromConnector() throws DomibusConnectorBackendWebServiceClientException;
	
	/**
	 * Delegate method to submit a message from the domibusConnectorClientLibrary to the backend of the domibusConnector.
	 * 
	 * @param message
	 * @return
	 * @throws DomibusConnectorBackendWebServiceClientException 
	 */
	public DomibsConnectorAcknowledgementType submitMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorBackendWebServiceClientException;
	
}

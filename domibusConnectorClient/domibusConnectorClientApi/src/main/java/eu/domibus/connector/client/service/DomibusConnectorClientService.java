package eu.domibus.connector.client.service;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

import java.util.List;
import java.util.Optional;

/**
 * Generic link interface for connector communication
 *
 */
public interface DomibusConnectorClientService {

	/**
	 *
	 * @return a list of messages not yet retrieved from the connector
	 * @throws DomibusConnectorClientException - any error
	 * @throws eu.domibus.connector.client.exception.DomibusConnectorClientMethodNotSupportedException - if the current implementation
	 * does not support this, eg. push implementations, where messages are only pushed to the client and connot be retrieved by an extra
	 * call
	 */
	public List<DomibusConnectorMessageType> requestMessagesFromConnector() throws DomibusConnectorClientException;

	/**
	 *
	 * @param message the message sent to the connector
	 * @return The return DomibusConnectorMessageResponseType can be set by an extra call most likely
	 * this will happen by async operating link implementations (eg. jms async calls, webservice async calls)
	 *
	 * @throws DomibusConnectorClientException
	 */
	public Optional<DomibusConnectorMessageResponseType> pushMessageToConnector(DomibusConnectorMessageType message)throws DomibusConnectorClientException;

	/**
	 * Sets the messageResponseType for a specific message
	 *  responseForMessageId must set to the nationalBackendId
	 *  or the ebmsId (the values are checked in this order)
	 * @param messageResponseType - the responseType
	 * @throws DomibusConnectorClientException
	 */
	void setMessageResponse(DomibusConnectorMessageResponseType messageResponseType) throws DomibusConnectorClientException;
}

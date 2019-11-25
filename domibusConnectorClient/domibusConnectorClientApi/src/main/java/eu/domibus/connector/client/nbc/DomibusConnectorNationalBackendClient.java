package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientMethodNotSupportedException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageResponseType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

import java.util.List;
import java.util.Optional;

/**
 * Should be implemented, so the connector can
 *  call setMessageResponse and pushMessageToNationalBackend
 *  can send/retreive messages to/from the national Backend
 */
//API do not change this...
public interface DomibusConnectorNationalBackendClient {

	/**
	 *
	 * @return the Response of the message, may be set
	 * by an extra call #setMessageResponse
	 * @throws DomibusConnectorNationalBackendClientException
	 */
	public Optional<DomibusConnectorMessageResponseType> pushMessageToNationalBackend(DomibusConnectorMessageType message) throws DomibusConnectorNationalBackendClientException;

	/**
	 *
	 * @return - a list of not retrieved messages on the backend system
	 * @throws DomibusConnectorNationalBackendClientException - if any problem occures
	 */
	public List<DomibusConnectorMessageType> checkForMessagesOnNationalBackend() throws DomibusConnectorNationalBackendClientException;

	/**
	 * Will be called to set the messageResponse for a already processed message
	 * The
	 * @param responseType - the response for the message passed with messageType
	 * @throws DomibusConnectorNationalBackendClientException - if a problem occures
	 * @throws DomibusConnectorNationalBackendClientMethodNotSupportedException - if the current implementation does not support setting the response with
	 * an extra request
	 */
	public void setMessageResponse(DomibusConnectorMessageResponseType responseType) throws DomibusConnectorNationalBackendClientMethodNotSupportedException, DomibusConnectorNationalBackendClientException;
	
}

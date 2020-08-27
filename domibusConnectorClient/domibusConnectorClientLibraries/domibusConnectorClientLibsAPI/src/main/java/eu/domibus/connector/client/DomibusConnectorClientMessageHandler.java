package eu.domibus.connector.client;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

/**
 * Interface to prepare a messages' business content XML before submitting/delivering it.
 * 
 * @author riederb
 *
 */
public interface DomibusConnectorClientMessageHandler {

	/**
	 * Indicates wether a message is INBOUND (delivered from domibusConnector) or OUTBOUND (to be submitted to domibusConnector).
	 * @author riederb
	 *
	 */
	public enum Direction{INBOUND,OUTBOUND};
	
	/**
	 * Method to prepare a messages' business content XML to be submitted or delivered.
	 * First, the implementation of {@link eu.domibus.connector.client.schema.validation.DCCBeforeMappingSchemaValidator} is called if present.
	 * Then, the message is mapped calling the implementation of {@link eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper} if present. 
	 * Last, the message gets again validated with an implementation of {@link eu.domibus.connector.client.schema.validation.DCCAfterMappingSchemaValidator} if present.
	 * 
	 * @param message
	 * @param direction
	 * @throws DomibusConnectorClientException
	 */
	void prepareMessage(DomibusConnectorMessageType message, Direction direction)
			throws DomibusConnectorClientException;

}
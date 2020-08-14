package eu.domibus.connector.client.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.exception.MessageSubmissionException;
import eu.domibus.connector.client.rest.exception.ParameterException;
import eu.domibus.connector.client.rest.exception.StorageException;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;

/**
 * This REST interface allows a backend application that is connected to the domibusConnectorClient to submit messages to it that have not been processed by
 * the domibusConnectorClient before.
 *  
 * 
 * @author riederb
 *
 */
@RequestMapping("/submissionrestservice")
public interface DomibusConnectorClientSubmissionRestAPI {

	/**
	 * With this method a new message may be submitted to the domibusConnectorClient by a backend application via REST service.
	 * The message is then processed by the domibusConnectorClient and submitted to the domibusConnector.
	 * 
	 * @param message The domibusConnectorClient message to be processed and submitted to the domibusConnector. This message object
	 * must already be built completely with all message files attached, as the domibusConnectorClient presumes that the message submitted is already prepared.
	 * @return
	 * @throws MessageSubmissionException
	 * @throws StorageException
	 * @throws ParameterException
	 */
	@PostMapping(
			value = "/submitNewMessageFromBackendToConnectorClient", consumes = "application/json", produces = "application/json")
	Boolean submitNewMessageFromBackendToConnectorClient(DomibusConnectorClientMessage message) throws MessageSubmissionException, StorageException, ParameterException;
	
	/**
	 * This method allows a backend application of the domibusConnectorClient to trigger a confirmation for a message. The confirmation trigger will be forwarded to the 
	 * domibusConnector which then generates the confirmation and submits it to the original sender of the message. The generated confirmation also is sent back to the
	 * domibusConnectorClient and stored there.
	 * 
	 * @param message A message object with the header information required to find the original message. Also a confirmaiton object with the type of confirmation
	 * triggered must be included.
	 * @return
	 * @throws MessageSubmissionException
	 * @throws ParameterException
	 * @throws MessageNotFoundException
	 */
	@PostMapping(
			value = "/triggerConfirmationAtConnectorClient", consumes = "application/json", produces = "application/json")
	Boolean triggerConfirmationAtConnectorClient(DomibusConnectorClientMessage message) throws MessageSubmissionException, ParameterException, MessageNotFoundException;
}

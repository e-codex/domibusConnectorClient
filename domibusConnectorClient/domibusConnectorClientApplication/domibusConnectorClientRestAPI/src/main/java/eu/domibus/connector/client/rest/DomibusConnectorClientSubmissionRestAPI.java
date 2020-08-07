package eu.domibus.connector.client.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.domibus.connector.client.rest.exception.MessageNotFoundException;
import eu.domibus.connector.client.rest.exception.MessageSubmissionException;
import eu.domibus.connector.client.rest.exception.ParameterException;
import eu.domibus.connector.client.rest.exception.StorageException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@RequestMapping("/submissionrestservice")
public interface DomibusConnectorClientSubmissionRestAPI {

	@PostMapping(
			value = "/submitNewMessageFromBackendToConnectorClient", consumes = "application/json", produces = "application/json")
	Boolean submitNewMessageFromBackendToConnectorClient(DomibusConnectorMessageType message) throws MessageSubmissionException, StorageException, ParameterException;
	
	@PostMapping(
			value = "/triggerConfirmationAtConnectorClient", consumes = "application/json", produces = "application/json")
	Boolean triggerConfirmationAtConnectorClient(DomibusConnectorMessageType message) throws MessageSubmissionException, ParameterException, MessageNotFoundException;
}

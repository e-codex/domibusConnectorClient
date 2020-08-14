package eu.domibus.connector.client.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;

@RequestMapping("/messagerestservice")
public interface DomibusConnectorClientMessageRestAPI {

	@GetMapping("/requestNewMessagesFromConnectorClient")
	DomibusConnectorClientMessageList requestNewMessagesFromConnectorClient();
	
	@GetMapping(value = "/requestConfirmationsForMessageFromConnectorClient", consumes = "application/json", produces = "application/json")
	DomibusConnectorClientMessage requestConfirmationsForMessageFromConnectorClient(@RequestBody DomibusConnectorClientMessage message);
}

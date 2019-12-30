package eu.domibus.connector.client.link.ws.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.ws.DomibusConnectorClientWSLink;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;

public class DomibusConnectorClientWSLinkImpl implements DomibusConnectorClientWSLink {

	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientWSLinkImpl.class);

	@Autowired
	DomibusConnectorBackendWebService connectorWsClient;

	@Override
	public DomibusConnectorMessagesType requestMessagesFromConnector() throws DomibusConnectorClientException{
		try {
			DomibusConnectorMessagesType domibusConnectorMessagesType = connectorWsClient.requestMessages(new EmptyRequestType());
			return domibusConnectorMessagesType;
		} catch (Exception e) {
			LOGGER.error("Exeception while requesting messages from Connector!");
			throw new DomibusConnectorClientException("Exeception while requesting messages from Connector!", e);
		}
	}

	@Override
	public DomibsConnectorAcknowledgementType submitMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException{
		try {
			DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType = connectorWsClient.submitMessage(message);
			return domibsConnectorAcknowledgementType;
		} catch (Exception e) {
			LOGGER.error("Exeception while submitting message to Connector!");
			throw new DomibusConnectorClientException("Exeception while submitting message to Connector!", e);
		}
	}

}

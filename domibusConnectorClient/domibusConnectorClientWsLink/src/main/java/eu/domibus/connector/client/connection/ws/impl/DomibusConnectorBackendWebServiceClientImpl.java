package eu.domibus.connector.client.connection.ws.impl;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.JAXB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import eu.domibus.connector.client.connection.exception.DomibusConnectorBackendWebServiceClientException;
import eu.domibus.connector.client.connection.ws.DomibusConnectorBackendWebServiceClient;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;

@Service
public class DomibusConnectorBackendWebServiceClientImpl implements DomibusConnectorBackendWebServiceClient {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorBackendWebServiceClientImpl.class);

	@Resource(name="connectorWsClient")
	DomibusConnectorBackendWebService backendWebServiceClient;

	@Override
	public void submitMessage(DomibusConnectorMessageType messageDTO) throws DomibusConnectorBackendWebServiceClientException {
		LOGGER.debug("#submitMessage: submitting message [{}] to backendWebService", messageDTO);
		try {
			DomibsConnectorAcknowledgementType submitMessageAck = backendWebServiceClient.submitMessage(messageDTO);

			if(submitMessageAck!=null) {
				if(!submitMessageAck.isResult()) {
					throw new DomibusConnectorBackendWebServiceClientException("backendWebService return negative acknowledgement: "
							+ submitMessageAck.getResultMessage()
							+ " Check domibusConnector for further details on the message submission status!"
							);
				}
			}else {
				throw new DomibusConnectorBackendWebServiceClientException("Exception calling the backendWebService: "
						+ "no proper acknowledgement received. "
						+ "Check domibusConnector for further details on the message submission status!"
						);
			}
		}catch(Exception e) {
			throw new DomibusConnectorBackendWebServiceClientException("Exception calling the backendWebService: ",e);
		}
	}

	@Override
	public List<DomibusConnectorMessageType> requestMessages() throws DomibusConnectorBackendWebServiceClientException {
		LOGGER.debug("fetchMessages from connector");
		try {
			DomibusConnectorMessagesType requestMessages = backendWebServiceClient.requestMessages(new EmptyRequestType());
			List<DomibusConnectorMessageType> messages = requestMessages.getMessages();
			LOGGER.debug("successfully fetched [{}] messages from connector", messages.size());

			return messages;
		}catch(Exception e) {
			throw new DomibusConnectorBackendWebServiceClientException("Exception calling the backendWebService: ",e);
		}
	}

}

package eu.domibus.connector.client.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.DomibusConnectorClientBackend;
import eu.domibus.connector.client.DomibusConnectorDeliveryClient;
import eu.domibus.connector.client.exception.DomibusConnectorClientBackendException;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorClientContentMapperException;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@ConditionalOnProperty(prefix = "connector-client.connector-link.ws", value = "pushEnabled", matchIfMissing = false)
@ConditionalOnMissingBean
public class DomibusConnectorDeliveryClientImpl implements DomibusConnectorDeliveryClient {

	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorDeliveryClientImpl.class);
	
	@Autowired
    private DomibusConnectorClientContentMapper contentMapper;
	
	@Autowired
    private DomibusConnectorClientBackend clientBackend;
	
	public DomibusConnectorDeliveryClientImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void receiveDeliveredMessageFromConnector(DomibusConnectorMessageType message)
			throws DomibusConnectorClientException {
		try {
        	contentMapper.mapInboundBusinessContent(message);
        } catch (DomibusConnectorClientContentMapperException e) {
			LOGGER.error("Exception while mapping inbound message with ebmsId {}: ", message.getMessageDetails().getEbmsMessageId(), e);
			throw new DomibusConnectorClientException(e);
		}
		
		try {
			clientBackend.deliverNewMessageToClientBackend(message);
		} catch (DomibusConnectorClientBackendException e) {
			throw new DomibusConnectorClientException(e);
		}

	}

	@Override
	public void receiveDeliveredConfirmationMessageFromConnector(DomibusConnectorMessageType message)
			throws DomibusConnectorClientException {
		try {
			clientBackend.deliverNewConfirmationToClientBackend(message);
		} catch (DomibusConnectorClientBackendException e) {
			throw new DomibusConnectorClientException(e);
		}
	}

}

package eu.domibus.connector.client.transport;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClientDelivery;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class TransportMessagesFromConnectorToNationalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransportMessagesFromConnectorToNationalService.class);

    @Autowired
    private DomibusConnectorClientService clientService;

    @Autowired
    private DomibusConnectorNationalBackendClientDelivery nationalBackendClientDelivery;

    public void transportMessageToNational() throws DomibusConnectorClientException {
        List<DomibusConnectorMessageType> messages = null;

        messages = clientService.requestMessagesFromConnector();

        if (!CollectionUtils.isEmpty(messages)) {
            LOGGER.info("{} new messages from connector to transport to national backend...", messages.size());
            messages.stream().forEach( message -> {
                try {
                    nationalBackendClientDelivery.processMessageFromConnector(message);
                } catch (DomibusConnectorNationalBackendClientException | ImplementationMissingException e) {
                    LOGGER.error("Transporting message [{}] to national system failed!", message);
                }
            });
        }
    }

}

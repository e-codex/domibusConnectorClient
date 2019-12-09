package eu.domibus.connector.client.transport;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service //client lib should not create spring services!
public class TransportMessagesFromNationalToConnectorService {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TransportMessagesFromNationalToConnectorService.class);

    @Autowired
    private DomibusConnectorClientService clientService;

    @Autowired
    private DomibusConnectorNationalBackendClient nationalBackendClient;


    public void submitMessageFromNationalToConnector() throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        List<DomibusConnectorMessageType> messages = null;

        messages = nationalBackendClient.checkForMessagesOnNationalBackend();

        if (!CollectionUtils.isEmpty(messages)) {
            LOGGER.debug("{} new messages from national backend to submit to connector...", messages.size());
            for (DomibusConnectorMessageType message : messages) {
                try {
                    clientService.submitMessageToConnector(message);
                } catch (DomibusConnectorClientException e) {
                    LOGGER.error("Exception submitting message to connector: ", e);
                }
            }
        }
    }


}

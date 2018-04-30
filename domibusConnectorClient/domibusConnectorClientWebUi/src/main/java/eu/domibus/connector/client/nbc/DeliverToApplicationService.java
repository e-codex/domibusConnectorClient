package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.storage.MessageStorageService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.tools.TransitionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class DeliverToApplicationService implements DomibusConnectorNationalBackendClientDelivery {

    @Autowired
    NationalMessageIdGeneratorImpl idGenerator;

    @Autowired
    MessageStorageService messageStorageService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void processMessageFromConnector(DomibusConnectorMessageType message) throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        if (TransitionHelper.isConfirmationMessage(message)) {
            processConfirmationMessage(message);
        } else {
            processBusinessMessage(message);
        }
    }

    private void processConfirmationMessage(DomibusConnectorMessageType message) {
        //TODO:!
    }

    private void processBusinessMessage(DomibusConnectorMessageType message) {
        String appId = idGenerator.generateNationalId();
        message.getMessageDetails().setBackendMessageId(appId);
        messageStorageService.saveMessage(message);
        //inform ui!
        applicationEventPublisher.publishEvent(message);
    }

}

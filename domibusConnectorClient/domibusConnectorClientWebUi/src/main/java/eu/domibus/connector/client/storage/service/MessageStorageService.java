package eu.domibus.connector.client.storage.service;

import eu.domibus.connector.client.storage.entity.BusinessMessage;
import eu.domibus.connector.client.storage.entity.Confirmation;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

import java.util.List;
import java.util.Optional;

public interface MessageStorageService {

    /**
     * throws exception if national messageid is not set or a duplicate!
     * @param messageType
     */
    public void saveMessage(BusinessMessage messageType);

    public Optional<BusinessMessage> loadMessage(String nationalMessageId);

    public List<BusinessMessage> loadMessages();

    public void addConfirmation(String nationalMessageId, Confirmation confirmation);

}

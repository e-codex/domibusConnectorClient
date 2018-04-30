package eu.domibus.connector.client.storage;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

import java.util.List;

public interface MessageStorageService {

    /**
     * throws exception if national messageid is not set or a duplicate!
     * @param messageType
     */
    public void saveMessage(DomibusConnectorMessageType messageType);

    public DomibusConnectorMessageType loadMessage(String nationalMessageId);

    public List<DomibusConnectorMessageType> loadMessages();

}

package eu.domibus.connector.client.events;

import eu.domibus.connector.client.storage.entity.BusinessMessage;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class BusinessMessageReceivedEvent {

    private BusinessMessage message;

    public BusinessMessage getMessage() {
        return message;
    }

    public void setMessage(BusinessMessage message) {
        this.message = message;
    }
}

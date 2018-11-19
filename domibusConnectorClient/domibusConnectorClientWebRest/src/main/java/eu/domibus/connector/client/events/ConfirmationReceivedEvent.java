package eu.domibus.connector.client.events;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;

public class ConfirmationReceivedEvent {

    private DomibusConnectorMessageConfirmationType confirmationType;

    private String nationalMessageId;

    public DomibusConnectorMessageConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(DomibusConnectorMessageConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public String getNationalMessageId() {
        return nationalMessageId;
    }

    public void setNationalMessageId(String nationalMessageId) {
        this.nationalMessageId = nationalMessageId;
    }
}

package eu.domibus.connector.client.events;

import eu.domibus.connector.client.storage.entity.Confirmation;

public class TriggerConfirmationEvent {

    private String nationalMessageId;

    private Confirmation.ConfirmationType confirmationType;

    public String getNationalMessageId() {
        return nationalMessageId;
    }

    public void setNationalMessageId(String nationalMessageId) {
        this.nationalMessageId = nationalMessageId;
    }

    public Confirmation.ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(Confirmation.ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }
}

package eu.domibus.connector.client.events;

public class TriggerMessageSendEvent {

    String nationalMessageId;

    /**
     * Should the ids be updated if null?
     *  - backendMessageId
     *
     */
    boolean idUpdate = true;

    public String getNationalMessageId() {
        return nationalMessageId;
    }

    public void setNationalMessageId(String nationalMessageId) {
        this.nationalMessageId = nationalMessageId;
    }

    public boolean isIdUpdate() {
        return idUpdate;
    }

    public void setIdUpdate(boolean idUpdate) {
        this.idUpdate = idUpdate;
    }
}

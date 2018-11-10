package eu.domibus.connector.client.storage.entity;

import javax.persistence.*;

@Entity
public class Confirmation {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne
    private Transport transport;

    private byte[] confirmationXml;

    private ConfirmationType confirmationType;

    @ManyToOne(optional = false)
    private BusinessMessage businessMessage;

    public byte[] getConfirmationXml() {
        return confirmationXml;
    }

    public void setConfirmationXml(byte[] confirmationXml) {
        this.confirmationXml = confirmationXml;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BusinessMessage getBusinessMessage() {
        return businessMessage;
    }

    public void setBusinessMessage(BusinessMessage businessMessage) {
        this.businessMessage = businessMessage;
    }

    public static enum ConfirmationType {

        SUBMISSION_ACCEPTANCE,
        SUBMISSION_REJECTION,
        RELAY_REMMD_ACCEPTANCE,
        RELAY_REMMD_REJECTION,
        RELAY_REMMD_FAILURE,
        DELIVERY,
        NON_DELIVERY,
        RETRIEVAL,
        NON_RETRIEVAL;

        public String value() {
            return name();
        }

        public static ConfirmationType fromValue(String v) {
            return valueOf(v);
        }

    }


}

package eu.domibus.connector.client.storage.entity;

import javax.persistence.*;

@Entity
public class Confirmation {

    @GeneratedValue
    @Id
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Transport transport;

    @Column(name = "CONFIRMATION_XML")
    @Lob
    private String confirmationXml;

    @Column(name = "CONFIRMATION_TYPE")
    private ConfirmationType confirmationType;

    @ManyToOne(optional = false)
    @JoinColumn(name="BUSINESS_MESSAGE_MESSAGE_ID", referencedColumnName = "ID")
    private BusinessMessage businessMessage;

    public String getConfirmationXml() {
        return confirmationXml;
    }

    public void setConfirmationXml(String confirmationXml) {
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

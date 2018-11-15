package eu.domibus.connector.client.storage.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BUSINESS_MESSAGE")
public class BusinessMessage {

    @Id
    @TableGenerator(name = "seqStoreBusinessMessage", table = "HIBERNATE_SEQ_TABLE", pkColumnName = "SEQ_NAME", pkColumnValue = "BUSINESS_MESSAGE.ID", valueColumnName = "SEQ_VALUE", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqStoreBusinessMessage")
    @Column(name = "ID")
    private Long messageId;

    @OneToOne(cascade = CascadeType.ALL, optional = true)
    private Transport transport;

    //business xml
    private byte[] businessXml;

    //business document
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "BUSINESS_ATTACHMENT_ID", referencedColumnName = "ID")
    private Attachment businessAttachment;

    //extra attachments
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BUSINESS_MESSAGE_ATTACHMENTS",
            joinColumns = @JoinColumn(name = "BUSINESS_MESSAGE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "ATTACHMENT_ID", referencedColumnName = "ID")
    )
    private List<Attachment> attachments = new ArrayList<>();

    //confirmations
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "BUSINESS_MESSAGE_CONFIRMATIONS",
            joinColumns = @JoinColumn(name = "BUSINESS_MESSAGE_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "CONFIRMATION_ID", referencedColumnName = "ID")
    )
    private List<Confirmation> confirmations = new ArrayList<>();

    @PrePersist
    public void prePersiste() {
        confirmations.stream().forEach( c -> c.setBusinessMessage(this));
    }

    @PreUpdate
    public void preUpdate() {
        confirmations.stream().forEach( c -> c.setBusinessMessage(this));
    }

    public byte[] getBusinessXml() {
        return businessXml;
    }

    public void setBusinessXml(byte[] businessXml) {
        this.businessXml = businessXml;
    }

    public Attachment getBusinessAttachment() {
        return businessAttachment;
    }

    public void setBusinessAttachment(Attachment businessAttachment) {
        this.businessAttachment = businessAttachment;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Confirmation> getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(List<Confirmation> confirmations) {
        this.confirmations = confirmations;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }
}

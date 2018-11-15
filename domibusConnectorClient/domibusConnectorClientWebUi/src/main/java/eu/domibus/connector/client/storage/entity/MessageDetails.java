package eu.domibus.connector.client.storage.entity;

import javax.persistence.*;

@Entity
@Table(name = "MESSAGE_DETAILS")
public class MessageDetails {

    @Id
    @TableGenerator(name = "seqStoreMessageDetails", table = "HIBERNATE_SEQ_TABLE", pkColumnName = "SEQ_NAME", pkColumnValue = "MESSAGE_DETAILS.ID", valueColumnName = "SEQ_VALUE", initialValue = 1000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seqStoreMessageDetails")
    @Column(name = "ID")
    private Long id;

    @Column(name = "NATIONAL_MESSAGE_ID")
    private String backendMessageId;

    @Column(name = "EBMS_MESSAGE_ID")
    private String ebmsMessageId;

    @Column(name = "REF_TO_MESSAGE_ID")
    private String refToMessageId;

    @Column(name = "CONVERSATION_ID")
    private String conversationId;

    @Column(name = "ORIGINAL_SENDER")
    private String originalSender;

    @Column(name = "FINAL_RECIPIENT")
    private String finalRecipient;

    @Column(name = "SERVICE_NAME")
    private String serviceName;

    @Column(name = "ACTION_NAME")
    private String actionName;

    @Column(name = "FROM_PARTY_ID")
    private String fromPartyId;

    @Column(name = "TO_PARTY_ID")
    private String toPartyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackendMessageId() {
        return backendMessageId;
    }

    public void setBackendMessageId(String backendMessageId) {
        this.backendMessageId = backendMessageId;
    }

    public String getEbmsMessageId() {
        return ebmsMessageId;
    }

    public void setEbmsMessageId(String ebmsMessageId) {
        this.ebmsMessageId = ebmsMessageId;
    }

    public String getRefToMessageId() {
        return refToMessageId;
    }

    public void setRefToMessageId(String refToMessageId) {
        this.refToMessageId = refToMessageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getOriginalSender() {
        return originalSender;
    }

    public void setOriginalSender(String originalSender) {
        this.originalSender = originalSender;
    }

    public String getFinalRecipient() {
        return finalRecipient;
    }

    public void setFinalRecipient(String finalRecipient) {
        this.finalRecipient = finalRecipient;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getFromPartyId() {
        return fromPartyId;
    }

    public void setFromPartyId(String fromPartyId) {
        this.fromPartyId = fromPartyId;
    }

    public String getToPartyId() {
        return toPartyId;
    }

    public void setToPartyId(String toPartyId) {
        this.toPartyId = toPartyId;
    }
}

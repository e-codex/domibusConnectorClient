package eu.domibus.connector.client.controller.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "CONNECTOR_CLIENT_MESSAGE")
public class PDomibusConnectorClientMessage {

	@Id
    @Column(name="ID")
	@SequenceGenerator(name = "clientMessageSeqGen", sequenceName = "clientMessageSeq", initialValue = 5, allocationSize = 1)
    @GeneratedValue(generator = "clientMessageSeqGen")
	private long id;
	
	@Column(name = "EBMS_MESSAGE_ID", length = 255)
    private String ebmsMessageId;
	
	@Column(name = "BACKEND_MESSAGE_ID", unique = true, length = 255)
    private String backendMessageId;
	
	@Column(name = "CONVERSATION_ID", length = 255)
    private String conversationId;
	
	@Column(name = "FROM_PARTY_ID", length = 255)
    private String fromPartyId;
	
	@Column(name = "FROM_PARTY_TYPE", length = 255)
    private String fromPartyType;
	
	@Column(name = "FROM_PARTY_ROLE", length = 255)
    private String fromPartyRole;
	
	@Column(name = "TO_PARTY_ID", length = 255)
    private String toPartyId;
	
	@Column(name = "TO_PARTY_TYPE", length = 255)
    private String toPartyType;
	
	@Column(name = "TO_PARTY_ROLE", length = 255)
    private String toPartyRole;
	
	@Column(name = "STORAGE_STATUS", length = 255)
    private String storageStatus;
	
	@Column(name = "STORAGE_INFO", length = 255)
    private String storageInfo;
	
	@Column(name = "LAST_CONFIRMATION_RECEIVED", length = 255)
    private String lastConfirmationReceived;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "UPDATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	public PDomibusConnectorClientMessage() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEbmsMessageId() {
		return ebmsMessageId;
	}

	public void setEbmsMessageId(String ebmsMessageId) {
		this.ebmsMessageId = ebmsMessageId;
	}

	public String getBackendMessageId() {
		return backendMessageId;
	}

	public void setBackendMessageId(String backendMessageId) {
		this.backendMessageId = backendMessageId;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getFromPartyId() {
		return fromPartyId;
	}

	public void setFromPartyId(String fromPartyId) {
		this.fromPartyId = fromPartyId;
	}

	public String getFromPartyType() {
		return fromPartyType;
	}

	public void setFromPartyType(String fromPartyType) {
		this.fromPartyType = fromPartyType;
	}

	public String getFromPartyRole() {
		return fromPartyRole;
	}

	public void setFromPartyRole(String fromPartyRole) {
		this.fromPartyRole = fromPartyRole;
	}

	public String getToPartyId() {
		return toPartyId;
	}

	public void setToPartyId(String toPartyId) {
		this.toPartyId = toPartyId;
	}

	public String getToPartyType() {
		return toPartyType;
	}

	public void setToPartyType(String toPartyType) {
		this.toPartyType = toPartyType;
	}

	public String getToPartyRole() {
		return toPartyRole;
	}

	public void setToPartyRole(String toPartyRole) {
		this.toPartyRole = toPartyRole;
	}

	public String getStorageStatus() {
		return storageStatus;
	}

	public void setStorageStatus(String storageStatus) {
		this.storageStatus = storageStatus;
	}

	public String getStorageInfo() {
		return storageInfo;
	}

	public void setStorageInfo(String storageInfo) {
		this.storageInfo = storageInfo;
	}

	public String getLastConfirmationReceived() {
		return lastConfirmationReceived;
	}

	public void setLastConfirmationReceived(String lastConfirmationReceived) {
		this.lastConfirmationReceived = lastConfirmationReceived;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

}

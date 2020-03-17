package eu.domibus.connector.client.controller.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "CONNECTOR_CLIENT_CONFIRMATION")
public class PDomibusConnectorClientConfirmation {

	@Id
    @Column(name="ID")
	@SequenceGenerator(name = "clientConfirmationSeqGen", sequenceName = "clientConfirmationSeq", initialValue = 5, allocationSize = 1)
    @GeneratedValue(generator = "clientConfirmationSeqGen")
	private long id;
	
	@ManyToOne
    @JoinColumn(name = "MESSAGE_ID", nullable = false)
    private PDomibusConnectorClientMessage message;
	
	@Column(name = "CONFIRMATION_TYPE", length = 255, nullable = false)
    private String confirmationType;
	
	@Column(name = "RECEIVED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date received;
	
	@Column(name = "STORAGE_STATUS", length = 255)
    private String storageStatus;
	
	@Column(name = "STORAGE_INFO", length = 255)
    private String storageInfo;
	
	public PDomibusConnectorClientConfirmation() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PDomibusConnectorClientMessage getMessage() {
		return message;
	}

	public void setMessage(PDomibusConnectorClientMessage message) {
		this.message = message;
	}

	public String getConfirmationType() {
		return confirmationType;
	}

	public void setConfirmationType(String confirmationType) {
		this.confirmationType = confirmationType;
	}

	public Date getReceived() {
		return received;
	}

	public void setReceived(Date received) {
		this.received = received;
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
	
	

}

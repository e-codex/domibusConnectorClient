package eu.domibus.connector.client.rest.model;

import java.util.Date;

public class DomibusConnectorClientConfirmation {

	private long id;
	
	private String confirmationType;
	
	private Date received;
	
	private String storageStatus;
	
	private String storageInfo;
	
	public DomibusConnectorClientConfirmation() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

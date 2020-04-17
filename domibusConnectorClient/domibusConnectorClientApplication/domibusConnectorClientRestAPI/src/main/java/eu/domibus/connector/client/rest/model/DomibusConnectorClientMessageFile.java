package eu.domibus.connector.client.rest.model;

import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;

public class DomibusConnectorClientMessageFile {

	private String fileName;
	
	private DomibusConnectorClientMessageFileType fileType;
	
	private String storageLocation;
	
	public DomibusConnectorClientMessageFile() {
		// TODO Auto-generated constructor stub
	}
	
	public DomibusConnectorClientMessageFile(String name, DomibusConnectorClientMessageFileType type) {
		this.fileName = name;
		this.setFileType(type);
	}
	
	public DomibusConnectorClientMessageFile(String name, DomibusConnectorClientMessageFileType type, String storageLocation) {
		this.fileName = name;
		this.setFileType(type);
		this.setStorageLocation(storageLocation);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public DomibusConnectorClientMessageFileType getFileType() {
		return fileType;
	}

	public void setFileType(DomibusConnectorClientMessageFileType fileType) {
		this.fileType = fileType;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	

}

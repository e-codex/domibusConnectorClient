package eu.domibus.connector.client.rest.model;

public class DomibusConnectorClientMessageFile {

	private String fileName;
	
	private String fileType;
	
	public DomibusConnectorClientMessageFile() {
		// TODO Auto-generated constructor stub
	}
	
	public DomibusConnectorClientMessageFile(String name, String type) {
		this.fileName = name;
		this.setFileType(type);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	

}

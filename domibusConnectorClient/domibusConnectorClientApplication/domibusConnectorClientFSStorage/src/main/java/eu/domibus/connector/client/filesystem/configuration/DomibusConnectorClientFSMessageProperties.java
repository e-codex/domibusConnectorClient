package eu.domibus.connector.client.filesystem.configuration;

public class DomibusConnectorClientFSMessageProperties {

	private String fileName;
    private String service;
    private String action;
    private String toPartyRole;
    private String toPartyId;
    private String fromPartyRole;
    private String fromPartyId;
    private String originalSender;
    private String finalRecipient;
//    private String nationalMessageId;
    private String backendMessageId;
    private String ebmsMessageId;
    private String conversationId;
    private String contentPdfFileName;
    private String contentXmlFileName;
    private String detachedSignatureFileName;
    private String messageReceivedDatetime;
    private String messageSentDatetime;
    
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getToPartyRole() {
		return toPartyRole;
	}
	public void setToPartyRole(String toPartyRole) {
		this.toPartyRole = toPartyRole;
	}
	public String getToPartyId() {
		return toPartyId;
	}
	public void setToPartyId(String toPartyId) {
		this.toPartyId = toPartyId;
	}
	public String getFromPartyRole() {
		return fromPartyRole;
	}
	public void setFromPartyRole(String fromPartyRole) {
		this.fromPartyRole = fromPartyRole;
	}
	public String getFromPartyId() {
		return fromPartyId;
	}
	public void setFromPartyId(String fromPartyId) {
		this.fromPartyId = fromPartyId;
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
//	public String getNationalMessageId() {
//		return nationalMessageId;
//	}
//	public void setNationalMessageId(String nationalMessageId) {
//		this.nationalMessageId = nationalMessageId;
//	}
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
	public String getConversationId() {
		return conversationId;
	}
	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}
	public String getContentPdfFileName() {
		return contentPdfFileName;
	}
	public void setContentPdfFileName(String contentPdfFileName) {
		this.contentPdfFileName = contentPdfFileName;
	}
	public String getContentXmlFileName() {
		return contentXmlFileName;
	}
	public void setContentXmlFileName(String contentXmlFileName) {
		this.contentXmlFileName = contentXmlFileName;
	}
	public String getDetachedSignatureFileName() {
		return detachedSignatureFileName;
	}
	public void setDetachedSignatureFileName(String detachedSignatureFileName) {
		this.detachedSignatureFileName = detachedSignatureFileName;
	}
	public String getMessageReceivedDatetime() {
		return messageReceivedDatetime;
	}
	public void setMessageReceivedDatetime(String messageReceivedDatetime) {
		this.messageReceivedDatetime = messageReceivedDatetime;
	}
	public String getMessageSentDatetime() {
		return messageSentDatetime;
	}
	public void setMessageSentDatetime(String messageSentDatetime) {
		this.messageSentDatetime = messageSentDatetime;
	}
	
    
}

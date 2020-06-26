package eu.domibus.connector.client.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSMessageProperties;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSProperties;
import eu.domibus.connector.client.filesystem.message.FSMessageDetails;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
//@ConfigurationProperties(prefix = DomibusConnectorClientFSStorageConfiguration.PREFIX)
//@PropertySource("classpath:/connector-client-fs-storage-default.properties")
@Validated
@Valid
public class DomibusConnectorClientFileSystemWriter {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientFileSystemWriter.class);

	@Autowired
	private DomibusConnectorClientFSMessageProperties messageProperties;
	
	@Autowired
	private DomibusConnectorClientFSProperties properties;


	public void writeConfirmationToFileSystem(DomibusConnectorMessageType confirmationMessage, File messageFolder ) throws DomibusConnectorClientFileSystemException {
		DomibusConnectorMessageConfirmationType confirmation = confirmationMessage.getMessageConfirmations().get(0);
		String type = confirmation.getConfirmationType().name();
		
		String path = messageFolder.getAbsolutePath() + File.separator + type + properties.getXmlFileExtension();;

		LOGGER.debug("Create confirmation file {}", path);
		File evidenceXml = new File(path);
		try {
			byte[] xmlBytes = sourceToByteArray(confirmation.getConfirmation());
			byteArrayToFile(xmlBytes, evidenceXml);
		} catch (IOException e) {
			throw new DomibusConnectorClientFileSystemException("Could not create file "
					+ evidenceXml.getAbsolutePath(), e);
		}
	}
	
	public void writeMessageFileToFileSystem(File messageFolder, String fileName, DomibusConnectorClientMessageFileType fileType, byte[] fileContent) throws DomibusConnectorClientFileSystemException {
		
		
		FSMessageDetails messageDetails = DomibusConnectorClientFileSystemUtil.loadMessageProperties(messageFolder, this.messageProperties.getFileName());
		
		switch(fileType) {
		case BUSINESS_CONTENT:messageDetails.getMessageDetails().put(messageProperties.getContentXmlFileName(),fileName);break;
		case BUSINESS_DOCUMENT:messageDetails.getMessageDetails().put(messageProperties.getContentPdfFileName(),fileName);break;
		case DETACHED_SIGNATURE:messageDetails.getMessageDetails().put(messageProperties.getDetachedSignatureFileName(),fileName);break;
		default:
		}
		
		File messagePropertiesFile = new File(messageFolder, this.messageProperties.getFileName());
		DomibusConnectorClientFileSystemUtil.storeMessagePropertiesToFile(messageDetails, messagePropertiesFile);
		
		try {
			createFile(messageFolder, fileName, fileContent);
		} catch (IOException e1) {
			throw new DomibusConnectorClientFileSystemException("Could not create file "+fileName+" at message folder "
					+ messageFolder.getAbsolutePath(), e1);
		}
		
	}
	
	public void deleteMessageFileFromFileSystem(File messageFolder, String fileName, DomibusConnectorClientMessageFileType fileType) throws DomibusConnectorClientFileSystemException {
		
		FSMessageDetails messageDetails = DomibusConnectorClientFileSystemUtil.loadMessageProperties(messageFolder, this.messageProperties.getFileName());
		
		switch(fileType) {
		case BUSINESS_CONTENT:messageDetails.getMessageDetails().remove(messageProperties.getContentXmlFileName(),fileName);break;
		case BUSINESS_DOCUMENT:messageDetails.getMessageDetails().remove(messageProperties.getContentPdfFileName(),fileName);break;
		case DETACHED_SIGNATURE:messageDetails.getMessageDetails().remove(messageProperties.getDetachedSignatureFileName(),fileName);break;
		default:
		}
		
		File messagePropertiesFile = new File(messageFolder, this.messageProperties.getFileName());
		DomibusConnectorClientFileSystemUtil.storeMessagePropertiesToFile(messageDetails, messagePropertiesFile);
		
		deleteFile(messageFolder, fileName);
		
	}
	
	public String updateMessageAtStorageToSent(File messageFolder) throws DomibusConnectorClientFileSystemException {
		
		FSMessageDetails messageDetails = DomibusConnectorClientFileSystemUtil.loadMessageProperties(messageFolder, this.messageProperties.getFileName());
		
		messageDetails.getMessageDetails().put(messageProperties.getMessageSentDatetime(),DomibusConnectorClientFileSystemUtil.convertDateToProperty(new Date()));
		
		File messagePropertiesFile = new File(messageFolder, this.messageProperties.getFileName());
		DomibusConnectorClientFileSystemUtil.storeMessagePropertiesToFile(messageDetails, messagePropertiesFile);
		
		if(messageFolder.getAbsolutePath().endsWith(properties.getMessageReadyPostfix())) {
			String newFolderName = messageFolder.getAbsolutePath().substring(0, messageFolder.getAbsolutePath().length() - properties.getMessageReadyPostfix().length());
			File newMessageFolder = new File(newFolderName);
			messageFolder.renameTo(newMessageFolder);
			return newFolderName;
		}
		return messageFolder.getAbsolutePath();
	}

	public String writeMessageToFileSystem(DomibusConnectorMessageType message, File messagesDir) throws DomibusConnectorClientFileSystemException {
		File messageFolder = createMessageFolder(message, messagesDir, true);

		LOGGER.debug("Write new message into folder {}", messageFolder.getAbsolutePath());

		Date messageReceived = new Date();

		FSMessageDetails msgProps = null;
		File messagePropertiesFile = null;
		String action = null;
		if (message.getMessageDetails() != null) {
			if (message.getMessageDetails().getAction() != null)
				action = message.getMessageDetails().getAction().getAction();
			String messagePropertiesPath = messageFolder.getAbsolutePath() + File.separator + messageProperties.getFileName();

			messagePropertiesFile = new File(messagePropertiesPath);
			msgProps = convertMessageDetailsToMessageProperties(message
					.getMessageDetails(), messageReceived);
		}

		DomibusConnectorMessageContentType messageContent = message.getMessageContent();
		if (messageContent != null) {
			if (messageContent.getDocument() != null) {
				String fileName = null;
				if (StringUtils.hasText(messageContent.getDocument().getDocumentName())) {
					fileName = messageContent.getDocument().getDocumentName();
				} else {
					fileName = action != null ? action + "." + properties.getPdfFileExtension()
							: properties.getDefaultPdfFileName();

				}
				msgProps.getMessageDetails().put(messageProperties.getContentPdfFileName(), fileName);
				byte[] document;
				try {
					document = dataHandlerToBytes(messageContent.getDocument().getDocument());
					createFile(messageFolder, fileName, document);
				} catch (IOException e) {
					LOGGER.error("Could not process business document file {} at messageFolder {}", fileName, messageFolder.getAbsolutePath(), e);
//					throw new DomibusConnectorClientFileSystemException("Could not process document! ", e);
				}
				DomibusConnectorDetachedSignatureType detachedSignature = messageContent.getDocument().getDetachedSignature();
				if (detachedSignature != null && detachedSignature.getDetachedSignature()!=null && detachedSignature.getDetachedSignature().length > 0
						&& detachedSignature.getMimeType() != null) {
					String fileName2 = null;
					if (StringUtils.hasText(detachedSignature.getDetachedSignatureName())
							&& !detachedSignature.getDetachedSignatureName().equals(
									properties.getDefaultDetachedSignatureFileName())) {
						fileName2 = detachedSignature.getDetachedSignatureName();
					} else {
						fileName2 = properties.getDefaultDetachedSignatureFileName();
						if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.XML))
							fileName2 += properties.getXmlFileExtension();
						else if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.PKCS_7))
							fileName2 += properties.getPkcs7FileExtension();

					}
					msgProps.getMessageDetails().put(messageProperties.getDetachedSignatureFileName(), fileName2);
					try {
						createFile(messageFolder, fileName2, detachedSignature.getDetachedSignature());
					} catch (IOException e) {
						LOGGER.error("Could not process detached signature file {} at messageFolder {}", fileName2, messageFolder.getAbsolutePath(), e);
//						throw new DomibusConnectorClientFileSystemException("Could not process document! ", e);
					}
				}
			}
			if (messageContent.getXmlContent() != null){
				String fileName = action != null ? action + properties.getXmlFileExtension()
						: properties.getDefaultXmlFileName();
				try {
					byte[] content = sourceToByteArray(messageContent.getXmlContent());
					msgProps.getMessageDetails().put(messageProperties.getContentXmlFileName(),fileName);
					createFile(messageFolder, fileName, content);
				}catch(DomibusConnectorClientFileSystemException | IOException e) {
					LOGGER.error("Could not process business content file {} at messageFolder {}", fileName, messageFolder.getAbsolutePath(), e);
				}
			}
		}
		LOGGER.debug("Store message properties to file {}", messagePropertiesFile.getAbsolutePath());
		DomibusConnectorClientFileSystemUtil.storeMessagePropertiesToFile(msgProps, messagePropertiesFile);

		if (message.getMessageAttachments() != null) {
			for (DomibusConnectorMessageAttachmentType attachment : message.getMessageAttachments()) {
				try {
					byte[] attachmentBytes = dataHandlerToBytes(attachment.getAttachment());
					createFile(messageFolder, attachment.getName(), attachmentBytes);
				} catch (IOException e) {
					LOGGER.error("Could not process business attachment file {} at messageFolder {}", attachment.getName(), messageFolder.getAbsolutePath(), e);
				}
			}
		}

		if (message.getMessageConfirmations() != null) {
			for (DomibusConnectorMessageConfirmationType confirmation : message.getMessageConfirmations()) {
				String fileName = confirmation.getConfirmationType().name()
						+ properties.getXmlFileExtension();
				try {
					byte[] confirmationBytes = sourceToByteArray(confirmation.getConfirmation());
					createFile(messageFolder, fileName, confirmationBytes);
				} catch (IOException e) {
					LOGGER.error("Could not process confirmation file {} at messageFolder {}",fileName, messageFolder.getAbsolutePath(), e);
				}
			}
		}

		return messageFolder.getAbsolutePath();
	}
	
	public void deleteFromStorage(File messageFolder) throws DomibusConnectorClientFileSystemException {
		
		try {
			deleteDirectory(messageFolder);
		} catch (Exception e) {
			throw new DomibusConnectorClientFileSystemException("Message folder and/or its contents could not be deleted! ", e);
		}
		
		
		
	}
	
	private void deleteDirectory(File directory) throws Exception {
		List<File> filesToBeDeleted = new ArrayList<File>();
		
		for (File sub : directory.listFiles()) {
			filesToBeDeleted.add(sub);
		}
		
		if(!filesToBeDeleted.isEmpty()) {
			for(File sub: filesToBeDeleted) {
				if(sub.isDirectory()) 
					deleteDirectory(sub);
				else
					sub.delete();
			}
		}
		
		directory.delete();
	}

	private FSMessageDetails convertMessageDetailsToMessageProperties(
			DomibusConnectorMessageDetailsType messageDetails, Date messageReceived) {

		FSMessageDetails msgDetails = new FSMessageDetails();
		if (StringUtils.hasText(messageDetails.getEbmsMessageId())) {
			msgDetails.getMessageDetails().put(messageProperties.getEbmsMessageId(), messageDetails.getEbmsMessageId());
		}
		if (StringUtils.hasText(messageDetails.getBackendMessageId())) {
			msgDetails.getMessageDetails().put(messageProperties.getBackendMessageId(), messageDetails.getBackendMessageId());
		}
		if (StringUtils.hasText(messageDetails.getConversationId())) {
			msgDetails.getMessageDetails().put(messageProperties.getConversationId(), messageDetails.getConversationId());
		}
		if(messageDetails.getToParty()!=null) {
			msgDetails.getMessageDetails().put(messageProperties.getToPartyId(), messageDetails.getToParty().getPartyId());
			msgDetails.getMessageDetails().put(messageProperties.getToPartyRole(), messageDetails.getToParty().getRole());
		}
		if(messageDetails.getFromParty()!=null) {
			msgDetails.getMessageDetails().put(messageProperties.getFromPartyId(), messageDetails.getFromParty().getPartyId());
			msgDetails.getMessageDetails().put(messageProperties.getFromPartyRole(), messageDetails.getFromParty().getRole());
		}
		msgDetails.getMessageDetails().put(messageProperties.getFinalRecipient(), messageDetails.getFinalRecipient());
		msgDetails.getMessageDetails().put(messageProperties.getOriginalSender(), messageDetails.getOriginalSender());
		if(messageDetails.getAction()!=null)
			msgDetails.getMessageDetails().put(messageProperties.getAction(), messageDetails.getAction().getAction());
		if(messageDetails.getService()!=null)
			msgDetails.getMessageDetails().put(messageProperties.getService(), messageDetails.getService().getService());
		if(messageReceived!=null)
			msgDetails.getMessageDetails().put(messageProperties.getMessageReceivedDatetime(), DomibusConnectorClientFileSystemUtil.convertDateToProperty(messageReceived));

		return msgDetails;
	}

	private void createFile(File messageFolder, String fileName, byte[] content)
			throws IOException {
		String filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
		File file = new File(filePath);
		if(!file.exists()) {
			LOGGER.debug("Create file {}", filePath);
			byteArrayToFile(content, file);
		}
	}
	
	private void deleteFile(File messageFolder, String fileName) throws DomibusConnectorClientFileSystemException {
		String filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
		File file = new File(filePath);
		if(file.exists()) {
			LOGGER.debug("delete file {}", filePath);
			if(!file.delete()){
				throw new DomibusConnectorClientFileSystemException("File "+file.getAbsolutePath()+" could not be deleted!");
			}
		}
	}

	private void byteArrayToFile(byte[] data, File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(data);
		fos.flush();
		fos.close();
	}

	private byte[] dataHandlerToBytes(DataHandler dh) throws IOException {
		InputStream is = dh.getInputStream();
		byte[] b = new byte[is.available()];
		is.read(b);
		return b;
	}

	private @NotNull byte[] sourceToByteArray(@NotNull Source xmlInput) throws DomibusConnectorClientFileSystemException {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult xmlOutput = new StreamResult(new OutputStreamWriter(output));
			transformer.transform(xmlInput, xmlOutput);
			return output.toByteArray();
		} catch (IllegalArgumentException | TransformerException e) {
			throw new DomibusConnectorClientFileSystemException("Exception occured during transforming xml into byte[]", e);
		}
	}

	private File createMessageFolder(DomibusConnectorMessageType message, File messagesDir, boolean createIfNonExistent) throws DomibusConnectorClientFileSystemException {

		String messageId = message.getMessageDetails().getEbmsMessageId()!=null && !message.getMessageDetails().getEbmsMessageId().isEmpty()?message.getMessageDetails().getEbmsMessageId():message.getMessageDetails().getBackendMessageId();
		String pathname = new StringBuilder()
				.append(messagesDir.getAbsolutePath())
				.append(File.separator)
				.append(DomibusConnectorClientFileSystemUtil.getMessageFolderName(message, messageId ))
				.toString();
		File messageFolder = new File(pathname);
		if (createIfNonExistent && (!messageFolder.exists() || !messageFolder.isDirectory())) {
			LOGGER.debug("Message folder {} does not exist. Create folder!",
					messageFolder.getAbsolutePath());
			if (!messageFolder.mkdir()) {
				throw new DomibusConnectorClientFileSystemException(
						"Message folder cannot be created!");
			}
		}

		return messageFolder;
	}

//	public DomibusConnectorClientFSMessageProperties getMessageProperties() {
//		return messageProperties;
//	}
//
//	public void setMessageProperties(DomibusConnectorClientFSMessageProperties messageProperties) {
//		this.messageProperties = messageProperties;
//	}

	




}

package eu.domibus.connector.client.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.activation.DataHandler;
import javax.validation.constraints.NotNull;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSMessageProperties;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;
import eu.domibus.connector.client.filesystem.message.FSMessageDetails;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@ConfigurationProperties(prefix = DomibusConnectorClientFSStorageConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-fs-storage-default.properties")
public class DomibusConnectorClientFileSystemWriter {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientFileSystemWriter.class);
	
	@NotNull
	private DomibusConnectorClientFSMessageProperties messageProperties;
	
	@NotNull
	private String pdfFileExtension;
	
	@NotNull
	private String xmlFileExtension;
	
	@NotNull
	private String pkcs7FileExtension;
	
	@NotNull
	private String defaultPdfFileName;
	
	@NotNull
	private String defaultXmlFileName;
	
	@NotNull
	private String defaultDetachedSignatureFileName;
	
	@NotNull
	private String messageSentPostfix;
	
	public void writeConfirmationToFileSystem(DomibusConnectorMessageType confirmationMessage, File incomingMessagesDir, File outgoingMessagesDir ) throws DomibusConnectorClientFileSystemException {
		DomibusConnectorMessageConfirmationType confirmation = confirmationMessage.getMessageConfirmations().get(0);
		String type = confirmation.getConfirmationType().name();

		String backendMessageId = confirmationMessage.getMessageDetails().getBackendMessageId();
		File messageFolder = null;

		if(!StringUtils.isEmpty(backendMessageId)) {
			messageFolder = new File(outgoingMessagesDir + File.separator
					+ backendMessageId
					+ messageSentPostfix);
			if (!messageFolder.exists() || !messageFolder.isDirectory()) {
				LOGGER.info("Message folder {} for outgoing message does not exist anymore. Create incoming!",
						messageFolder.getAbsolutePath());
				messageFolder = createIncomingMessageFolder(confirmationMessage, incomingMessagesDir);
			}
		} else {

			messageFolder = createIncomingMessageFolder(confirmationMessage, incomingMessagesDir);
			
		}

		String path = messageFolder.getAbsolutePath() + File.separator + type
				+ xmlFileExtension;
		LOGGER.debug("Create evidence xml file {}", path);
		File evidenceXml = new File(path);
		try {
			byte[] xmlBytes = sourceToByteArray(confirmation.getConfirmation());
			byteArrayToFile(xmlBytes, evidenceXml);
		} catch (IOException e) {
			throw new DomibusConnectorClientFileSystemException("Could not create file "
					+ evidenceXml.getAbsolutePath(), e);
		}
	}
	
	public String writeMessageToFileSystem(DomibusConnectorMessageType message, File incomingMessagesDir) throws DomibusConnectorClientFileSystemException {
		File messageFolder = createIncomingMessageFolder(message, incomingMessagesDir);
		
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
					fileName = action != null ? action + "." + pdfFileExtension
							: defaultPdfFileName;

				}
				msgProps.getMessageDetails().put(messageProperties.getContentPdfFileName(), fileName);
				byte[] document;
				try {
					document = dataHandlerToBytes(messageContent.getDocument().getDocument());
				} catch (IOException e) {
					throw new DomibusConnectorClientFileSystemException("Could not process document! ", e);
				}
				createFile(messageFolder, fileName, document);
				DomibusConnectorDetachedSignatureType detachedSignature = messageContent.getDocument().getDetachedSignature();
				if (detachedSignature != null && detachedSignature.getDetachedSignature()!=null && detachedSignature.getDetachedSignature().length > 0
						&& detachedSignature.getMimeType() != null) {
					String fileName2 = null;
					if (StringUtils.hasText(detachedSignature.getDetachedSignatureName())
							&& !detachedSignature.getDetachedSignatureName().equals(
									defaultDetachedSignatureFileName)) {
						fileName2 = detachedSignature.getDetachedSignatureName();
					} else {
						fileName2 = defaultDetachedSignatureFileName;
						if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.XML))
							fileName2 += xmlFileExtension;
						else if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.PKCS_7))
							fileName2 += pkcs7FileExtension;
						
					}
					msgProps.getMessageDetails().put(messageProperties.getDetachedSignatureFileName(), fileName2);
					createFile(messageFolder, fileName2, detachedSignature.getDetachedSignature());
				}
			}
			if (messageContent.getXmlContent() != null){
				byte[] content = sourceToByteArray(messageContent.getXmlContent());
				String fileName = action != null ? action + xmlFileExtension
						: defaultXmlFileName;
				msgProps.getMessageDetails().put(messageProperties.getContentXmlFileName(),fileName);
				createFile(messageFolder, fileName, content);
			}
		}
		LOGGER.debug("Store message properties to file {}", messagePropertiesFile.getAbsolutePath());
		DomibusConnectorClientFileSystemUtil.storeMessagePropertiesToFile(msgProps, messagePropertiesFile);

		if (message.getMessageAttachments() != null) {
			for (DomibusConnectorMessageAttachmentType attachment : message.getMessageAttachments()) {
				byte[] attachmentBytes;
				try {
					attachmentBytes = dataHandlerToBytes(attachment.getAttachment());
				} catch (IOException e) {
					throw new DomibusConnectorClientFileSystemException("Could not process attachment! ", e);
				}
				createFile(messageFolder, attachment.getName(), attachmentBytes);
			}
		}

		if (message.getMessageConfirmations() != null) {
			for (DomibusConnectorMessageConfirmationType confirmation : message.getMessageConfirmations()) {
				createFile(messageFolder, confirmation.getConfirmationType().name()
						+ xmlFileExtension, sourceToByteArray(confirmation.getConfirmation()));
			}
		}
		
		return messageFolder.getAbsolutePath();
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
		msgDetails.getMessageDetails().put(messageProperties.getToPartyId(), messageDetails.getToParty().getPartyId());
		msgDetails.getMessageDetails().put(messageProperties.getToPartyRole(), messageDetails.getToParty().getRole());
		msgDetails.getMessageDetails().put(messageProperties.getFromPartyId(), messageDetails.getFromParty().getPartyId());
		msgDetails.getMessageDetails().put(messageProperties.getFromPartyRole(), messageDetails.getFromParty().getRole());
		msgDetails.getMessageDetails().put(messageProperties.getFinalRecipient(), messageDetails.getFinalRecipient());
		msgDetails.getMessageDetails().put(messageProperties.getOriginalSender(), messageDetails.getOriginalSender());
		msgDetails.getMessageDetails().put(messageProperties.getAction(), messageDetails.getAction().getAction());
		msgDetails.getMessageDetails().put(messageProperties.getService(), messageDetails.getService().getService());
		if(messageReceived!=null)
			msgDetails.getMessageDetails().put(messageProperties.getMessageReceivedDatetime(), DomibusConnectorClientFileSystemUtil.convertDateToProperty(messageReceived));

		return msgDetails;
	}
	
	private void createFile(File messageFolder, String fileName, byte[] content)
			throws DomibusConnectorClientFileSystemException {
		String filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
		LOGGER.debug("#loadMessageProperties: Create file {}", filePath);
		File file = new File(filePath);
		try {
			byteArrayToFile(content, file);
		} catch (IOException e) {
			throw new DomibusConnectorClientFileSystemException("Could not create file " + file.getAbsolutePath(),
					e);
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

	private byte[] sourceToByteArray(Source xmlInput) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");    
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			StreamResult xmlOutput = new StreamResult(new OutputStreamWriter(output));
			transformer.transform(xmlInput, xmlOutput);            
			return output.toByteArray();
		} catch (IllegalArgumentException | TransformerException e) {
			throw new RuntimeException("Exception occured during transforming xml into byte[]", e);
		}
	}
	
	private File createIncomingMessageFolder(DomibusConnectorMessageType message, File incomingMessagesDir) throws DomibusConnectorClientFileSystemException {
		
		String pathname = new StringBuilder()
				.append(incomingMessagesDir.getAbsolutePath())
				.append(File.separator)
				.append(DomibusConnectorClientFileSystemUtil.getMessageFolderName(message, message.getMessageDetails().getEbmsMessageId()))
				.toString();
		File messageFolder = new File(pathname);
		if (!messageFolder.exists() || !messageFolder.isDirectory()) {
			LOGGER.debug("Message folder {} for incoming message does not exist. Create folder!",
					messageFolder.getAbsolutePath());
			if (!messageFolder.mkdir()) {
				throw new DomibusConnectorClientFileSystemException(
						"Incoming message folder cannot be created!");
			}
		}

		return messageFolder;
	}
	
	
}

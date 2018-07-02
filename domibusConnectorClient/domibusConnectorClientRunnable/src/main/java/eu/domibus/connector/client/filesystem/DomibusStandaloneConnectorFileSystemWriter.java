package eu.domibus.connector.client.filesystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import javax.activation.DataHandler;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.runnable.util.DomibusConnectorMessageProperties;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableUtil;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
public class DomibusStandaloneConnectorFileSystemWriter {
	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusStandaloneConnectorFileSystemWriter.class);
	
	@Value("${message.properties.file.name:"+DomibusConnectorRunnableConstants.MESSAGE_PROPERTIES_DEFAULT_FILE_NAME+"}")
	private String messagePropertiesFileName;
	
	public void createConfirmationMessage(DomibusConnectorMessageType message, File outgoingMessagesDir) throws DomibusStandaloneConnectorFileSystemException {
		File messageFolder = createConfirmationMessageFolder(message, outgoingMessagesDir);
		
		DomibusConnectorMessageProperties msgProps = null;
		File messagePropertiesFile = null;
		if (message.getMessageDetails() != null) {
			String messagePropertiesPath = messageFolder.getAbsolutePath() + File.separator + messagePropertiesFileName;

			messagePropertiesFile = new File(messagePropertiesPath);
			msgProps = convertMessageDetailsToMessageProperties(message
					.getMessageDetails(), null);
		}
		
		LOGGER.debug("Store message properties to file {}", messagePropertiesFile.getAbsolutePath());
		DomibusConnectorRunnableUtil.storeMessagePropertiesToFile(msgProps, messagePropertiesFile);
		
		if (message.getMessageConfirmations() != null) {
			for (DomibusConnectorMessageConfirmationType confirmation : message.getMessageConfirmations()) {
				if(confirmation.getConfirmation()!=null)
				createFile(messageFolder, confirmation.getConfirmationType().name()
						+ DomibusConnectorRunnableConstants.XML_FILE_EXTENSION, sourceToByteArray(confirmation.getConfirmation()));
			}
		}
	}
	
	public void writeConfirmationToFileSystem(DomibusConnectorMessageType confirmationMessage, File incomingMessagesDir, File outgoingMessagesDir ) throws DomibusStandaloneConnectorFileSystemException {
		DomibusConnectorMessageConfirmationType confirmation = confirmationMessage.getMessageConfirmations().get(0);
		String type = confirmation.getConfirmationType().name();

		String backendMessageId = confirmationMessage.getMessageDetails().getBackendMessageId();
		File messageFolder = null;

		if(!StringUtils.isEmpty(backendMessageId)) {
			messageFolder = new File(outgoingMessagesDir + File.separator
					+ backendMessageId
					+ DomibusConnectorRunnableConstants.MESSAGE_SENT_FOLDER_POSTFIX);
			if (!messageFolder.exists() || !messageFolder.isDirectory()) {
				LOGGER.info("Message folder {} for outgoing message does not exist anymore. Create incoming!",
						messageFolder.getAbsolutePath());
				messageFolder = createIncomingMessageFolder(confirmationMessage, incomingMessagesDir);
			}
		} else {

			messageFolder = createIncomingMessageFolder(confirmationMessage, incomingMessagesDir);
			
		}

		String path = messageFolder.getAbsolutePath() + File.separator + type
				+ DomibusConnectorRunnableConstants.XML_FILE_EXTENSION;
		LOGGER.debug("Create evidence xml file {}", path);
		File evidenceXml = new File(path);
		try {
			byte[] xmlBytes = sourceToByteArray(confirmation.getConfirmation());
			byteArrayToFile(xmlBytes, evidenceXml);
		} catch (IOException e) {
			throw new DomibusStandaloneConnectorFileSystemException("Could not create file "
					+ evidenceXml.getAbsolutePath(), e);
		}
	}
	
	public void writeMessageToFileSystem(DomibusConnectorMessageType message, File incomingMessagesDir) throws DomibusStandaloneConnectorFileSystemException {
		File messageFolder = createIncomingMessageFolder(message, incomingMessagesDir);
		
		LOGGER.debug("Write new message into folder {}", messageFolder.getAbsolutePath());
		
		Date messageReceived = new Date();

		DomibusConnectorMessageProperties msgProps = null;
		File messagePropertiesFile = null;
		String action = null;
		if (message.getMessageDetails() != null) {
			if (message.getMessageDetails().getAction() != null)
				action = message.getMessageDetails().getAction().getAction();
			String messagePropertiesPath = messageFolder.getAbsolutePath() + File.separator + messagePropertiesFileName;

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
					fileName = action != null ? action + DomibusConnectorRunnableConstants.PDF_FILE_EXTENSION
							: DomibusConnectorRunnableConstants.DEFAULT_PDF_FILE_NAME;

				}
				msgProps.setContentPdfFileName(fileName);
				byte[] document;
				try {
					document = dataHandlerToBytes(messageContent.getDocument().getDocument());
				} catch (IOException e) {
					throw new DomibusStandaloneConnectorFileSystemException("Could not process document! ", e);
				}
				createFile(messageFolder, fileName, document);
				DomibusConnectorDetachedSignatureType detachedSignature = messageContent.getDocument().getDetachedSignature();
				if (detachedSignature != null && detachedSignature.getDetachedSignature()!=null && detachedSignature.getDetachedSignature().length > 0
						&& detachedSignature.getMimeType() != null) {
					String fileName2 = null;
					if (StringUtils.hasText(detachedSignature.getDetachedSignatureName())
							&& !detachedSignature.getDetachedSignatureName().equals(
									DomibusConnectorRunnableConstants.DETACHED_SIGNATURE_FILE_NAME)) {
						fileName2 = detachedSignature.getDetachedSignatureName();
					} else {
						fileName2 = DomibusConnectorRunnableConstants.DETACHED_SIGNATURE_FILE_NAME;
						if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.XML))
							fileName2 += DomibusConnectorRunnableConstants.XML_FILE_EXTENSION;
						else if (detachedSignature.getMimeType().equals(DomibusConnectorDetachedSignatureMimeType.PKCS_7))
							fileName2 += DomibusConnectorRunnableConstants.PKCS7_FILE_EXTENSION;
						
					}
					msgProps.setDetachedSignatureFileName(fileName2);
					createFile(messageFolder, fileName2, detachedSignature.getDetachedSignature());
				}
			}
			if (messageContent.getXmlContent() != null){
				byte[] content = sourceToByteArray(messageContent.getXmlContent());
				String fileName = action != null ? action + DomibusConnectorRunnableConstants.XML_FILE_EXTENSION
						: DomibusConnectorRunnableConstants.DEFAULT_XML_FILE_NAME;
				msgProps.setContentXmlFileName(fileName);
				createFile(messageFolder, fileName, content);
			}
		}
		LOGGER.debug("Store message properties to file {}", messagePropertiesFile.getAbsolutePath());
		DomibusConnectorRunnableUtil.storeMessagePropertiesToFile(msgProps, messagePropertiesFile);

		if (message.getMessageAttachments() != null) {
			for (DomibusConnectorMessageAttachmentType attachment : message.getMessageAttachments()) {
				byte[] attachmentBytes;
				try {
					attachmentBytes = dataHandlerToBytes(attachment.getAttachment());
				} catch (IOException e) {
					throw new DomibusStandaloneConnectorFileSystemException("Could not process attachment! ", e);
				}
				createFile(messageFolder, attachment.getName(), attachmentBytes);
			}
		}

		if (message.getMessageConfirmations() != null) {
			for (DomibusConnectorMessageConfirmationType confirmation : message.getMessageConfirmations()) {
				createFile(messageFolder, confirmation.getConfirmationType().name()
						+ DomibusConnectorRunnableConstants.XML_FILE_EXTENSION, sourceToByteArray(confirmation.getConfirmation()));
			}
		}
	}
	
	private DomibusConnectorMessageProperties convertMessageDetailsToMessageProperties(
			DomibusConnectorMessageDetailsType messageDetails, Date messageReceived) {

		DomibusConnectorMessageProperties messageProperties = new DomibusConnectorMessageProperties();
		if (StringUtils.hasText(messageDetails.getEbmsMessageId())) {
			messageProperties.setEbmsMessageId(messageDetails.getEbmsMessageId());
		}
		if (StringUtils.hasText(messageDetails.getBackendMessageId())) {
			messageProperties.setNationalMessageId(messageDetails.getBackendMessageId());
		}
		if (StringUtils.hasText(messageDetails.getConversationId())) {
			messageProperties.setConversationId(messageDetails.getConversationId());
		}
		messageProperties.setToPartyId(messageDetails.getToParty().getPartyId());
		messageProperties.setToPartyRole(messageDetails.getToParty().getRole());
		messageProperties.setFromPartyId(messageDetails.getFromParty().getPartyId());
		messageProperties.setFromPartyRole(messageDetails.getFromParty().getRole());
		messageProperties.setFinalRecipient(messageDetails.getFinalRecipient());
		messageProperties.setOriginalSender(messageDetails.getOriginalSender());
		messageProperties.setAction(messageDetails.getAction().getAction());
		messageProperties.setService(messageDetails.getService().getService());
		if(messageReceived!=null)
			messageProperties.setMessageReceivedDatetime(DomibusStandaloneConnectorFileSystemUtil.convertDateToProperty(messageReceived));

		return messageProperties;
	}
	
	private void createFile(File messageFolder, String fileName, byte[] content)
			throws DomibusStandaloneConnectorFileSystemException {
		String filePath = messageFolder.getAbsolutePath() + File.separator + fileName;
		LOGGER.debug("#loadMessageProperties: Create file {}", filePath);
		File file = new File(filePath);
		try {
			byteArrayToFile(content, file);
		} catch (IOException e) {
			throw new DomibusStandaloneConnectorFileSystemException("Could not create file " + file.getAbsolutePath(),
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
	
	private File createConfirmationMessageFolder(DomibusConnectorMessageType message, File outgoingMessagesDir) throws DomibusStandaloneConnectorFileSystemException {
		String backendMessageId = message.getMessageDetails().getBackendMessageId();
		if(StringUtils.isEmpty(backendMessageId)) {
			backendMessageId = DomibusConnectorRunnableUtil.generateNationalMessageId(message
					.getMessageDetails().getFromParty().getPartyId(), new Date());
			LOGGER.debug("Generated backend message ID for incoming message {}", backendMessageId);
			message.getMessageDetails().setBackendMessageId(backendMessageId);
		}
		
		String pathname = outgoingMessagesDir.getAbsolutePath() + File.separator + backendMessageId + DomibusConnectorRunnableConstants.MESSAGE_NEW_FOLDER_POSTFIX;
		File messageFolder = new File(pathname);
		if (!messageFolder.exists() || !messageFolder.isDirectory()) {
			if (!messageFolder.mkdir()) {
				throw new DomibusStandaloneConnectorFileSystemException(
						"Confirmation message folder cannot be created!");
			}
		}

		return messageFolder;
	}
	
	private File createIncomingMessageFolder(DomibusConnectorMessageType message, File incomingMessagesDir) throws DomibusStandaloneConnectorFileSystemException {
		String backendMessageId = message.getMessageDetails().getBackendMessageId();
		if(StringUtils.isEmpty(backendMessageId)) {
			backendMessageId = DomibusConnectorRunnableUtil.generateNationalMessageId(message
					.getMessageDetails().getFromParty().getPartyId(), new Date());
			LOGGER.debug("Generated backend message ID for incoming message {}", backendMessageId);
			message.getMessageDetails().setBackendMessageId(backendMessageId);
		}

		String pathname = incomingMessagesDir.getAbsolutePath() + File.separator + backendMessageId;
		File messageFolder = new File(pathname);
		if (!messageFolder.exists() || !messageFolder.isDirectory()) {
			LOGGER.debug("Message folder {} for incoming message does not exist. Create folder!",
					messageFolder.getAbsolutePath());
			if (!messageFolder.mkdir()) {
				throw new DomibusStandaloneConnectorFileSystemException(
						"Incoming message folder cannot be created!");
			}
		}

		return messageFolder;
	}
}

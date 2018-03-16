package eu.domibus.connector.client.filesystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.runnable.exception.DomibusConnectorRunnableException;
import eu.domibus.connector.client.runnable.util.DomibusConnectorMessageProperties;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableUtil;
import eu.domibus.connector.client.runnable.util.StandaloneClientProperties;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;

@Component
public class DomibusStandaloneConnectorFileSystemReader {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusStandaloneConnectorFileSystemReader.class);

	@Value("${message.properties.file.name:"+DomibusConnectorRunnableConstants.MESSAGE_PROPERTIES_DEFAULT_FILE_NAME+"}")
	private String messagePropertiesFileName;

	@Autowired
	StandaloneClientProperties standaloneClientProperties;
	
	public List<File> readUnsentMessages(File outgoingMessagesDir ){
		List<File> messagesUnsent = new ArrayList<File>();

		if (outgoingMessagesDir.listFiles().length > 0) {
			for (File sub : outgoingMessagesDir.listFiles()) {
				if (sub.isDirectory()
						&& sub.getName().endsWith(DomibusConnectorRunnableConstants.MESSAGE_READY_FOLDER_POSTFIX)) {
					messagesUnsent.add(sub);
				}
			}
		}
		
		return messagesUnsent;
	}

	public DomibusConnectorMessageType readMessageFromFolder(File messageFolder) throws DomibusStandaloneConnectorFileSystemException {
		DomibusConnectorMessageProperties messageProperties = DomibusConnectorRunnableUtil
				.loadMessageProperties(messageFolder, messagePropertiesFileName);
		
		String nationalMessageId = extractNationalMessageId(messageProperties);

		String newMessageFolderPath = messageFolder.getAbsolutePath().substring(0,
				messageFolder.getAbsolutePath().lastIndexOf(File.separator)+1);
				

		messageFolder = DomibusStandaloneConnectorFileSystemUtil.renameMessageFolder(messageFolder, newMessageFolderPath, nationalMessageId);
		
		
		if (messageFolder.exists() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {
			String messageFolderPath = messageFolder.getAbsolutePath();
			LOGGER.info("Start reading message from folder {}", messageFolderPath);

			File workMessageFolder = DomibusStandaloneConnectorFileSystemUtil.renameMessageFolder(messageFolder, messageFolderPath, DomibusConnectorRunnableConstants.MESSAGE_PROCESSING_FOLDER_POSTFIX);
			
			DomibusConnectorMessageType message = new DomibusConnectorMessageType();
			
			try {
				message = processMessageFolderFiles(workMessageFolder, messageProperties);
			} catch (Exception e) {
				File failedMessageFolder = DomibusStandaloneConnectorFileSystemUtil.renameMessageFolder(workMessageFolder, messageFolderPath, DomibusConnectorRunnableConstants.MESSAGE_FAILED_FOLDER_POSTFIX);
				
				throw new DomibusStandaloneConnectorFileSystemException("Could not process message folder "+failedMessageFolder.getAbsolutePath());
			}
			
			messageProperties.setMessageSentDatetime(DomibusStandaloneConnectorFileSystemUtil.convertDateToProperty(new Date()));
			DomibusConnectorRunnableUtil.storeMessagePropertiesToFile(messageProperties, new File(workMessageFolder, messagePropertiesFileName));
			try {
				DomibusStandaloneConnectorFileSystemUtil.renameMessageFolder(workMessageFolder, messageFolderPath, DomibusConnectorRunnableConstants.MESSAGE_SENT_FOLDER_POSTFIX);
			} catch (DomibusStandaloneConnectorFileSystemException e) {
				LOGGER.error("",e);
			}


			return message;
		}
		return null;
	}

	private DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder, DomibusConnectorMessageProperties messageProperties)
			throws DomibusConnectorRunnableException {

		DomibusConnectorMessageType message = new DomibusConnectorMessageType();

		DomibusConnectorMessageDetailsType messageDetails = convertMessagePropertiesToMessageDetails(messageProperties);
		message.setMessageDetails(messageDetails);

		int attachmentCount = 1;

		DomibusConnectorMessageContentType messageContent = new DomibusConnectorMessageContentType();
		
		for (File sub : workMessageFolder.listFiles()) {
			if (sub.getName().equals(messagePropertiesFileName)) {
				continue;
			} else {
				DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();
				if (isFile(sub.getName(),messageProperties.getContentXmlFileName())) {
					LOGGER.debug("Found content xml file with name {}", sub.getName());
					try {
						messageContent.setXmlContent(fileToSource(sub));
					} catch (IOException e) {
						throw new DomibusConnectorRunnableException(
								"Exception creating Source object out of file " + sub.getName());
					}
					continue;
				} else if (isFile(sub.getName(),messageProperties.getContentPdfFileName())) {
					LOGGER.debug("Found content pdf file with name {}", sub.getName());
					document.setDocument(new DataHandler(new FileDataSource(sub)));
					document.setDocumentName(sub.getName());


					continue;
				} else if (isFile(sub.getName(),messageProperties.getDetachedSignatureFileName())) {
					LOGGER.debug("Found detached signature file with name {}", sub.getName());
					try {
						DomibusConnectorDetachedSignatureType det = new DomibusConnectorDetachedSignatureType();
						det.setDetachedSignature(fileToByteArray(sub));
						det.setDetachedSignatureName(sub.getName());
						if (sub.getName().endsWith(DomibusConnectorRunnableConstants.XML_FILE_EXTENSION)) {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.XML);
						} else if (sub.getName()
								.endsWith(DomibusConnectorRunnableConstants.PKCS7_FILE_EXTENSION)) {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.PKCS_7);
						} else {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.BINARY);
						}
						document.setDetachedSignature(det );
					} catch (IOException e) {
						throw new DomibusConnectorRunnableException(
								"Exception loading detached signature into byte array from file " + sub.getName());
					}
					continue;
				} else if (isConfirmation(sub.getName())) {
					DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
					try {
						confirmation.setConfirmation(fileToSource(sub));
					} catch (IOException e) {
						throw new DomibusConnectorRunnableException(
								"Exception creating Source object out of file " + sub.getName());
					}
					if(sub.getName().indexOf(".xml")>0) {
						String name = sub.getName().substring(0, sub.getName().indexOf(".xml"));
						DomibusConnectorConfirmationType valueOf = DomibusConnectorConfirmationType.valueOf(name);
						if(valueOf!=null) {
							confirmation.setConfirmationType(valueOf);
						}
					}
					message.getMessageConfirmations().add(confirmation);
				} else {
					LOGGER.debug("Processing attachment File {}", sub.getName());

					byte[] attachmentData = null;
					try {
						attachmentData = fileToByteArray(sub);
					} catch (IOException e) {
						throw new DomibusConnectorRunnableException(
								"Exception loading attachment into byte array from file " + sub.getName());
					}
					String attachmentId = DomibusConnectorRunnableConstants.ATTACHMENT_ID_PREFIX + attachmentCount;

					if(!ArrayUtils.isEmpty(attachmentData)){
						DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();

						attachment.setAttachment(new DataHandler(new FileDataSource(sub)));
						attachment.setIdentifier(attachmentId);
						attachment.setName(sub.getName());
						attachmentCount++;
						attachment.setMimeType(DomibusConnectorRunnableUtil.getMimeTypeFromFileName(sub));

						LOGGER.debug("Add attachment {}", attachment.toString());
						message.getMessageAttachments().add(attachment);
					}
				}
				messageContent.setDocument(document);
			}
		}

		if(messageContent.getXmlContent()!=null)
			message.setMessageContent(messageContent);
		

		return message;
	}
	
	private boolean isFile(String filename, String messageProperty) {
		if(messageProperty!=null) {
			if(messageProperty.equals(filename))
				return true;
		}
		return false;
	}
	
	private boolean isConfirmation(String filename) {
		if(filename!=null) {
			if(filename.indexOf(".xml")>0) {
				String name = filename.substring(0, filename.indexOf(".xml"));
				DomibusConnectorConfirmationType valueOf = DomibusConnectorConfirmationType.valueOf(name);
				if(valueOf!=null) {
					return true;
				}
			}
		}
		return false;
	}
	
	private String extractNationalMessageId(DomibusConnectorMessageProperties messageProperties) {
		String nationalMessageId = null;
		if (messageProperties != null) {
			nationalMessageId = messageProperties.getNationalMessageId();
			LOGGER.debug("Found nationalMessageId in Properties: {}", nationalMessageId);
		}
		if (!StringUtils.hasText(nationalMessageId)) {
			nationalMessageId = DomibusConnectorRunnableUtil.generateNationalMessageId(messageProperties
					.getOriginalSender(), new Date());
			LOGGER.debug("No national message ID resolved. Generated " + nationalMessageId);
		}
		return nationalMessageId;
	}

	private DomibusConnectorMessageDetailsType convertMessagePropertiesToMessageDetails(DomibusConnectorMessageProperties properties) {

		DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();

		messageDetails.setFinalRecipient(properties.getFinalRecipient());
		messageDetails.setOriginalSender(properties.getOriginalSender());
		messageDetails.setBackendMessageId(properties.getNationalMessageId());


		String gatewayName = standaloneClientProperties.getGateway().getName();
		String gatewayRole = standaloneClientProperties.getGateway().getRole();

		//set from party...
		String fromPartyId = properties.getFromPartyId();
		String fromPartyRole = properties.getFromPartyRole();


		if (!StringUtils.hasText(fromPartyId)) {
			fromPartyId = gatewayName;
		}
		if (!StringUtils.hasText(fromPartyRole)) {
			fromPartyRole = gatewayRole;
		}

		DomibusConnectorPartyType fromParty = new DomibusConnectorPartyType();
		fromParty.setPartyId(fromPartyId);
		fromParty.setRole(fromPartyRole);
		messageDetails.setFromParty(fromParty);



		String toPartyId = properties.getToPartyId();
		String toPartyRole = properties.getToPartyRole();
		DomibusConnectorPartyType toParty = new DomibusConnectorPartyType();
		toParty.setPartyId(toPartyId);
		toParty.setRole(toPartyRole);
		messageDetails.setToParty(toParty);

		String action = properties.getAction();
		DomibusConnectorActionType domibusConnectorAction = new DomibusConnectorActionType();
		domibusConnectorAction.setAction(action);
		messageDetails.setAction(domibusConnectorAction);

		String service = properties.getService();
		DomibusConnectorServiceType domibusConnectorService = new DomibusConnectorServiceType();
		domibusConnectorService.setService(service);
		messageDetails.setService(domibusConnectorService);

		String conversationId = properties.getConversationId();
		if(StringUtils.hasText(conversationId)){
			messageDetails.setConversationId(conversationId);
		}

		return messageDetails;
	}
	
	private byte[] fileToByteArray(File file) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fileInputStream.read(data);
		fileInputStream.close();

		return data;
	}

	private Source fileToSource(File file) throws IOException {
		byte[] bytes = fileToByteArray(file);
		
		
		return new StreamSource(new ByteArrayInputStream(bytes));

	}

}

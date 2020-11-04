package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;
import javax.validation.Valid;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSMessageProperties;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSProperties;
import eu.domibus.connector.client.filesystem.message.FSMessageDetails;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
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
import eu.domibus.connector.domain.transition.tools.ConversionTools;

@Component
@Validated
@Valid
public class DomibusConnectorClientFileSystemReader {

	@Autowired
	private DomibusConnectorClientFSMessageProperties messageProperties;

	@Autowired
	private DomibusConnectorClientFSProperties properties;

	
	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusConnectorClientFileSystemReader.class);

	public List<File> readUnsentMessages(File outgoingMessagesDir){
		LOGGER.debug("#readUnsentMessages: Searching for folders with ending {}", properties.getMessageReadyPostfix());
		List<File> messagesUnsent = new ArrayList<File>();

		if (outgoingMessagesDir.listFiles().length > 0) {
			for (File sub : outgoingMessagesDir.listFiles()) {
				if (sub.isDirectory()
						&& sub.getName().endsWith(properties.getMessageReadyPostfix())) {
					messagesUnsent.add(sub);
				}
			}
		}

		return messagesUnsent;
	}

	public List<File> readMessagesFromDirWithPostfix(File messagesDir, String endsWith) {
		List<File> messages = new ArrayList<File>();

		if (messagesDir.listFiles().length > 0) {
			for (File sub : messagesDir.listFiles()) {
				if (sub.isDirectory()
						&& sub.getName().endsWith(endsWith)) {
					messages.add(sub);
				}
			}
		}
		return messages;
	}

	public List<File> readAllMessagesFromDir(File messagesDir) {
		List<File> messages = new ArrayList<File>();

		if (messagesDir.listFiles().length > 0) {
			for (File sub : messagesDir.listFiles()) {
				if (sub.isDirectory() && sub.listFiles()!=null && sub.listFiles().length > 0) {
					messages.add(sub);
				}
			}
		}
		return messages;
	}

	public DomibusConnectorMessageType readMessageFromFolder(File messageFolder) throws DomibusConnectorClientFileSystemException {
		FSMessageDetails messageDetails = DomibusConnectorClientFileSystemUtil.loadMessageProperties(messageFolder, this.messageProperties.getFileName());


		if (messageFolder.exists() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {
			String messageFolderPath = messageFolder.getAbsolutePath();
			LOGGER.info("Start reading message from folder {}", messageFolderPath);

			DomibusConnectorMessageType message = new DomibusConnectorMessageType();

			try {
				message = processMessageFolderFiles(messageFolder, messageDetails);
			} catch (Exception e) {
				LOGGER.error("#readMessageFromFolder: an error occured!", e);
				throw new DomibusConnectorClientFileSystemException("Could not process message folder "+messageFolder.getAbsolutePath());
			}

			return message;
		}
		return null;
	}
	
	public Map<String, DomibusConnectorClientStorageFileType> getFileListFromMessageFolder(File messageFolder){
		Map<String, DomibusConnectorClientStorageFileType> files = new HashMap<String, DomibusConnectorClientStorageFileType>();
		if (messageFolder.exists() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {
			
			FSMessageDetails messageDetails = DomibusConnectorClientFileSystemUtil.loadMessageProperties(messageFolder, this.messageProperties.getFileName());
			
			for (File sub : messageFolder.listFiles()) {
				
				if (sub.getName().equals(messageProperties.getFileName())) {
					continue;
				} else {

					if (isFile(sub.getName(),messageDetails.getMessageDetails().getProperty(messageProperties.getContentXmlFileName()))) {
						LOGGER.debug("Found content xml file with name {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_CONTENT);
						continue;
					} else if (isFile(sub.getName(),messageDetails.getMessageDetails().getProperty(messageProperties.getContentPdfFileName()))) {
						LOGGER.debug("Found content pdf file with name {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_DOCUMENT);
						continue;
					} else if (isFile(sub.getName(),messageDetails.getMessageDetails().getProperty(messageProperties.getDetachedSignatureFileName()))) {
						LOGGER.debug("Found detached signature file with name {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.DETACHED_SIGNATURE);
						continue;
					} else if (isConfirmation(sub.getName())) {
						LOGGER.debug("Found confirmation file {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.CONFIRMATION);
						continue;
					} else {
						LOGGER.debug("Found attachment file {}", sub.getName());
						files.put(sub.getName(), DomibusConnectorClientStorageFileType.BUSINESS_ATTACHMENT);
					}
				}
			}
			
		}
		return files;
	}
	
	public byte[] loadFileContentFromMessageFolder(File messageFolder, String fileName) {
		if (messageFolder.exists() && messageFolder.isDirectory() && messageFolder.listFiles().length > 0) {
			
			String messageFolderPath = messageFolder.getAbsolutePath();
			LOGGER.debug("Start reading file {} from folder {}", fileName, messageFolderPath);
			
			for (File sub : messageFolder.listFiles()) {
				if(sub.getName().equals(fileName)) {
					LOGGER.debug("Found file with name {} and length {}", sub.getName(), sub.length());
					byte[] content = null;
					try {
						content = fileToByteArray(sub);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					return content;
				}
				
				
			}
		}
		return null;
	}


	private DomibusConnectorMessageType processMessageFolderFiles(File workMessageFolder, FSMessageDetails messageDetails)
			throws DomibusConnectorClientFileSystemException {

		DomibusConnectorMessageType message = new DomibusConnectorMessageType();

		DomibusConnectorMessageDetailsType msgDetails = convertMessagePropertiesToMessageDetails(messageDetails);
		message.setMessageDetails(msgDetails);

		int attachmentCount = 1;

		DomibusConnectorMessageContentType messageContent = new DomibusConnectorMessageContentType();
		DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();

		for (File sub : workMessageFolder.listFiles()) {
			if (sub.getName().equals(messageDetails.getMessageDetails().getProperty(messageProperties.getFileName()))) {
				continue;
			} else {

				if (isFile(sub.getName(),messageDetails.getMessageDetails().getProperty(messageProperties.getContentXmlFileName()))) {
					LOGGER.debug("Found content xml file with name {}", sub.getName());
					try {
						if(LOGGER.isDebugEnabled()) {
							byte[] xmlBytes = fileToByteArray(sub);
							LOGGER.debug("Business content XML after read from file: {}", new String(xmlBytes));
						}
						messageContent.setXmlContent(ConversionTools.convertFileToStreamSource(sub));
					} catch (IOException e) {
						throw new DomibusConnectorClientFileSystemException(
								"Exception creating Source object out of file " + sub.getName());
					}
					continue;
				} else if (isFile(sub.getName(),messageDetails.getMessageDetails().getProperty(messageProperties.getContentPdfFileName()))) {
					LOGGER.debug("Found content pdf file with name {}", sub.getName());
//					try {
						document.setDocument(ConversionTools.convertFileToDataHandler(sub, "application/octet-stream"));
						document.setDocumentName(sub.getName());
//					} catch (IOException e) {
//						throw new DomibusConnectorClientFileSystemException(
//								"Exception creating DataHandler object out of file " + sub.getName());
//					}
					continue;
				} else if (isFile(sub.getName(),messageDetails.getMessageDetails().getProperty(messageProperties.getDetachedSignatureFileName()))) {
					LOGGER.debug("Found detached signature file with name {}", sub.getName());
					try {
						DomibusConnectorDetachedSignatureType det = new DomibusConnectorDetachedSignatureType();
						det.setDetachedSignature(fileToByteArray(sub));
						det.setDetachedSignatureName(sub.getName());
						if (sub.getName().endsWith(properties.getXmlFileExtension())) {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.XML);
						} else if (sub.getName()
								.endsWith(properties.getPkcs7FileExtension())) {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.PKCS_7);
						} else {
							det.setMimeType(DomibusConnectorDetachedSignatureMimeType.BINARY);
						}
						document.setDetachedSignature(det );
					} catch (IOException e) {
						throw new DomibusConnectorClientFileSystemException(
								"Exception loading detached signature into byte array from file " + sub.getName());
					}
					continue;
				} else if (isConfirmation(sub.getName())) {
					DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
//					try {
						confirmation.setConfirmation(ConversionTools.convertFileToStreamSource(sub));
//					} catch (IOException e) {
//						throw new DomibusConnectorClientFileSystemException(
//								"Exception creating Source object out of file " + sub.getName());
//					}
					if(sub.getName().indexOf(".xml")>0) {
						String name = sub.getName().substring(0, sub.getName().indexOf(".xml"));
						DomibusConnectorConfirmationType valueOf = DomibusConnectorConfirmationType.valueOf(name);
						if(valueOf!=null) {
							confirmation.setConfirmationType(valueOf);
						}
					}
					message.getMessageConfirmations().add(confirmation);
				} else if (!isFile(sub.getName(), this.messageProperties.getFileName())){
					LOGGER.debug("Processing attachment File {}", sub.getName());

					byte[] attachmentData = null;
					try {
						attachmentData = fileToByteArray(sub);
					} catch (IOException e) {
						throw new DomibusConnectorClientFileSystemException(
								"Exception loading attachment into byte array from file " + sub.getName());
					}
					String attachmentId = properties.getAttachmentIdPrefix() + attachmentCount;

					if(!ArrayUtils.isEmpty(attachmentData)){
//						try {
							DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();

							attachment.setAttachment(ConversionTools.convertFileToDataHandler(sub, "application/octet-stream"));
							attachment.setIdentifier(attachmentId);
							attachment.setName(sub.getName());
							attachmentCount++;
							attachment.setMimeType(new MimetypesFileTypeMap().getContentType(sub.getName()));

							LOGGER.debug("Add attachment {}", attachment.toString());
							message.getMessageAttachments().add(attachment);
//						} catch (IOException ioe) {
//							String error = String.format("Error while loading attachment from file [%s]", sub);
//							LOGGER.error(error);
//							throw new RuntimeException(error);
//						}
					}
				}
			}
		}

		if (document.getDocument() != null) {
			messageContent.setDocument(document);
		}

		if(messageContent.getXmlContent()!=null)
			message.setMessageContent(messageContent);


		return message;
	}

	public DomibusConnectorClientStorageStatus checkStorageStatusOfMessage(String storageLocation) {
		File storageFile = new File(storageLocation);

		//check if the storageLocation is a directory
		if(storageFile!=null && storageFile.exists() && storageFile.isDirectory()) {
			return DomibusConnectorClientStorageStatus.STORED;
		}
		

		//Non of the above, so the storageLocation does not exist anymore
		return DomibusConnectorClientStorageStatus.DELETED;
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
				List<String> CONFIRMATION_NAMES = Arrays.asList(DomibusConnectorConfirmationType.values()).stream()
						.map(e -> e.name())
						.collect(Collectors.toList());
				if (CONFIRMATION_NAMES.contains(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private DomibusConnectorMessageDetailsType convertMessagePropertiesToMessageDetails(FSMessageDetails properties) {

		DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();

		messageDetails.setEbmsMessageId(properties.getMessageDetails().getProperty(messageProperties.getEbmsMessageId()));
		
		messageDetails.setFinalRecipient(properties.getMessageDetails().getProperty(messageProperties.getFinalRecipient()));
		messageDetails.setOriginalSender(properties.getMessageDetails().getProperty(messageProperties.getOriginalSender()));
		if(!StringUtils.isEmpty(properties.getMessageDetails().getProperty(messageProperties.getBackendMessageId())))
			messageDetails.setBackendMessageId(properties.getMessageDetails().getProperty(messageProperties.getBackendMessageId()));


		String fromPartyId = properties.getMessageDetails().getProperty(messageProperties.getFromPartyId());
		String fromPartyRole = properties.getMessageDetails().getProperty(messageProperties.getFromPartyRole());


		DomibusConnectorPartyType fromParty = new DomibusConnectorPartyType();
		fromParty.setPartyId(fromPartyId);
		fromParty.setRole(fromPartyRole);
		messageDetails.setFromParty(fromParty);



		String toPartyId = properties.getMessageDetails().getProperty(messageProperties.getToPartyId());
		String toPartyRole = properties.getMessageDetails().getProperty(messageProperties.getToPartyRole());
		DomibusConnectorPartyType toParty = new DomibusConnectorPartyType();
		toParty.setPartyId(toPartyId);
		toParty.setRole(toPartyRole);
		messageDetails.setToParty(toParty);

		String action = properties.getMessageDetails().getProperty(messageProperties.getAction());
		DomibusConnectorActionType domibusConnectorAction = new DomibusConnectorActionType();
		domibusConnectorAction.setAction(action);
		messageDetails.setAction(domibusConnectorAction);

		String service = properties.getMessageDetails().getProperty(messageProperties.getService());
		DomibusConnectorServiceType domibusConnectorService = new DomibusConnectorServiceType();
		domibusConnectorService.setService(service);
		messageDetails.setService(domibusConnectorService);

		String conversationId = properties.getMessageDetails().getProperty(messageProperties.getConversationId());
		if(StringUtils.hasText(conversationId)){
			messageDetails.setConversationId(conversationId);
		}

		return messageDetails;
	}

	private byte[] fileToByteArray(File file) throws IOException {
		return Files.readAllBytes(file.toPath());
//		try (FileInputStream fileInputStream = new FileInputStream(file)) {
//			byte[] data = StreamUtils.copyToByteArray(fileInputStream);
//			fileInputStream.close();
//			return data;
//		} catch (IOException ioe) {
//			throw new RuntimeException("IOExceptio occured while reading from file " + file, ioe);
//		}
	}

//	private Source fileToSource(File file) throws IOException {
//		byte[] bytes = fileToByteArray(file);
//		return new StreamSource(new ByteArrayInputStream(bytes));
//	}

//	private DataHandler convertToDataHandler(File file) throws IOException {
//		LOGGER.debug("#convertToDataHandler: converting file [{}] to DataHandler", file);
//		byte[] bytes = fileToByteArray(file);
//		DataSource dataSource = new MyByteArrayDataSource(bytes);
//		DataHandler dh = new DataHandler(dataSource);
//		return dh;
//	}


	

}

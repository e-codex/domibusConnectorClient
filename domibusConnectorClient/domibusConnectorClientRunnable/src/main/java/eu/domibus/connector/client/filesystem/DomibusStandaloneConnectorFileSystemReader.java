package eu.domibus.connector.client.filesystem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.runnable.configuration.ConnectorClientProperties;
import eu.domibus.connector.client.runnable.exception.DomibusConnectorRunnableException;
import eu.domibus.connector.client.runnable.util.DomibusConnectorMessageProperties;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableUtil;
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
				.loadMessageProperties(messageFolder, ConnectorClientProperties.messagePropertiesFileName);
		
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
			    LOGGER.error("#readMessageFromFolder: an error occured, renaming folder to failed", e);
				File failedMessageFolder = DomibusStandaloneConnectorFileSystemUtil.renameMessageFolder(workMessageFolder, messageFolderPath, DomibusConnectorRunnableConstants.MESSAGE_FAILED_FOLDER_POSTFIX);
				
				throw new DomibusStandaloneConnectorFileSystemException("Could not process message folder "+failedMessageFolder.getAbsolutePath());
			}
			
			messageProperties.setMessageSentDatetime(DomibusStandaloneConnectorFileSystemUtil.convertDateToProperty(new Date()));
			DomibusConnectorRunnableUtil.storeMessagePropertiesToFile(messageProperties, new File(workMessageFolder, ConnectorClientProperties.messagePropertiesFileName));
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
        DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();

		for (File sub : workMessageFolder.listFiles()) {
			if (sub.getName().equals(ConnectorClientProperties.messagePropertiesFileName)) {
				continue;
			} else {

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
					try {
						document.setDocument(convertToDataHandler(sub));
						document.setDocumentName(sub.getName());
					} catch (IOException e) {
						throw new DomibusConnectorRunnableException(
								"Exception creating DataHandler object out of file " + sub.getName());
					}
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
					    try {
                            DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();

                            attachment.setAttachment(this.convertToDataHandler(sub));
                            attachment.setIdentifier(attachmentId);
                            attachment.setName(sub.getName());
                            attachmentCount++;
                            attachment.setMimeType(DomibusConnectorRunnableUtil.getMimeTypeFromFileName(sub));

                            LOGGER.debug("Add attachment {}", attachment.toString());
                            message.getMessageAttachments().add(attachment);
                        } catch (IOException ioe) {
					        String error = String.format("Error while loading attachment from file [%s]", sub);
					        LOGGER.error(error);
					        throw new RuntimeException(error);
                        }
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
//                DomibusConnectorConfirmationType valueOf = DomibusConnectorConfirmationType.fromValue(name);
//				if(valueOf!=null) {
//					return true;
//				}
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


		String gatewayName = ConnectorClientProperties.gatewayNameValue;
		String gatewayRole = ConnectorClientProperties.gatewayRoleValue;

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
		try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] data = StreamUtils.copyToByteArray(fileInputStream);
            return data;
        } catch (IOException ioe) {
		    throw new RuntimeException("IOExceptio occured while reading from file " + file, ioe);
        }
	}

	private Source fileToSource(File file) throws IOException {
		byte[] bytes = fileToByteArray(file);
		return new StreamSource(new ByteArrayInputStream(bytes));
	}

	private DataHandler convertToDataHandler(File file) throws IOException {
		LOGGER.debug("#convertToDataHandler: converting file [{}] to DataHandler", file);
		byte[] bytes = fileToByteArray(file);
		DataSource dataSource = new MyByteArrayDataSource(bytes);
		DataHandler dh = new DataHandler(dataSource);
		return dh;
	}
    
    
    private static class MyByteArrayDataSource implements DataSource {

        private byte[] buffer;
        
        public MyByteArrayDataSource() {}
        
        public MyByteArrayDataSource(byte[] buffer) {
            this.buffer = buffer;
        }

        public byte[] getBuffer() {
            return buffer;
        }

        public void setBuffer(byte[] buffer) {
            this.buffer = buffer;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(buffer);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException("Read Only Data Source!");
        }

        @Override
        public String getContentType() {
            return "application/octet-stream";
        }

        @Override
        public String getName() {
            return "";
        }
        
    }

}

package eu.domibus.connector.client.filesystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.client.nbc.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.runnable.configuration.StandaloneClientProperties;
import eu.domibus.connector.client.runnable.exception.DomibusConnectorRunnableException;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;
import eu.domibus.connector.client.service.DomibusConnectorClientService;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
public class DomibusStandaloneConnectorFileSystemClient implements InitializingBean, DomibusConnectorNationalBackendClient {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(DomibusStandaloneConnectorFileSystemClient.class);

	@Autowired
	StandaloneClientProperties standaloneClientProperties;
	
	@Autowired
	DomibusStandaloneConnectorFileSystemReader fileSystemReader;
	
	@Autowired
	DomibusStandaloneConnectorFileSystemWriter fileSystemWriter;
	
	@Autowired
	private DomibusConnectorClientService clientService;

	private File incomingMessagesDir;

	private File outgoingMessagesDir;
	
	@Override
	public void processMessagesFromConnector(List<DomibusConnectorMessageType> messages)
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		if(!CollectionUtils.isEmpty(messages)) {
			for(DomibusConnectorMessageType message:messages) {
				try {
					if(checkIfConfirmationMessage(message)) {
						LOGGER.debug("#processMessagesFromConnector: message [{}] is a confirmation message!", message);
						fileSystemWriter.writeConfirmationToFileSystem(message, incomingMessagesDir, outgoingMessagesDir);
					}else {
						LOGGER.debug("#processMessageFromConnector: message [{}] is a incoming business message", message);
						fileSystemWriter.writeMessageToFileSystem(message, incomingMessagesDir);
						confirmIncomingMessage(message);
					}
				} catch (DomibusStandaloneConnectorFileSystemException e) {
					LOGGER.error("Exception processing message from connector... ", e);
				}
			}
		}
	}

	@Override
	public List<DomibusConnectorMessageType> checkForMessagesOnNationalBackend()
			throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
		LOGGER.debug("Start searching dir {} for folder with ending {}", outgoingMessagesDir.getAbsolutePath(),
				DomibusConnectorRunnableConstants.MESSAGE_READY_FOLDER_POSTFIX);
		List<File> messagesUnsent = fileSystemReader.readUnsentMessages(outgoingMessagesDir);

		if (!messagesUnsent.isEmpty()) {
			List<DomibusConnectorMessageType> messages = new ArrayList<DomibusConnectorMessageType>(messagesUnsent.size());
			LOGGER.info("Found {} new outgoing messages to process!", messagesUnsent.size());
			for (File messageFolder : messagesUnsent) {
				LOGGER.debug("Processing new message folder {}", messageFolder.getAbsolutePath());
				if (messageFolder.listFiles().length > 0) {
					
					DomibusConnectorMessageType message = null;
					try {
						message = fileSystemReader.readMessageFromFolder(messageFolder);
					} catch (DomibusStandaloneConnectorFileSystemException e) {
						LOGGER.error(""+e);
						continue;
					}
					
					if(message!=null) {
						messages.add(message);
					}
				}
			}
			return messages;
		} else {
			LOGGER.debug("No new messages found!");
			return null;
		}
	}


	

	private void confirmIncomingMessage(DomibusConnectorMessageType message) throws DomibusStandaloneConnectorFileSystemException {
		DomibusConnectorMessageType deliveryMessage = createConfirmationMessage(DomibusConnectorConfirmationType.DELIVERY, message);
		
//		fileSystemWriter.createConfirmationMessage(deliveryMessage, outgoingMessagesDir);
		try {
			clientService.submitMessageToConnector(deliveryMessage);
		} catch (DomibusConnectorClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DomibusConnectorMessageType retrievalMessage = createConfirmationMessage(DomibusConnectorConfirmationType.RETRIEVAL, message);
		
//		fileSystemWriter.createConfirmationMessage(retrievalMessage, outgoingMessagesDir);
		try {
			clientService.submitMessageToConnector(retrievalMessage);
		} catch (DomibusConnectorClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private DomibusConnectorMessageType createConfirmationMessage(DomibusConnectorConfirmationType evidenceType, DomibusConnectorMessageType originalMessage) {
		DomibusConnectorMessageDetailsType details = new DomibusConnectorMessageDetailsType();
		details.setRefToMessageId(originalMessage.getMessageDetails().getEbmsMessageId());
		details.setService(originalMessage.getMessageDetails().getService());
		details.setFromParty(originalMessage.getMessageDetails().getToParty());
		details.setToParty(originalMessage.getMessageDetails().getFromParty());
		details.setFinalRecipient(originalMessage.getMessageDetails().getOriginalSender());
		details.setOriginalSender(originalMessage.getMessageDetails().getFinalRecipient());
		DomibusConnectorActionType action = new DomibusConnectorActionType();
		action.setAction(evidenceType.value());
		details.setAction(action);
		details.setService(originalMessage.getMessageDetails().getService());

		DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
		confirmation.setConfirmationType(evidenceType);

		DomibusConnectorMessageType confirmationMessage = new DomibusConnectorMessageType();
		confirmationMessage.setMessageDetails(details);
		confirmationMessage.getMessageConfirmations().add(confirmation);

		return confirmationMessage;
	}

	

	private boolean checkIfConfirmationMessage(DomibusConnectorMessageType message) {
		return message.getMessageContent()==null && !CollectionUtils.isEmpty(message.getMessageConfirmations());
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		String path = System.getProperty("user.dir");

		String incomingMessagesDirectory = standaloneClientProperties.getMessages().getIncoming().getDirectory();
		boolean createIncomingMessageDirectories = standaloneClientProperties.getMessages().getIncoming().isCreateDirectory();

		if (!StringUtils.hasText(incomingMessagesDirectory)) {
			incomingMessagesDirectory = path + File.separator
					+ DomibusConnectorRunnableConstants.INCOMING_MESSAGES_DEFAULT_DIR;
			LOGGER.debug("The property 'incoming.messages.directory' is not set properly as it is null or empty! Default value set!");
		}
		LOGGER.debug("Initializing set incoming messages directory {}", incomingMessagesDirectory);
		incomingMessagesDir = new File(incomingMessagesDirectory);

		if (createIncomingMessageDirectories && !incomingMessagesDir.exists()) {
			incomingMessagesDir.mkdirs();
		} else if (!incomingMessagesDir.exists()) {
			throw new DomibusConnectorRunnableException("Directory '" + incomingMessagesDirectory + "' does not exist!");
		}

		if (!incomingMessagesDir.isDirectory()) {
			throw new DomibusConnectorRunnableException("'" + incomingMessagesDirectory + "' is not a directory!");
		}


		String outgoingMessagesDirectory = standaloneClientProperties.getMessages().getOutgoing().getDirectory();
		boolean createOutgoingMessageDirectories = standaloneClientProperties.getMessages().getOutgoing().isCreateDirectory();

		if (!StringUtils.hasText(outgoingMessagesDirectory)) {
			outgoingMessagesDirectory = path + File.separator
					+ DomibusConnectorRunnableConstants.OUTGOING_MESSAGES_DEFAULT_DIR;
			LOGGER.debug("The property 'outgoing.messages.directory' is not set properly as it is null or empty! Default value set!");
		}
		LOGGER.debug("Initializing set outgoing messages directory {}", outgoingMessagesDirectory);
		outgoingMessagesDir = new File(outgoingMessagesDirectory);

		if (createOutgoingMessageDirectories && !outgoingMessagesDir.isDirectory()) {
			outgoingMessagesDir.mkdirs();
		} else if (!outgoingMessagesDir.exists()) {
			throw new DomibusConnectorRunnableException("Directory '" + outgoingMessagesDirectory + "' does not exist!");
		}

		if (!outgoingMessagesDir.isDirectory()) {
			throw new DomibusConnectorRunnableException("'" + outgoingMessagesDirectory + "' is not a directory!");
		}

	}



}

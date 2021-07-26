package eu.domibus.connector.client.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientMessageHandler;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.DomibusConnectorClientLink;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;

@Component
public class DomibusConnectorClientImpl implements DomibusConnectorClient {

	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientImpl.class);


    @Autowired
    private DomibusConnectorClientLink clientService;
    
    @Autowired
    private DomibusConnectorClientMessageHandler messageHandler;
    
	@Override
	public void submitNewMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException {
		 	MDC.put("backendmessageid", message.getMessageDetails().getBackendMessageId());
	        DomibsConnectorAcknowledgementType domibusConnectorAckType = null;
	        
	       messageHandler.prepareOutboundMessage(message);
	        
	        try {
	            domibusConnectorAckType = clientService.submitMessageToConnector(message);
	        } catch (DomibusConnectorClientException e) {
	            LOGGER.error("Exception submitting message to connector: ", e);
	            MDC.remove("backendmessageid");
	            throw e;
	        }
	        
	        if(domibusConnectorAckType == null) {
	        	LOGGER.error("The received acknowledgement for message with backend message ID {} is null! ", message.getMessageDetails().getBackendMessageId());
	            MDC.remove("backendmessageid");
	            throw new DomibusConnectorClientException("The received acknowledgement for message with backend message ID "+message.getMessageDetails().getBackendMessageId()+" is null!");
	        }
	        if(!domibusConnectorAckType.isResult()) {
	        	LOGGER.error("The received acknowledgement for message with backend message ID {} is negative! {} ", message.getMessageDetails().getBackendMessageId(), domibusConnectorAckType.getResultMessage());
	            MDC.remove("backendmessageid");
	            throw new DomibusConnectorClientException("The received acknowledgement for message with backend message ID "+message.getMessageDetails().getBackendMessageId()+" is negative!");
	        }

	}


	@Override
	public DomibusConnectorMessagesType requestNewMessagesFromConnector() throws DomibusConnectorClientException {
		DomibusConnectorMessagesType messages = null;
		try {
			messages = clientService.requestMessagesFromConnector();
		} catch (DomibusConnectorClientException e) {
			LOGGER.error("Exception occurred requesting new messages from connector!");
			throw e;
		}

		DomibusConnectorMessagesType mappedMessages = new DomibusConnectorMessagesType();
		
		if (messages!=null && !CollectionUtils.isEmpty(messages.getMessages())) {
			LOGGER.debug("{} new messages from connector to transport to client...", messages.getMessages().size());
			for(DomibusConnectorMessageType message:messages.getMessages()) {
				if(message.getMessageContent()!=null) {
					try {
						messageHandler.prepareInboundMessage(message);
					} catch (DomibusConnectorClientException e1) {
						LOGGER.error(e1);
						e1.printStackTrace();
						continue;
					}
				}
				mappedMessages.getMessages().add(message);
			}
		}

		return mappedMessages;
	}

	@Override
	public void triggerConfirmationForMessage(DomibusConnectorMessageType confirmationMessage) throws DomibusConnectorClientException {

		String refToMessageId = confirmationMessage.getMessageDetails()!=null?confirmationMessage.getMessageDetails().getRefToMessageId():null;

		if(confirmationMessage.getMessageDetails()==null || refToMessageId==null || refToMessageId.isEmpty()) {
			throw new DomibusConnectorClientException("The field [refToMessageId] in the messageDetails of the confirmationMessage must not be null! It must contain the ebmsId of the originalMessage that should be confirmed!");
		}

		if(confirmationMessage.getMessageConfirmations()==null || confirmationMessage.getMessageConfirmations().get(0) == null || confirmationMessage.getMessageConfirmations().get(0).getConfirmationType()==null) {
			throw new DomibusConnectorClientException("The confirmationMessage must contain one messageConfirmation. This messageConfirmation must contain the confirmationType that should be generated and submitted by the connector!");
		}
		DomibusConnectorConfirmationType confirmationType = confirmationMessage.getMessageConfirmations().get(0).getConfirmationType();

		confirmationMessage.setMessageDetails(setDummyValuesForConfirmationTrigger(confirmationMessage.getMessageDetails()));
		
		
		DomibsConnectorAcknowledgementType domibusConnectorAckType = new DomibsConnectorAcknowledgementType();
		try {
			LOGGER.debug("Submitting confirmation message with refToMessageId {} and confirmationType {} to connector.", refToMessageId, confirmationType.name());
			domibusConnectorAckType.setResult(true); //when no exception is thrown message is assumed processed successfully!
			domibusConnectorAckType = clientService.submitMessageToConnector(confirmationMessage);
		} catch (DomibusConnectorClientException e) {
			LOGGER.error("Exception submitting confirmation message to connector: ", e);
			throw e;
		} 

		if(domibusConnectorAckType == null) {
			LOGGER.error("The received acknowledgement for confirmation message with originalEbmsId {} and confirmationType {} is null! ");
			throw new DomibusConnectorClientException("The received acknowledgement for confirmation message with originalEbmsId "+refToMessageId+" and confirmationType "+confirmationType.name()+" is null!");
		}
		if(!domibusConnectorAckType.isResult()) {
			LOGGER.error("The received acknowledgement for confirmation message with originalEbmsId {} and confirmationType {} is negative! \n"
					+ "ResultMessage: "+domibusConnectorAckType.getResultMessage(), refToMessageId, confirmationType.name());
			throw new DomibusConnectorClientException("The received acknowledgement for confirmation message with originalEbmsId "+refToMessageId+" and confirmationType "+confirmationType.name()+" is negative! \n"
					+ "ResultMessage: "+domibusConnectorAckType.getResultMessage());
		}
	}
	
	//Workaround for domibusConnector versions 4.2.x and 4.3.x as it checks the presence of Parties when mapping transition to domain model
			//TODO: make it possible that the following empty action, service, parties are not required for evidence
	        //trigger message! if refToMessageId is set!
	private DomibusConnectorMessageDetailsType setDummyValuesForConfirmationTrigger(DomibusConnectorMessageDetailsType messageDetails) {
		DomibusConnectorPartyType dummyParty = new DomibusConnectorPartyType();
		dummyParty.setPartyId("DUMMY");
		
		messageDetails.setAction(new DomibusConnectorActionType());
		messageDetails.getAction().setAction("DummyAction");
		messageDetails.setService(new DomibusConnectorServiceType());
		messageDetails.getService().setService("DummyService");
		messageDetails.setFromParty(dummyParty);
		messageDetails.setToParty(dummyParty);
		messageDetails.setFinalRecipient("dummyRecipient");
		messageDetails.setOriginalSender("dummySender");
		
		return messageDetails;
	}
}

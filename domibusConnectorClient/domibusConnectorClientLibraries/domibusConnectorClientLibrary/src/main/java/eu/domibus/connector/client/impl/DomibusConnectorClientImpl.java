package eu.domibus.connector.client.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.DomibusConnectorClientMessageBuilder;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.DomibusConnectorClientLink;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorClientContentMapperException;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
public class DomibusConnectorClientImpl implements DomibusConnectorClient {
	
	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientImpl.class);

    @Autowired
    private DomibusConnectorClientContentMapper contentMapper;

    @Autowired
    private DomibusConnectorClientLink clientService;
    
    @Autowired
    private DomibusConnectorClientMessageBuilder messageBuilder;

	
	@Override
	public void submitNewMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException {
		 	MDC.put("backendmessageid", message.getMessageDetails().getBackendMessageId());
	        DomibsConnectorAcknowledgementType domibusConnectorAckType = new DomibsConnectorAcknowledgementType();
	        try {
	            contentMapper.mapOutboundBusinessContent(message);
	            domibusConnectorAckType.setResult(true); //when no exception is thrown message is assumed processed successfully!
	            domibusConnectorAckType = clientService.submitMessageToConnector(message);
	        } catch (DomibusConnectorClientException e) {
	            LOGGER.error("Exception submitting message to connector: ", e);
	            MDC.remove("backendmessageid");
	            throw e;
	        } catch (DomibusConnectorClientContentMapperException e) {
	            LOGGER.error("Exception while mapping outbound message: ", e);
	            MDC.remove("backendmessageid");
	            throw new DomibusConnectorClientException(e);
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
		
		if (messages!=null && !CollectionUtils.isEmpty(messages.getMessages())) {
            LOGGER.debug("{} new messages from connector to transport to client...", messages.getMessages().size());
            messages.getMessages().stream().forEach( message -> {

                try {
                	contentMapper.mapInboundBusinessContent(message);
                } catch (DomibusConnectorClientContentMapperException e) {
					LOGGER.error("Exception while mapping inbound message with ebmsId {}: ", message.getMessageDetails().getEbmsMessageId(), e);
					e.printStackTrace();
				}
            });
        }else {
        	throw new DomibusConnectorClientException("The received DomibusConnectorMessagesType from the connector is either null, or its containing collection is null or empty!");
        }
		
		return messages;
	}

	@Override
	public void triggerConfirmationForMessage(DomibusConnectorMessageType originalMessage,
			DomibusConnectorConfirmationType confirmationType) throws DomibusConnectorClientException {
		DomibusConnectorMessageType confirmationMessage = messageBuilder.createNewConfirmationMessage(originalMessage.getMessageDetails().getEbmsMessageId(), confirmationType);
		
		DomibsConnectorAcknowledgementType domibusConnectorAckType = new DomibsConnectorAcknowledgementType();
        try {
            domibusConnectorAckType.setResult(true); //when no exception is thrown message is assumed processed successfully!
            domibusConnectorAckType = clientService.submitMessageToConnector(confirmationMessage);
        } catch (DomibusConnectorClientException e) {
            LOGGER.error("Exception submitting confirmation message to connector: ", e);
            throw e;
        } 
        
        if(domibusConnectorAckType == null) {
        	LOGGER.error("The received acknowledgement for confirmation message with originalEbmsId {} and confirmationType {} is null! ");
            throw new DomibusConnectorClientException("The received acknowledgement for confirmation message with originalEbmsId "+originalMessage.getMessageDetails().getEbmsMessageId()+" and confirmationType "+confirmationType.value()+" is null!");
        }
        if(!domibusConnectorAckType.isResult()) {
        	LOGGER.error("The received acknowledgement for confirmation message with originalEbmsId {} and confirmationType {} is negative! \n"
        			+ "ResultMessage: "+domibusConnectorAckType.getResultMessage(), originalMessage.getMessageDetails().getEbmsMessageId(), confirmationType.value());
            throw new DomibusConnectorClientException("The received acknowledgement for confirmation message with originalEbmsId "+originalMessage.getMessageDetails().getEbmsMessageId()+" and confirmationType "+confirmationType.value()+" is negative! \n"
            		+ "ResultMessage: "+domibusConnectorAckType.getResultMessage());
        }
	}

}

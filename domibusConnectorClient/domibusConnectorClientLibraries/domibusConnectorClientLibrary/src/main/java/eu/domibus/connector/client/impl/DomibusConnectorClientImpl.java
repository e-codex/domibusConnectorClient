package eu.domibus.connector.client.impl;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClient;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.DomibusConnectorClientLink;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper;
import eu.domibus.connector.client.mapping.exception.DomibusConnectorClientContentMapperException;
import eu.domibus.connector.client.schema.validation.DomibusConnectorClientSchemaValidator;
import eu.domibus.connector.client.schema.validation.SeverityLevel;
import eu.domibus.connector.client.schema.validation.ValidationResult;
import eu.domibus.connector.client.spring.ConnectorClientAutoConfiguration;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;

@Component
@ConfigurationProperties(prefix = ConnectorClientAutoConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-library-default.properties")
@Validated
@Valid
public class DomibusConnectorClientImpl implements DomibusConnectorClient {

	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientImpl.class);

	@Autowired
	@Nullable
	private DomibusConnectorClientContentMapper contentMapper;

	@Autowired
	private DomibusConnectorClientLink clientService;
    
    @Autowired
    @Nullable
    private DomibusConnectorClientSchemaValidator schemaValidator;
    
    @Autowired
    @Nullable
    private SeverityLevel schemaValidationMaxSeverityLevel;
    
	
	@Override
	public void submitNewMessageToConnector(DomibusConnectorMessageType message) throws DomibusConnectorClientException {
		 	MDC.put("backendmessageid", message.getMessageDetails().getBackendMessageId());
	        DomibsConnectorAcknowledgementType domibusConnectorAckType = null;
	        
	        if(schemaValidator!=null) {
	        	ValidationResult result = schemaValidator.validateBusinessContentBeforeMapping(message);
	        	checkSchemaValidationResult(result);
	        	
	        }
	        
			if(contentMapper!=null) {
				LOGGER.debug("Instance of DomibusConnectorContentMapper found in context!");
				try {
					contentMapper.mapOutboundBusinessContent(message);
				} catch (DomibusConnectorClientContentMapperException e) {
					LOGGER.error("Exception while mapping outbound message: ", e);
					MDC.remove("backendmessageid");
					throw new DomibusConnectorClientException(e);
				}
			}
	        
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

	private void checkSchemaValidationResult(ValidationResult result) throws DomibusConnectorClientException {
		if(schemaValidationMaxSeverityLevel!=null && !result.isOkay()) {
			switch(schemaValidationMaxSeverityLevel) {
			case FATAL_ERROR: if(result.isFatal())throw new DomibusConnectorClientException("");
			case ERROR:if(result.isFatal()||result.isError())throw new DomibusConnectorClientException("");
			case WARNING:if(result.isFatal()||result.isError()||result.isWarning())throw new DomibusConnectorClientException("");
			}
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
				if(message.getMessageContent()!=null && contentMapper!=null) {
					LOGGER.debug("Instance of DomibusConnectorContentMapper found in context!");
					try {
						contentMapper.mapInboundBusinessContent(message);
					} catch (DomibusConnectorClientContentMapperException e) {
						LOGGER.error("Exception while mapping inbound message with ebmsId {}: ", message.getMessageDetails().getEbmsMessageId(), e);
						e.printStackTrace();
					}
				}
			});
			//        }else {
			//        	throw new DomibusConnectorClientException("The received DomibusConnectorMessagesType from the connector is either null, or its containing collection is null or empty!");
		}

		return messages;
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

}

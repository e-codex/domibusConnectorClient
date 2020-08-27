package eu.domibus.connector.client.impl;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import eu.domibus.connector.client.DomibusConnectorClientMessageHandler;
import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapper;
import eu.domibus.connector.client.mapping.DomibusConnectorClientContentMapperException;
import eu.domibus.connector.client.schema.validation.DCCAfterMappingSchemaValidator;
import eu.domibus.connector.client.schema.validation.DCCBeforeMappingSchemaValidator;
import eu.domibus.connector.client.schema.validation.DCCSchemaValidationException;
import eu.domibus.connector.client.schema.validation.SeverityLevel;
import eu.domibus.connector.client.schema.validation.ValidationResult;
import eu.domibus.connector.client.spring.ConnectorClientAutoConfiguration;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

@Component
@ConfigurationProperties(prefix = ConnectorClientAutoConfiguration.PREFIX)
@PropertySource("classpath:/connector-client-library-default.properties")
@Validated
@Valid
public class DomibusConnectorClientMessageHandlerImpl implements DomibusConnectorClientMessageHandler {
	
	private static final Logger LOGGER = LogManager.getLogger(DomibusConnectorClientMessageHandlerImpl.class);
	
	@Autowired
	@Nullable
	private DomibusConnectorClientContentMapper contentMapper;
    
    @Autowired
    @Nullable
    private DCCBeforeMappingSchemaValidator beforeMappingSchemaValidator;
    
    @Autowired
    @Nullable
    private DCCAfterMappingSchemaValidator afterMappingSchemaValidator;
    
    @Autowired
    @Nullable
    private SeverityLevel schemaValidationMaxSeverityLevel;
  
	/* (non-Javadoc)
	 * @see eu.domibus.connector.client.impl.IDomibusConnectorClientMessageHandler#prepareMessage(eu.domibus.connector.domain.transition.DomibusConnectorMessageType, eu.domibus.connector.client.impl.DomibusConnectorClientMessageHandler.Direction)
	 */
    @Override
	public void prepareMessage(DomibusConnectorMessageType message, Direction direction)
			throws DomibusConnectorClientException {
		 if(beforeMappingSchemaValidator!=null) {
	        	LOGGER.debug("Instance of DCCBeforeMappingSchemaValidator found in context!");
	        	ValidationResult result = beforeMappingSchemaValidator.validateBusinessContentBeforeMapping(message);
	        	result.printValidationResults(LOGGER);
	        	try {
					checkSchemaValidationResult(result);
				} catch (DCCSchemaValidationException e) {
					throw new DomibusConnectorClientException("Schema Validation before mapping has results of max severity level "+schemaValidationMaxSeverityLevel.name()+" or higher! ",e);
					
				}
	        }else {
	        	LOGGER.debug("No instance of DCCBeforeMappingSchemaValidator found in context!");
	        }
	        
			if(contentMapper!=null) {
				LOGGER.debug("Instance of DomibusConnectorContentMapper found in context!");
				switch(direction) {
				case INBOUND:
					try {
						contentMapper.mapInboundBusinessContent(message);
					} catch (DomibusConnectorClientContentMapperException e) {
						throw new DomibusConnectorClientException("Exception while mapping inbound message with ebmsId: "+ message.getMessageDetails().getEbmsMessageId(),e);
					}
					break;
				case OUTBOUND:
					try {
						contentMapper.mapOutboundBusinessContent(message);
					} catch (DomibusConnectorClientContentMapperException e) {
						throw new DomibusConnectorClientException("Exception while mapping outbound message!",e);
					}
					break;
					
				}
			}else {
	        	LOGGER.debug("No instance of DomibusConnectorContentMapper found in context!");
			}
			
			if(afterMappingSchemaValidator!=null) {
	        	LOGGER.debug("Instance of DCCAfterMappingSchemaValidator found in context!");
	        	ValidationResult result = afterMappingSchemaValidator.validateBusinessContentAfterMapping(message);
	        	result.printValidationResults(LOGGER);
	        	try {
					checkSchemaValidationResult(result);
				} catch (DCCSchemaValidationException e) {
					throw new DomibusConnectorClientException("Schema Validation after mapping has results of max severity level "+schemaValidationMaxSeverityLevel.name()+" or higher! ",e);
					
				}
			}else {
	        	LOGGER.debug("No instance of DCCAfterMappingSchemaValidator found in context!");
	        }
	}
	
	private void checkSchemaValidationResult(ValidationResult result) throws DCCSchemaValidationException {
		LOGGER.debug("Checking schema validation results against maxSeverityLevel {}", schemaValidationMaxSeverityLevel);
		if(schemaValidationMaxSeverityLevel!=null && !result.isOkay()) {
			switch(schemaValidationMaxSeverityLevel) {
			case FATAL_ERROR: 
				if(result.isFatal())
					throw new DCCSchemaValidationException("ValidationResult contains results of severity level "+SeverityLevel.FATAL_ERROR.name()+" or higher!") ;
			case ERROR:
				if(result.isFatal()||result.isError())
					throw new DCCSchemaValidationException("ValidationResult contains results of severity level "+SeverityLevel.ERROR.name()+" or higher!");
			case WARNING:
				if(result.isFatal()||result.isError()||result.isWarning())
					throw new DCCSchemaValidationException("ValidationResult contains results of severity level "+SeverityLevel.WARNING.name()+" or higher!");
			}
		}
	}


	public SeverityLevel getSchemaValidationMaxSeverityLevel() {
		return schemaValidationMaxSeverityLevel;
	}


	public void setSchemaValidationMaxSeverityLevel(SeverityLevel schemaValidationMaxSeverityLevel) {
		this.schemaValidationMaxSeverityLevel = schemaValidationMaxSeverityLevel;
	}


}

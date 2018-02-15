
package eu.domibus.connector.v35client;

import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.nbc.exception.DomibusConnectorNationalBackendClientException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This Service maps the old {@link eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient} 
 * to the new webservice
 * this is part of the client35 lib
 * 
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class TranslateBetween35And4Client {

    private final static Logger LOGGER = LoggerFactory.getLogger(TranslateBetween35And4Client.class);
    
    @Autowired
    DomibusConnectorNationalBackendClient nationalBackendClient;
    
    
    
    //must be called by timer job!
    public void transportMessagesToController() {
        try {
            String[] unsentMessageIds = nationalBackendClient.requestMessagesUnsent();
            
            for (String id : unsentMessageIds) {
                transportOneMessageToController(id);
            }            
        } catch (DomibusConnectorNationalBackendClientException clientException) {
            String error = "clientException from national backend occured!";
            LOGGER.error(error, clientException);
            throw new RuntimeException(clientException);
        } catch (ImplementationMissingException ex) {            
            throw new RuntimeException(ex);            
        }        
    }
    
    void transportOneMessageToController(String id) {
        try {
            Message nationalMessage = getMessageFromNational(id);
            
        } catch (Exception e) {            
            String error = String.format("Sending national message with id [%s] to domibusConnector failed!", id);            
            LOGGER.error(error, e);
        }
    }
    

        
    Message getMessageFromNational(String id) {
        try {
            MessageDetails messageDetails = new MessageDetails();
            MessageContent messageContent = new MessageContent();
            Message message = new Message(messageDetails, messageContent);
            message.getMessageDetails().setNationalMessageId(id);
            nationalBackendClient.requestMessage(message);
            return message;
        } catch (DomibusConnectorNationalBackendClientException | ImplementationMissingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    
    
}

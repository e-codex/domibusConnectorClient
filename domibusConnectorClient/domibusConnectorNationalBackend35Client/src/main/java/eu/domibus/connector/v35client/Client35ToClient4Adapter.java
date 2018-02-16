
package eu.domibus.connector.v35client;

import eu.domibus.connector.client.connection.SubmitMessageToConnector;
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
import org.springframework.stereotype.Service;

/**
 * This Service maps the old {@link eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient} 
 * to the new service a adapter between the client35 and client4
 * this is part of the client35 lib
 * 
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Service
public class Client35ToClient4Adapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(Client35ToClient4Adapter.class);
    
    @Autowired
    DomibusConnectorNationalBackendClient nationalBackendClient;
    
    @Autowired
    SubmitMessageToConnector submitMessageToConnectorService;
    
    @Autowired
    Map35MessageTov4Message map35MessageTov4Message;


    //SETTER
    public void setNationalBackendClient(DomibusConnectorNationalBackendClient nationalBackendClient) {
        this.nationalBackendClient = nationalBackendClient;
    }

    public void setSubmitMessageToConnectorService(SubmitMessageToConnector submitMessageToConnectorService) {
        this.submitMessageToConnectorService = submitMessageToConnectorService;
    }
    public void setMap35MessageTov4Message(Map35MessageTov4Message map35MessageTov4Message) {
        this.map35MessageTov4Message = map35MessageTov4Message;
    }
    

    //TODO: must be called by timer job!
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
            DomibusConnectorMessage domibusMessage = map35MessageTov4Message.map35MessageTov4Message(nationalMessage);
            submitMessageToConnectorService.submitMessage(domibusMessage);            
        } catch (Exception e) {            
            String error = String.format("Sending national message with id [%s] to domibusConnector failed!", id);            
            LOGGER.error(error, e);
            //TODO: mark message as failed! AND INFORM client!
            //Maybe create a NonDelivery Message?
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
    
    
    //TODO: transport messages from controller to National
    
}

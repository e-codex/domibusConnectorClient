
package eu.domibus.connector.v35client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.domibus.connector.client.connection.ws.DomibusConnectorBackendWebServiceClient;
import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.nbc.exception.DomibusConnectorNationalBackendClientException;

/**
 * This Service maps the old {@link eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient} 
 * to the new service a adapter between the client35 and client4
 * this is part of the client35 lib
 * 
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Service
public class PushMessagesToControllerFrom35Client {

    private final static Logger LOGGER = LoggerFactory.getLogger(PushMessagesToControllerFrom35Client.class);
    
    
    private DomibusConnectorNationalBackendClient nationalBackendClient;
        
    private DomibusConnectorBackendWebServiceClient backendClient;
        
    private Map35MessageTov4Message map35MessageTov4Message;
    
    private DomibusConnectorContentMapper domibusConnectorContentMapper;


    //SETTER
    @Autowired
    public void setNationalBackendClient(DomibusConnectorNationalBackendClient nationalBackendClient) {
        this.nationalBackendClient = nationalBackendClient;
    }
    
    @Autowired
    public void setBackendClient(DomibusConnectorBackendWebServiceClient backendClient) {
    	this.backendClient = backendClient;
    }
    
    @Autowired
    public void setMap35MessageTov4Message(Map35MessageTov4Message map35MessageTov4Message) {
        this.map35MessageTov4Message = map35MessageTov4Message;
    }
    
    @Autowired
    public void setDomibusConnectorContentMapper(DomibusConnectorContentMapper domibusConnectorContentMapper) {
        this.domibusConnectorContentMapper = domibusConnectorContentMapper;
    }
    

    //TODO: make configureable!
    @Scheduled(fixedDelay = 9000)
    public void transportMessagesToController() {
        try {
            String[] unsentMessageIds = nationalBackendClient.requestMessagesUnsent();
            
            for (String id : unsentMessageIds) {
                transportOneMessageToController(id);
            }            
        } catch (DomibusConnectorNationalBackendClientException clientException) {
            String error = "#transportMessagesToController: clientException from national backend occured while retrieving national-ids of unsentMessages!";
            LOGGER.error(error, clientException);
            throw new RuntimeException(clientException);
        } catch (ImplementationMissingException ex) {
            throw new RuntimeException(ex);
        }        
    }
    
    
    void transportOneMessageToController(String id) {
        
        try {            
            Message nationalMessage = getMessageFromNational(id);  
            
            LOGGER.info("#transportOneMessageToController: calling content mapper to map content from national to international");
            domibusConnectorContentMapper.mapNationalToInternational(nationalMessage);
            
            LOGGER.info("#transportOneMessageToController: converting from old message format (v35) to new message format (v4)");
            DomibusConnectorMessageType domibusMessage = map35MessageTov4Message.map35MessageTov4Message(nationalMessage);
            
            LOGGER.info("#transportOneMessageToController: passing message to SubmitMessageToConnector service");
            backendClient.submitMessage(domibusMessage);            
        } catch (Exception e) {            
            String error = String.format("#transportOneMessageToController: sending national message with id [%s] to domibusConnector failed!", id);            
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
            LOGGER.info("Requesting message with id [{}] from national system", id);
            nationalBackendClient.requestMessage(message);
            return message;
        } catch (DomibusConnectorNationalBackendClientException | ImplementationMissingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    

    
}

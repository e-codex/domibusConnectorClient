
package eu.domibus.connector.v35client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.link.DomibusConnectorClientLink;
import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.nbc.exception.DomibusConnectorNationalBackendClientException;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Service
@Profile("!pushfromcontroller") //only active if push profile is not activated!
public class PullMessagesFromControllerTo35Client {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PullMessagesFromControllerTo35Client.class);
        
    private DomibusConnectorNationalBackendClient nationalBackendClient;
        
    private DomibusConnectorClientLink backendClient;
        
    private MapV4TransitionMessageTo35Message mapTo35Message;
    
    private DomibusConnectorContentMapper domibusConnectorContentMapper;
    
    //SETTER
    @Autowired
    public void setNationalBackendClient(DomibusConnectorNationalBackendClient nationalBackendClient) {
        this.nationalBackendClient = nationalBackendClient;
    }
    
   
    @Autowired
    public void setBackendClient(DomibusConnectorClientLink backendClient) {
		this.backendClient = backendClient;
	}



	@Autowired
    public void setMapTo35Message(MapV4TransitionMessageTo35Message mapTo35Message) {
        this.mapTo35Message = mapTo35Message;
    }

    @Autowired
    public void setDomibusConnectorContentMapper(DomibusConnectorContentMapper domibusConnectorContentMapper) {
        this.domibusConnectorContentMapper = domibusConnectorContentMapper;
    }
    
    
    public void pullMessagesFromController() {
        List<DomibusConnectorMessageType> fetchMessages = null;
		try {
			DomibusConnectorMessagesType messages = backendClient.requestMessagesFromConnector();
			fetchMessages = messages.getMessages();
		} catch (DomibusConnectorClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        fetchMessages.forEach(this::deliverMessageToNationalSystem);
    }
    
    void deliverMessageToNationalSystem(DomibusConnectorMessageType msg) {
        try {
            Message mappedMessage = mapTo35Message.mapMessageTo35Message(msg);
            if (TransitionModelHelper.isEvidenceMessage(msg)) {
                LOGGER.debug("#deliverMessageToNationalSystem: message is evidenceMessage");
                nationalBackendClient.deliverLastEvidenceForMessage(mappedMessage);
            } else {
                LOGGER.debug("#deliverMessageToNationalSystem: message is message with content");
                domibusConnectorContentMapper.mapInternationalToNational(mappedMessage);
                nationalBackendClient.deliverMessage(mappedMessage);
            }
        } catch (DomibusConnectorNationalBackendClientException backendClientException) {
            String error = String.format("Failed to deliver message [%s] to national backend", msg);
            putMessageOnErrorQueue(msg);
            LOGGER.error(error, backendClientException);
            throw new RuntimeException(backendClientException);
        } catch (ImplementationMissingException implementationMissingException) {
            LOGGER.error("National implementation is missing, cannot deliver message!", implementationMissingException);            
            putMessageOnErrorQueue(msg);
            throw new RuntimeException(implementationMissingException);
        } catch (DomibusConnectorContentMapperException ex) {
            String error = String.format("Failed to map international content to national content of message [%s]", msg);
            putMessageOnErrorQueue(msg);
            LOGGER.error(error, ex);
            throw new RuntimeException(ex);
        }                
    }
    
    void putMessageOnErrorQueue(DomibusConnectorMessageType msg) {
        //TODO: implement error queue
        LOGGER.error("ERROR QUEUE NOT IMPLEMENTED YET, MESSAGE [{}] WILL NOT BE STORED", msg);
    }
    
}

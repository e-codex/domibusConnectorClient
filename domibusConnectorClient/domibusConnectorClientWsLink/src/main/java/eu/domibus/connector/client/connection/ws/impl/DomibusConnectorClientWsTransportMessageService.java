
package eu.domibus.connector.client.connection.ws.impl;

import eu.domibus.connector.client.connection.FetchMessagesFromConnector;
import eu.domibus.connector.client.connection.SubmitMessageToConnector;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Service
public class DomibusConnectorClientWsTransportMessageService implements FetchMessagesFromConnector, SubmitMessageToConnector {

    private final static Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientWsTransportMessageService.class);
    
    @Resource(name="connectorWsClient")
    DomibusConnectorBackendWebService webService;

    public void setWebService(DomibusConnectorBackendWebService webService) {
        this.webService = webService;
    }
    
    @Override
    public List<DomibusConnectorMessageType> fetchMessages() {
        LOGGER.debug("#fetchMessages: fetchMessages from connector");
        DomibusConnectorMessagesType requestMessages = webService.requestMessages(new EmptyRequestType());
        
        List<DomibusConnectorMessageType> messages = requestMessages.getMessages();
        LOGGER.debug("#fetchMessages: successfully fetched [{}] messages from connector", messages.size());

        return messages;
    }

    @Override
    public DomibsConnectorAcknowledgementType submitMessage(DomibusConnectorMessageType messageDTO) {  
        LOGGER.debug("#submitMessage: submitting message [{}] to webServiceClient", messageDTO);
        DomibsConnectorAcknowledgementType submitMessageAck = webService.submitMessage(messageDTO);
                
        return submitMessageAck;
    }

}

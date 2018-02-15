
package eu.domibus.connector.client.connection.ws.impl;

import eu.domibus.connector.client.connection.FetchMessagesFromConnector;
import eu.domibus.connector.client.connection.SubmitMessageToConnector;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformer;
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
    public List<DomibusConnectorMessage> fetchMessages() {
        List<DomibusConnectorMessage> messages = new ArrayList<>();
        DomibusConnectorMessagesType requestMessages = webService.requestMessages(new EmptyRequestType());
        
        requestMessages.getMessages().forEach( (DomibusConnectorMessageType msg) -> {
            DomibusConnectorMessage domainMessage = DomibusConnectorDomainMessageTransformer.transformTransitionToDomain(msg);
            messages.add(domainMessage);
        });
        
        return messages;
    }

    @Override
    public DomibusConnectorMessage submitMessage(DomibusConnectorMessage message) {
        DomibusConnectorMessageType messageDTO = DomibusConnectorDomainMessageTransformer.transformDomainToTransition(message);
        DomibsConnectorAcknowledgementType submitMessage = webService.submitMessage(messageDTO);
        String messageId = submitMessage.getMessageId();
        message.setConnectorMessageId(messageId);
        return message;
    }

}

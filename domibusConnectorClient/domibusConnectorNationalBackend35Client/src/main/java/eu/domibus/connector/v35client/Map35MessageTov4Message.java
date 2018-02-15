
package eu.domibus.connector.v35client;

import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.domain.model.builder.DomibusConnectorActionBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageContentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorPartyBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorServiceBuilder;
import javax.annotation.Nullable;
import org.springframework.beans.BeanUtils;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class Map35MessageTov4Message {

    
    
    
    DomibusConnectorMessage map35MessageTov4Message(Message msg) {

        DomibusConnectorMessageDetails mapMessageDetails = mapMessageDetails(msg.getMessageDetails());
        
//        details.setAction(action); msg.getMessageDetails().getAction();
//        
//        
//        DomibusConnectorMessageBuilder.createBuilder()
//                .setMessageDetails(msgDetails)
        return null;
    }
    
    DomibusConnectorMessageContent mapMessageContent(MessageContent oldMessageContent) {
        
        return DomibusConnectorMessageContentBuilder.createBuilder()
                .setXmlContent(oldMessageContent.getInternationalContent())
                .build();   
    }
    
    
    DomibusConnectorMessageDetails mapMessageDetails(MessageDetails oldMessageDetails) {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();
        
        BeanUtils.copyProperties(oldMessageDetails, messageDetails);
        
        messageDetails.setBackendMessageId(oldMessageDetails.getNationalMessageId());
        
        messageDetails.setAction(mapDomibusConnectorAction(oldMessageDetails.getAction()));        
        messageDetails.setFromParty(mapDomibusConnectorParty(oldMessageDetails.getFromParty()));
        messageDetails.setService(mapDomibusConnectorService(oldMessageDetails.getService()));
        messageDetails.setToParty(mapDomibusConnectorParty(oldMessageDetails.getToParty()));
                
        return messageDetails;
    }
    
    @Nullable DomibusConnectorAction mapDomibusConnectorAction(@Nullable eu.domibus.connector.common.db.model.DomibusConnectorAction oldAction) {
        if (oldAction == null) return null;
        return DomibusConnectorActionBuilder.createBuilder()
                .setAction(oldAction.getAction())
                .withDocumentRequired(oldAction.isPdfRequired())
                .build();
    }
    
    @Nullable DomibusConnectorParty mapDomibusConnectorParty(@Nullable eu.domibus.connector.common.db.model.DomibusConnectorParty oldParty) {
        if (oldParty == null) return null;
        return DomibusConnectorPartyBuilder.createBuilder()
                .setPartyId(oldParty.getPartyId())
                .setRole(oldParty.getRole())
                .withPartyIdType(oldParty.getPartyIdType())
                .build();
    }
    
    @Nullable DomibusConnectorService mapDomibusConnectorService(@Nullable eu.domibus.connector.common.db.model.DomibusConnectorService oldService) {
        if (oldService == null) return null;
        return DomibusConnectorServiceBuilder.createBuilder()
                .setService(oldService.getService())
                .withServiceType(oldService.getServiceType())
                .build();
    }
    
}

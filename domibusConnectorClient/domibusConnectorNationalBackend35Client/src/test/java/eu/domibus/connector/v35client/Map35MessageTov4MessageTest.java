
package eu.domibus.connector.v35client;


import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.util.StreamUtils;

/**
 *
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */
public class Map35MessageTov4MessageTest {

    Map35MessageTov4Message mapper = new Map35MessageTov4Message();

    
    @Test
    public void testMap35MessageTov4Message() {
        Message message = new Message(createMessageDetails(), createMessageContent());
        
        DomibusConnectorMessage msg = mapper.map35MessageTov4Message(message);
        
        assertThat(msg).isNotNull();
        assertThat(msg.getMessageDetails()).isNotNull();
        assertThat(msg.getMessageContent()).isNotNull();
        
        //TODO: check message errors, attachments, confirmations!
        
    }
    

    
    @Test
    public void testMapMessageContent() throws UnsupportedEncodingException, IOException {
       
        MessageContent messageContent = createMessageContent();
        
        
        DomibusConnectorMessageContent mapMessageContent = mapper.mapMessageContent(messageContent);
        
        assertThat(mapMessageContent).isNotNull();
        
        assertThat(new String(mapMessageContent.getXmlContent(), "UTF-8")).isEqualTo("internationalContent");
        
        DomibusConnectorMessageDocument document = mapMessageContent.getDocument();
        assertThat(document).isNotNull();
        
        assertThat(document.getDocumentName()).isEqualTo("pdfDocumentName");
        assertThat(new String(StreamUtils.copyToByteArray(document.getDocument().getInputStream()), "UTF-8")).isEqualTo("pdfDocument");
        
        DetachedSignature detachedSignature = document.getDetachedSignature();
        assertThat(detachedSignature).isNotNull();
        assertThat(detachedSignature.getDetachedSignatureName()).isEqualTo("detachedSignatureName");
        assertThat(new String(detachedSignature.getDetachedSignature(), "UTF-8")).isEqualTo("detachedSignature");
        assertThat(detachedSignature.getMimeType()).isEqualTo(DetachedSignatureMimeType.BINARY);
    }

    MessageContent createMessageContent() {
        MessageContent messageContent = new MessageContent();
        messageContent.setDetachedSignature("detachedSignature".getBytes());
        messageContent.setDetachedSignatureMimeType(eu.domibus.connector.common.enums.DetachedSignatureMimeType.BINARY);
        messageContent.setDetachedSignatureName("detachedSignatureName");
        messageContent.setInternationalContent("internationalContent".getBytes()); 
        messageContent.setNationalXmlContent(null); //not mapped!
        messageContent.setPdfDocument("pdfDocument".getBytes());
        messageContent.setPdfDocumentName("pdfDocumentName");
        return messageContent;
    }
    
    @Test
    public void testMapMessageDetails() {
        
        MessageDetails messageDetails = createMessageDetails();
        
        DomibusConnectorMessageDetails mapMessageDetails = mapper.mapMessageDetails(messageDetails);
        assertThat(mapMessageDetails).isNotNull();
        
        assertThat(mapMessageDetails.getAction()).isNotNull();
        assertThat(mapMessageDetails.getBackendMessageId()).isEqualTo("nationalMessageId");
        assertThat(mapMessageDetails.getConversationId()).isEqualTo("conversationId");
        assertThat(mapMessageDetails.getFinalRecipient()).isEqualTo("finalRecipient");
        assertThat(mapMessageDetails.getOriginalSender()).isEqualTo("originalSender");
        assertThat(mapMessageDetails.getRefToMessageId()).isEqualTo("refToMessageId");
        assertThat(mapMessageDetails.getAction()).isNotNull();
        //TODO: compare action
        assertThat(mapMessageDetails.getFromParty()).isNotNull();
        //TODO: compare party
        assertThat(mapMessageDetails.getService()).isNotNull();
        //TODO: compare service
        assertThat(mapMessageDetails.getToParty()).isNotNull();
        //TODO: compare party
    }
    
    public MessageDetails createMessageDetails() {
        MessageDetails messageDetails = new MessageDetails();
        
        eu.domibus.connector.common.db.model.DomibusConnectorAction action = new eu.domibus.connector.common.db.model.DomibusConnectorAction();
        action.setAction("action");
                
        messageDetails.setAction(action);
        messageDetails.setConversationId("conversationId");
        messageDetails.setEbmsMessageId("ebmsMessageId");
        messageDetails.setFinalRecipient("finalRecipient");
        
        eu.domibus.connector.common.db.model.DomibusConnectorParty party = createParty();
        
        messageDetails.setFromParty(party);
        messageDetails.setNationalMessageId("nationalMessageId");
        messageDetails.setOriginalSender("originalSender");
        messageDetails.setRefToMessageId("refToMessageId");
        
        eu.domibus.connector.common.db.model.DomibusConnectorService service = createService();
        
        messageDetails.setService(service);
        messageDetails.setToParty(party);
        return messageDetails;
    }
    
    public eu.domibus.connector.common.db.model.DomibusConnectorParty createParty() {
        eu.domibus.connector.common.db.model.DomibusConnectorParty party = new eu.domibus.connector.common.db.model.DomibusConnectorParty();
        party.setPartyId("partyId");
        party.setPartyIdType("partyIdType");
        party.setRole("role");
        return party;
    }
    
    public eu.domibus.connector.common.db.model.DomibusConnectorService createService() {
        eu.domibus.connector.common.db.model.DomibusConnectorService service = new eu.domibus.connector.common.db.model.DomibusConnectorService();
        service.setService("service");
        service.setServiceType("serviceType");
        return service;
    }
    

}
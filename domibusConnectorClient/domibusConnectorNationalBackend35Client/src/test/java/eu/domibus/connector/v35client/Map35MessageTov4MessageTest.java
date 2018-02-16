
package eu.domibus.connector.v35client;


import eu.domibus.connector.common.enums.EvidenceType;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageAttachment;
import eu.domibus.connector.common.message.MessageConfirmation;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        Message message = createv35TestMessage();
        
        DomibusConnectorMessage msg = mapper.map35MessageTov4Message(message);
        
        assertThat(msg).as("message must not be null").isNotNull();
        assertThat(msg.getMessageDetails()).as("message details must not be null").isNotNull();
        assertThat(msg.getMessageContent()).as("message content must not be null!").isNotNull();
        
        assertThat(msg.getMessageAttachments()).hasSize(1);
        assertThat(msg.getMessageConfirmations()).hasSize(1);
        
        //TODO: check message errors, attachments, confirmations!        
    }
    
    
    private Message createv35TestMessage() {
        Message message = new Message(createMessageDetails(), createMessageContent());
        
        //byte[] attachment, String identifier
        MessageAttachment attach1 = new MessageAttachment("attachment".getBytes(), "attach1");
        attach1.setDescription("description");
        attach1.setMimeType("application/pdf");
        attach1.setName("name");        
        message.addAttachment(attach1);
        
        MessageConfirmation confirm1 = new MessageConfirmation();
        confirm1.setEvidence("evidence".getBytes());
        confirm1.setEvidenceType(EvidenceType.DELIVERY);
        message.addConfirmation(confirm1);
                
        return message;
    }
    
    @Test
    public void testMap35MessageTov4Message_isEvidenceMessage() {
        Message message = createv35TestEvidenceMessage();
        
        DomibusConnectorMessage msg = mapper.map35MessageTov4Message(message);
        
        assertThat(msg).as("message must not be null").isNotNull();
        assertThat(msg.getMessageDetails()).as("message details must not be null").isNotNull();
        assertThat(msg.getMessageContent()).as("message content must be null (evidence message)!").isNull();
        
        assertThat(msg.getMessageAttachments()).hasSize(1);
        assertThat(msg.getMessageConfirmations()).hasSize(2);
             
    }
    
    private Message createv35TestEvidenceMessage() {
            
        try {
            MessageConfirmation confirmation = new MessageConfirmation();
            confirmation.setEvidence("evidence".getBytes("UTF-8"));
            confirmation.setEvidenceType(EvidenceType.RELAY_REMMD_ACCEPTANCE);
            
            Message message = new Message(createMessageDetails(), confirmation);
            
            //byte[] attachment, String identifier
            MessageAttachment attach1 = new MessageAttachment("attachment".getBytes(), "attach1");
            attach1.setDescription("description");
            attach1.setMimeType("application/pdf");
            attach1.setName("name");
            message.addAttachment(attach1);
            
            MessageConfirmation confirm1 = new MessageConfirmation();
            confirm1.setEvidence("evidence".getBytes());
            confirm1.setEvidenceType(EvidenceType.DELIVERY);
            message.addConfirmation(confirm1);
            
            return message;
        } catch (UnsupportedEncodingException ex) {
           throw new RuntimeException(ex);
        }
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

    @Test
    public void testMapMessageContent_signatureIsNull() throws UnsupportedEncodingException, IOException {
       
        MessageContent messageContent = createMessageContent();
        messageContent.setDetachedSignature(null);
        
        DomibusConnectorMessageContent mapMessageContent = mapper.mapMessageContent(messageContent);
        
        assertThat(mapMessageContent).isNotNull();
        
        assertThat(new String(mapMessageContent.getXmlContent(), "UTF-8")).isEqualTo("internationalContent");
        
        DomibusConnectorMessageDocument document = mapMessageContent.getDocument();
        assertThat(document).isNotNull();
        
        assertThat(document.getDocumentName()).isEqualTo("pdfDocumentName");
        assertThat(new String(StreamUtils.copyToByteArray(document.getDocument().getInputStream()), "UTF-8")).isEqualTo("pdfDocument");
        
        DetachedSignature detachedSignature = document.getDetachedSignature();
        assertThat(detachedSignature).isNull();        
    }
    
    @Test
    public void testMapMessageContent_documentIsNull() throws UnsupportedEncodingException, IOException {
       
        MessageContent messageContent = createMessageContent();
        messageContent.setPdfDocument(null);
        
        DomibusConnectorMessageContent mapMessageContent = mapper.mapMessageContent(messageContent);
        
        assertThat(mapMessageContent).isNotNull();
        
        assertThat(new String(mapMessageContent.getXmlContent(), "UTF-8")).isEqualTo("internationalContent");
        
        DomibusConnectorMessageDocument document = mapMessageContent.getDocument();
        assertThat(document).isNull();
        
//        assertThat(document.getDocumentName()).isEqualTo("pdfDocumentName");
//        assertThat(new String(StreamUtils.copyToByteArray(document.getDocument().getInputStream()), "UTF-8")).isEqualTo("pdfDocument");
        
      
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
    


    
    @Test
    public void testMapAttachment() throws UnsupportedEncodingException, IOException {
        MessageAttachment attachment = createAttachment();

        DomibusConnectorMessageAttachment mapAttachment = mapper.mapAttachment(attachment);
        
        assertThat(mapAttachment.getDescription()).isEqualTo("description");
        assertThat(mapAttachment.getMimeType()).isEqualTo("application/pdf");
        assertThat(mapAttachment.getName()).isEqualTo("name");
        
        byte[] attachContent = StreamUtils.copyToByteArray(mapAttachment.getAttachment().getInputStream());
        assertThat(new String(attachContent, "UTF-8")).isEqualTo("attachment");
        
    }
    
    public MessageAttachment createAttachment() {
        MessageAttachment attachment = new MessageAttachment("attachment".getBytes(), "attach1");
        attachment.setDescription("description");
        attachment.setMimeType("application/pdf");
        attachment.setName("name");   
        return attachment;
    }
    
    
    /**
     * Test that each DetachedSignatureMimeType can be mapped
     *  does not test correctnes of mapping!
     */
    @Test
    public void testMapDetachedSignatureMimeType() {
        Arrays.asList(eu.domibus.connector.common.enums.DetachedSignatureMimeType.values()).forEach(
                mimeType -> mapper.mapDetachedSignatureMimeType(mimeType)
        );
    }
    
    /**
     * test that each EvidenceType can be mapped to 
     * DomibusConnectorEvidenceType
     *  does not test correctnes of mapping!
     */
    @Test
    public void testMapEvidenceType() {
        Arrays.asList(EvidenceType.values()).forEach(
                evidenceType -> mapper.mapEvidenceType(evidenceType)
        );
    }
    
    

}

package eu.domibus.connector.v35client;

import eu.domibus.connector.common.db.model.DomibusConnectorAction;
import eu.domibus.connector.common.db.model.DomibusConnectorParty;
import eu.domibus.connector.common.db.model.DomibusConnectorService;
import eu.domibus.connector.common.enums.DetachedSignatureMimeType;
import eu.domibus.connector.common.enums.EvidenceType;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageAttachment;
import eu.domibus.connector.common.message.MessageConfirmation;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import eu.domibus.connector.domain.transition.testutil.TransitionCreator;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.assertj.core.api.AbstractByteArrayAssert;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.DOMDifferenceEngine;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEngine;

/**
 *
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */
public class MapV4TransitionMessageTo35MessageTest {

    private MapV4TransitionMessageTo35Message mapService;

    private static final String XMLUTF8PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

    @Before
    public void before() {
        mapService = new MapV4TransitionMessageTo35Message();
    }

    @Test
    public void testMapMessageTo35Message() {
        DomibusConnectorMessageType messageTO = TransitionCreator.createMessage();
        
        Message message = mapService.mapMessageTo35Message(messageTO);
        
        assertThat(message).isNotNull();
        assertThat(message.getMessageContent()).isNotNull();
        assertThat(message.getMessageDetails()).isNotNull();
        
        
        assertThat(message.getAttachments()).hasSize(1);
        assertThat(message.getConfirmations()).hasSize(1);
        
    }
    
    @Test
    public void testMapMessageTo35Message_nonDeliveryMessage() {
        DomibusConnectorMessageType messageTO = TransitionCreator.createEvidenceNonDeliveryMessage();
        
        Message message = mapService.mapMessageTo35Message(messageTO);
        
        assertThat(message).isNotNull();
        assertThat(message.getMessageContent()).isNull();
        assertThat(message.getMessageDetails()).isNotNull();
        
        assertThat(message.getConfirmations()).hasSize(1);        
    }
    
    @Test
    public void testMapMessageTo35Message_epoMessage() {
        DomibusConnectorMessageType messageTO = TransitionCreator.createEpoMessage();
        
        Message message = mapService.mapMessageTo35Message(messageTO);
        
        assertThat(message).isNotNull();
        assertThat(message.getMessageContent()).isNotNull();
        assertThat(message.getMessageDetails()).isNotNull();          
    }
    
    @Test
    public void testMapMessageConfirmation() {
        DomibusConnectorMessageConfirmationType confirmationDeliveryTO = TransitionCreator.createMessageConfirmationType_DELIVERY();
        MessageConfirmation deliveryConfirmation = mapService.mapMessageConfirmation(confirmationDeliveryTO);
        
        assertThat(deliveryConfirmation).isNotNull();
        ByteArrayStringAssert.assertThat(deliveryConfirmation.getEvidence()).isXml(XMLUTF8PREFIX+"<DELIVERY></DELIVERY>");
        assertThat(deliveryConfirmation.getEvidenceType()).isEqualTo(EvidenceType.DELIVERY);        
    }
    
    @Test
    public void testMapAttachment() {
        DomibusConnectorMessageAttachmentType attachmentTO = TransitionCreator.createMessageAttachment();
        MessageAttachment mapMessageAttachment = mapService.mapMessageAttachment(attachmentTO);
        
        assertThat(mapMessageAttachment).isNotNull();
        ByteArrayStringAssert.assertThat(mapMessageAttachment.getAttachment()).isString("attachment");
        assertThat(mapMessageAttachment.getDescription()).isEqualTo("description");
        assertThat(mapMessageAttachment.getIdentifier()).isEqualTo("identifier");
        assertThat(mapMessageAttachment.getMimeType()).isEqualTo(TransitionCreator.APPLICATION_OCTET_STREAM_MIME_TYPE);
        assertThat(mapMessageAttachment.getName()).isEqualTo("name");
    }
    
    
    @Test
    public void testMapMessageDetails() {
        DomibusConnectorMessageDetailsType messageDetailsTO = TransitionCreator.createMessageDetails();
        
        MessageDetails messageDetails = mapService.mapMessageDetails(messageDetailsTO);
        
        assertThat(messageDetails).isNotNull();
        assertThat(messageDetails.getConversationId()).isEqualTo("conversationId");
        assertThat(messageDetails.getNationalMessageId()).isEqualTo("backendMessageId");
        assertThat(messageDetails.getOriginalSender()).isEqualTo("originalSender");
        assertThat(messageDetails.getFinalRecipient()).isEqualTo("finalRecipient");
        assertThat(messageDetails.getRefToMessageId()).isEqualTo("refToMessageId");
        
        assertThat(messageDetails.getAction()).isNotNull();
        assertThat(messageDetails.getService()).isNotNull();
        assertThat(messageDetails.getFromParty()).isNotNull();
        assertThat(messageDetails.getToParty()).isNotNull();
    }
    
    @Test
    @SuppressWarnings("null")
    public void testMapAction() {
        DomibusConnectorActionType actionTO = TransitionCreator.createAction();
        DomibusConnectorAction mapAction = mapService.mapAction(actionTO);

        assertThat(mapAction).isNotNull();
        assertThat(mapAction.getAction()).isEqualTo("action");
    }
    
    @Test
    @SuppressWarnings("null")
    public void testMapService() {
        DomibusConnectorServiceType serviceTO = TransitionCreator.createService();
        DomibusConnectorService mappedService = mapService.mapService(serviceTO);
        
        assertThat(mappedService).isNotNull();
        assertThat(mappedService.getService()).isEqualTo("service");
        assertThat(mappedService.getServiceType()).isEqualTo("serviceType");
    }
    
    @Test
    @SuppressWarnings("null")
    public void testMapParty() {
        DomibusConnectorPartyType partyAT_TO = TransitionCreator.createPartyAT();
        DomibusConnectorParty mapParty = mapService.mapParty(partyAT_TO);
        
        assertThat(mapParty).isNotNull();
        assertThat(mapParty.getPartyId()).isEqualTo("AT");
        assertThat(mapParty.getRole()).isEqualTo("GW");
        assertThat(mapParty.getPartyIdType()).isEqualTo("urn:oasis:names:tc:ebcore:partyid-type:iso3166-1");
    }
    
    
    @Test
    public void testMapMessageContent() throws UnsupportedEncodingException {
        DomibusConnectorMessageContentType messageContentTO = TransitionCreator.createMessageContent();
        
        MessageContent messageContent = mapService.mapMessageContent(messageContentTO);
        
        //assertThat(messageContent).isNotNull();        
        //assertThat(messageContent.getInternationalContent()).hasToString("<xmlContent></xmlContent>");
        ByteArrayStringAssert.assertThat(messageContent.getInternationalContent()).isXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlContent></xmlContent>");
        ByteArrayStringAssert.assertThat(messageContent.getPdfDocument()).isString("document");
        
        //compare detached signature
        ByteArrayStringAssert.assertThat(messageContent.getDetachedSignature()).isString("detachedSignature");
        assertThat(messageContent.getDetachedSignatureName()).isEqualTo("detachedSignatureName");
        assertThat(messageContent.getDetachedSignatureMimeType()).isEqualTo(DetachedSignatureMimeType.PKCS7);        
    }
    
    @Test
    public void testMapMessageContent_documentNull() throws UnsupportedEncodingException {
        DomibusConnectorMessageContentType messageContentTO = TransitionCreator.createMessageContent();
        messageContentTO.setDocument(null);
        
        MessageContent messageContent = mapService.mapMessageContent(messageContentTO);
        
        assertThat(messageContent).isNotNull();
        ByteArrayStringAssert.assertThat(messageContent.getInternationalContent()).isXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlContent></xmlContent>");
        assertThat((messageContent.getPdfDocument())).isNull();      
    }
    
    @Test
    public void testMapMessageContent_detachedSignatureNull() throws UnsupportedEncodingException {
        DomibusConnectorMessageContentType messageContentTO = TransitionCreator.createMessageContent();
        
        messageContentTO.getDocument().setDetachedSignature(null);
        
        MessageContent messageContent = mapService.mapMessageContent(messageContentTO);
        
        assertThat(messageContent).as("message content must not be null").isNotNull();
        ByteArrayStringAssert.assertThat(messageContent.getInternationalContent()).isXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmlContent></xmlContent>");
        ByteArrayStringAssert.assertThat(messageContent.getPdfDocument()).isString("document");
        
        assertThat(messageContent.getDetachedSignature()).isNull();
        assertThat(messageContent.getDetachedSignatureName()).isNull();
        assertThat(messageContent.getDetachedSignatureMimeType()).isNull();

    }
    

    
    private static class ByteArrayStringAssert extends AbstractByteArrayAssert<ByteArrayStringAssert> {

        public ByteArrayStringAssert(byte[] actual) {
            super(actual, ByteArrayStringAssert.class);
        }
        
        public static ByteArrayStringAssert assertThat(byte[] bytes) {
            return new ByteArrayStringAssert(bytes);
        }
        
        public ByteArrayStringAssert isXml(String string) {
            if (string == null) {
                throw new IllegalArgumentException("isString string parameter cannot be null!");
            }
            isNotNull();
            
            Source fromByteArray = Input.fromByteArray(actual).build();

            Diff d = DiffBuilder.compare(Input.fromString(string))
                    .withTest(fromByteArray)
                    .build();
            
            if (d.hasDifferences()) {
                failWithMessage("There are differences: %s", d.toString());
            }
            
            return this;
        }
        
        public ByteArrayStringAssert isString(String string) {
            if (string == null) {
                throw new IllegalArgumentException("isString string parameter cannot be null!");
            }
            try {
                isNotNull();
                
                String a = new String(actual, "UTF-8");
                if (!string.equals(a)) {
                    super.
                    failWithMessage("Expected string needs to be <%s> but was <%s>", string, a);
                }
                
                return this;
            } catch (UnsupportedEncodingException ex) {
               throw new RuntimeException(ex); //this exception should never occur!
            }
        }
        
    }


}

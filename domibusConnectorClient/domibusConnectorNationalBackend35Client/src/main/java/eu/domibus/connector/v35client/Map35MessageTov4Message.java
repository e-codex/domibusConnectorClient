
package eu.domibus.connector.v35client;

import eu.domibus.connector.common.enums.EvidenceType;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageAttachment;
import eu.domibus.connector.common.message.MessageConfirmation;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DetachedSignatureMimeType;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorBigDataReference;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.domain.model.builder.DetachedSignatureBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorActionBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageAttachmentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageContentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorPartyBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorServiceBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Maps a v4 Message to a v35 Message
 *  breaks streaming
 * 
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * 
 */
@Component
public class Map35MessageTov4Message {

    private final static Logger LOGGER = LoggerFactory.getLogger(Map35MessageTov4Message.class);
    
    
    DomibusConnectorMessage map35MessageTov4Message(Message msg) {
        if (msg.getMessageDetails() == null) {
            throw new IllegalArgumentException("MessageDetails of message must be not null!");
        }
        
        DomibusConnectorMessageBuilder messageBuilder = DomibusConnectorMessageBuilder.createBuilder();

        if (msg.getMessageContent() != null) {
            LOGGER.info("#map35MessageTov4Message: map normal message");
            DomibusConnectorMessageContent mapMessageContent = mapMessageContent(msg.getMessageContent());
            messageBuilder.setMessageContent(mapMessageContent);
        } else if (msg.getConfirmations().size() > 0) {
            LOGGER.info("#map35MessageTov4Message: message is a evidence message (contains no message content)");
        } else {            
            LOGGER.error("Throwing exception");
            throw new IllegalArgumentException("Either the message must hava a message content (normal messsage) or a message confirmation (evidence message)!");
        }
        
        DomibusConnectorMessageDetails mapMessageDetails = mapMessageDetails(msg.getMessageDetails());
        messageBuilder.setMessageDetails(mapMessageDetails);
        
        for (MessageConfirmation confirmation : msg.getConfirmations()) {
            DomibusConnectorMessageConfirmation mappedConfirmation = mapConfirmation(confirmation);
            messageBuilder.addConfirmation(mappedConfirmation);
        }

        for (MessageAttachment attachment : msg.getAttachments()) {
            DomibusConnectorMessageAttachment mappedAttachment = mapAttachment(attachment);
            messageBuilder.addAttachment(mappedAttachment);            
        }
        
        
        return messageBuilder.build();
    }
    
    DomibusConnectorEvidenceType mapEvidenceType(@Nonnull EvidenceType evidenceType) {
        return DomibusConnectorEvidenceType.valueOf(evidenceType.name());
    }
    
    DomibusConnectorMessageConfirmation mapConfirmation(MessageConfirmation oldConfirmation) {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();
        confirmation.setEvidence(oldConfirmation.getEvidence());
        
        confirmation.setEvidenceType(mapEvidenceType(oldConfirmation.getEvidenceType()));
        return confirmation;
    }
    
    DomibusConnectorMessageAttachment mapAttachment(MessageAttachment oldAttachment) {
        DomibusConnectorMessageAttachmentBuilder attachmentBuilder = DomibusConnectorMessageAttachmentBuilder.createBuilder();

        DomibusConnectorByteBasedBigDataReference dataRef = new DomibusConnectorByteBasedBigDataReference(oldAttachment.getAttachment());
        
        attachmentBuilder.setAttachment(dataRef);
        attachmentBuilder.setIdentifier(oldAttachment.getIdentifier());
        
        attachmentBuilder.withDescription(oldAttachment.getDescription());
        attachmentBuilder.withMimeType(oldAttachment.getMimeType());
        attachmentBuilder.withName(oldAttachment.getName());
                
        return attachmentBuilder.build();
    }
    

    
    
    DomibusConnectorMessageContent mapMessageContent(MessageContent oldMessageContent) {
        
        DomibusConnectorMessageContentBuilder contentBuilder = DomibusConnectorMessageContentBuilder.createBuilder();
        
        DomibusConnectorMessageDocumentBuilder documentBuilder = DomibusConnectorMessageDocumentBuilder.createBuilder();
        
        if (oldMessageContent.getDetachedSignature() != null && oldMessageContent.getDetachedSignatureName() != null) {            
            DetachedSignatureMimeType sigMimeType = mapDetachedSignatureMimeType(oldMessageContent.getDetachedSignatureMimeType());
            String signatureName = oldMessageContent.getDetachedSignatureName();
            byte[] signatureBytes = oldMessageContent.getDetachedSignature();

            DetachedSignature signature = DetachedSignatureBuilder.createBuilder()
                    .setMimeType(sigMimeType)
                    .setName(signatureName)
                    .setSignature(signatureBytes)
                    .build();
            documentBuilder.withDetachedSignature(signature);
        } else {
            LOGGER.info("mapMessageContent# v35 MessageContent contains no detachedsignature: either detachedSignature [{}] or or signature name [{}] is null", 
                    oldMessageContent.getDetachedSignature(),
                    oldMessageContent.getDetachedSignatureName());
        }
        
        String pdfDocumentName = oldMessageContent.getPdfDocumentName();
        byte[] pdfDocument = oldMessageContent.getPdfDocument();
        if (pdfDocument != null) {
            DomibusConnectorByteBasedBigDataReference pdfDocFileRef = new DomibusConnectorByteBasedBigDataReference(pdfDocument);
        
            DomibusConnectorMessageDocument document = documentBuilder
                .setName(pdfDocumentName)
                .setContent(pdfDocFileRef)                
                .build();
            contentBuilder.setDocument(document);
        } else {
            LOGGER.info("#mapMessageContent# v35 messageContent contains no pdf document!");
        }
        
        return contentBuilder
                .setXmlContent(oldMessageContent.getInternationalContent())                
                .build();   
    }
    
    DetachedSignatureMimeType mapDetachedSignatureMimeType(eu.domibus.connector.common.enums.DetachedSignatureMimeType mimeType35) {
        return DetachedSignatureMimeType.valueOf(mimeType35.name());
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
    
    /**
     * A read only implementation of DomibusConnectorBigDataReference
     * which stores the content in a byte[] (in memory, breaks streaming)
     */
    private static class DomibusConnectorByteBasedBigDataReference extends DomibusConnectorBigDataReference {

        private final byte[] bytes;
        
        public DomibusConnectorByteBasedBigDataReference(byte[] bytes) {
            this.bytes = bytes;
        }
        
        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(bytes);
        }
        
        @Override
        public boolean isReadable() { return true; }            

    }
    
}

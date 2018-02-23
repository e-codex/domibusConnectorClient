
package eu.domibus.connector.v35client;

import eu.domibus.connector.common.enums.EvidenceType;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageAttachment;
import eu.domibus.connector.common.message.MessageConfirmation;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.common.message.MessageDetails;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.stream.StreamSource;
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
    
    
    DomibusConnectorMessageType map35MessageTov4Message(Message msg) {
        LOGGER.debug("#map35MessageTov4Message: map message [{}]", msg);
        if (msg.getMessageDetails() == null) {
            throw new IllegalArgumentException("MessageDetails of message must be not null!");
        }
        
        DomibusConnectorMessageType message = new DomibusConnectorMessageType();

        if (msg.getMessageContent() != null) {
            LOGGER.info("#map35MessageTov4Message: map normal message");
            DomibusConnectorMessageContentType mapMessageContent = mapMessageContent(msg.getMessageContent());
            message.setMessageContent(mapMessageContent);
        } else if (msg.getConfirmations() != null && msg.getConfirmations().size() > 0) {
            LOGGER.info("#map35MessageTov4Message: message is a evidence message (contains no message content)");
        } else {            
            LOGGER.error("Throwing exception");
            throw new IllegalArgumentException("Either the message must hava a message content (normal messsage) or a message confirmation (evidence message)!");
        }
        
        DomibusConnectorMessageDetailsType mapMessageDetails = mapMessageDetails(msg.getMessageDetails());
        message.setMessageDetails(mapMessageDetails);
        
        if (msg.getConfirmations() != null) {
            for (MessageConfirmation confirmation : msg.getConfirmations()) {
                DomibusConnectorMessageConfirmationType mappedConfirmation = mapConfirmation(confirmation);
                message.getMessageConfirmations().add(mappedConfirmation);
            }
        }

        if (msg.getAttachments() != null) {
            for (MessageAttachment attachment : msg.getAttachments()) {
                DomibusConnectorMessageAttachmentType mappedAttachment = mapAttachment(attachment);
                message.getMessageAttachments().add(mappedAttachment);            
            }
        }
                
        return message;
    }
    
    DomibusConnectorConfirmationType mapEvidenceType(@Nonnull EvidenceType evidenceType) {
        return DomibusConnectorConfirmationType.valueOf(evidenceType.name());
    }
    
    DomibusConnectorMessageConfirmationType mapConfirmation(MessageConfirmation oldConfirmation) {
        DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
        //confirmation.setEvidence(oldConfirmation.getEvidence());
        
        StreamSource streamSource = new StreamSource(new ByteArrayInputStream(oldConfirmation.getEvidence()));
        confirmation.setConfirmation(streamSource);
        
        confirmation.setConfirmationType(mapEvidenceType(oldConfirmation.getEvidenceType()));

        return confirmation;
    }
    
    DomibusConnectorMessageAttachmentType mapAttachment(MessageAttachment oldAttachment) {
        DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType(); 

        DataHandler dataHandler = convertByteArrayToDataHandler(oldAttachment.getAttachment(), oldAttachment.getMimeType());
        attachment.setAttachment(dataHandler);
        attachment.setIdentifier(oldAttachment.getIdentifier());
        
        attachment.setDescription(oldAttachment.getDescription());
        attachment.setMimeType(oldAttachment.getMimeType());
        attachment.setName(oldAttachment.getName());
                
        return attachment;
    }
    

    
    
    DomibusConnectorMessageContentType mapMessageContent(MessageContent oldMessageContent) {
        
        DomibusConnectorMessageContentType messageContent = new DomibusConnectorMessageContentType();
        
        DomibusConnectorMessageDocumentType document = new DomibusConnectorMessageDocumentType();
        
        if (oldMessageContent.getDetachedSignature() != null && oldMessageContent.getDetachedSignatureName() != null) {            
            DomibusConnectorDetachedSignatureMimeType sigMimeType = mapDetachedSignatureMimeType(oldMessageContent.getDetachedSignatureMimeType());
            String signatureName = oldMessageContent.getDetachedSignatureName();
            byte[] signatureBytes = oldMessageContent.getDetachedSignature();

            DomibusConnectorDetachedSignatureType detachedSignature = new DomibusConnectorDetachedSignatureType();
            detachedSignature.setDetachedSignature(signatureBytes);
            detachedSignature.setDetachedSignatureName(signatureName);
            detachedSignature.setMimeType(sigMimeType);
            
            document.setDetachedSignature(detachedSignature);

        } else {
            LOGGER.info("mapMessageContent# v35 MessageContent contains no detachedsignature: either detachedSignature [{}] or or signature name [{}] is null", 
                    oldMessageContent.getDetachedSignature(),
                    oldMessageContent.getDetachedSignatureName());
        }
        
        if (oldMessageContent.getInternationalContent() != null) {
            StreamSource streamSource = new StreamSource(new ByteArrayInputStream(oldMessageContent.getInternationalContent()));
            messageContent.setXmlContent(streamSource);
        }
        
        
        String pdfDocumentName = oldMessageContent.getPdfDocumentName();
        byte[] pdfDocument = oldMessageContent.getPdfDocument();
        if (pdfDocument != null) {
            DataHandler dataHandler = convertByteArrayToDataHandler(pdfDocument, "application/pdf");
            
            document.setDocumentName(pdfDocumentName);
            document.setDocument(dataHandler);            
            messageContent.setDocument(document);
            LOGGER.info("#mapMessageContent# v35 messageContent document converted to transition document!");
        } else {
            LOGGER.info("#mapMessageContent# v35 messageContent contains no pdf document!");
        }
        
        return messageContent;
    }
    
    DomibusConnectorDetachedSignatureMimeType mapDetachedSignatureMimeType(eu.domibus.connector.common.enums.DetachedSignatureMimeType mimeType35) {
        return DomibusConnectorDetachedSignatureMimeType.fromValue(mimeType35.name());
    }
    
    
    DomibusConnectorMessageDetailsType mapMessageDetails(MessageDetails oldMessageDetails) {
        
        DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();
        
        BeanUtils.copyProperties(oldMessageDetails, messageDetails);
        
        messageDetails.setBackendMessageId(oldMessageDetails.getNationalMessageId());
        
        messageDetails.setAction(mapDomibusConnectorAction(oldMessageDetails.getAction()));        
        messageDetails.setFromParty(mapDomibusConnectorParty(oldMessageDetails.getFromParty()));
        messageDetails.setService(mapDomibusConnectorService(oldMessageDetails.getService()));
        messageDetails.setToParty(mapDomibusConnectorParty(oldMessageDetails.getToParty()));
        
        LOGGER.debug("#mapMessageDetails: mapped messageDetails from [{}] to [{}]", oldMessageDetails, messageDetails);
        return messageDetails;
    }
    
    @Nullable DomibusConnectorActionType mapDomibusConnectorAction(@Nullable eu.domibus.connector.common.db.model.DomibusConnectorAction oldAction) {
        if (oldAction == null) return null;
        DomibusConnectorActionType action = new DomibusConnectorActionType();        
        action.setAction(oldAction.getAction());
        return action;
    }
    
    @Nullable DomibusConnectorPartyType mapDomibusConnectorParty(@Nullable eu.domibus.connector.common.db.model.DomibusConnectorParty oldParty) {
        if (oldParty == null) return null;
        DomibusConnectorPartyType party = new DomibusConnectorPartyType();
        party.setPartyId(oldParty.getPartyId());
        party.setPartyIdType(oldParty.getPartyIdType());
        party.setRole(oldParty.getRole());
        return party;
        
    }
    
    @Nullable DomibusConnectorServiceType mapDomibusConnectorService(@Nullable eu.domibus.connector.common.db.model.DomibusConnectorService oldService) {
        if (oldService == null) return null;
        DomibusConnectorServiceType service = new DomibusConnectorServiceType();
        service.setService(oldService.getService());
        service.setServiceType(oldService.getServiceType());
        return service;
    }
    
    /**
     * converts a byte[] by creating a copy of the provided byte array (because
     * byte array is not immutable) and passing this byte array to DataHandler
     * constructor
     *
     *
     * @param array - the byte array
     * @param mimeType - the provided mimeType, can be null, if null
     * "application/octet-stream" mimeType will be set
     *
     * @return the DataHandler
     */
    static @Nonnull
    DataHandler convertByteArrayToDataHandler(@Nonnull byte[] array, @Nullable String mimeType) {
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }

        ByteArrayDataSource rawData = new ByteArrayDataSource(array);
        DataHandler dataHandler = new DataHandler(rawData);
        return dataHandler;
    }
    
    
//    /**
//     * A read only implementation of DomibusConnectorBigDataReference
//     * which stores the content in a byte[] (in memory, breaks streaming)
//     */
//    private static class DomibusConnectorByteBasedBigDataReference extends DomibusConnectorBigDataReference {
//
//        private final byte[] bytes;
//        
//        public DomibusConnectorByteBasedBigDataReference(byte[] bytes) {
//            this.bytes = bytes;
//        }
//        
//        @Override
//        public InputStream getInputStream() {
//            return new ByteArrayInputStream(bytes);
//        }
//        
//        @Override
//        public boolean isReadable() { return true; }            
//
//    }
    
    
    
    private static class ByteArrayDataSource implements DataSource {

        private byte[] bytes;

        public ByteArrayDataSource(byte[] bytes) {
            this.bytes = bytes;
        }
        
        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public byte[] getBytes() {
            return bytes;
        }
        private String contentType;

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(bytes);
        }

        /**
         * for completeness, here's how to implement the outputstream. this is
         * unnecessary for what you're doing, you can just throw an
         * UnsupportedOperationException.
         * @return 
         */
        @Override
        public OutputStream getOutputStream() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getName() {
            return "";
        }
    }
    
}

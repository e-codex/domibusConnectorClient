
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
import eu.domibus.connector.domain.transition.helper.TransitionModelHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Component
public class MapV4TransitionMessageTo35Message {

    private final static Logger LOGGER = LoggerFactory.getLogger(MapV4TransitionMessageTo35Message.class);
    
    public Message mapMessageTo35Message(DomibusConnectorMessageType messageTO) {
        Message message = null;
        MessageDetails mapMessageDetails = mapMessageDetails(messageTO.getMessageDetails());
        
        List<MessageConfirmation> messageConfirmations = mapMessageConfirmationList(messageTO.getMessageConfirmations());
        
        if (messageTO.getMessageContent() != null) {
            MessageContent mapMessageContent = mapMessageContent(messageTO.getMessageContent());
            message = new Message(mapMessageDetails, mapMessageContent);            
        } else if (TransitionModelHelper.isEvidenceMessage(messageTO)) {
            message = new Message(mapMessageDetails, messageConfirmations.get(0));
        } else {
            //should not end here!
            throw new RuntimeException("illegal state!");
        }
        //Message msg = new Message(null, null);
        message.setConfirmations(messageConfirmations);
        message.setAttachments(mapMessageAttachmentList(messageTO.getMessageAttachments()));
                
        return message;
    }

    @Nonnull
    List<MessageConfirmation> mapMessageConfirmationList(@Nonnull List<DomibusConnectorMessageConfirmationType> confirmationsTO) {
        List<MessageConfirmation> confirmations = confirmationsTO
                .stream()
                .map(c -> mapMessageConfirmation(c))
                .collect(Collectors.toList());
        return confirmations;
    }

    @Nonnull
    MessageConfirmation mapMessageConfirmation(@Nonnull DomibusConnectorMessageConfirmationType confirmationTO) {
        MessageConfirmation confirmation = new MessageConfirmation();        
        confirmation.setEvidence(transformXmlSourceToByteArray(confirmationTO.getConfirmation()));
        confirmation.setEvidenceType(mapEvidenceType(confirmationTO.getConfirmationType()));
        return confirmation;
    }

    @Nonnull
    List<MessageAttachment> mapMessageAttachmentList(@Nonnull List<DomibusConnectorMessageAttachmentType> messageAttachmentsTO) {
        List<MessageAttachment> attachments = messageAttachmentsTO
                .stream()
                .map(a -> mapMessageAttachment(a))
                .collect(Collectors.toList());
        return attachments;
    }

    @Nonnull MessageAttachment mapMessageAttachment(DomibusConnectorMessageAttachmentType attachmentTO) {
        String identifier = attachmentTO.getIdentifier();
        byte[] byteArray = convertDataHandlerToByteArray(attachmentTO.getAttachment());
        MessageAttachment attachment = new MessageAttachment(byteArray, identifier);
        attachment.setDescription(attachmentTO.getDescription());
        attachment.setMimeType(attachmentTO.getMimeType());
        attachment.setName(attachmentTO.getName());
        return attachment;
    }

    @Nonnull
    MessageDetails mapMessageDetails(@Nonnull DomibusConnectorMessageDetailsType messageDetailsTO) {
        MessageDetails messageDetails = new MessageDetails();
        
        messageDetails.setConversationId(messageDetailsTO.getConversationId());
        messageDetails.setNationalMessageId(messageDetailsTO.getBackendMessageId());
        messageDetails.setOriginalSender(messageDetailsTO.getOriginalSender());
        messageDetails.setFinalRecipient(messageDetailsTO.getFinalRecipient());
        messageDetails.setRefToMessageId(messageDetailsTO.getRefToMessageId());
        
        messageDetails.setAction(mapAction(messageDetailsTO.getAction()));
        messageDetails.setService(mapService(messageDetailsTO.getService()));
        messageDetails.setFromParty(mapParty(messageDetailsTO.getFromParty()));
        messageDetails.setToParty(mapParty(messageDetailsTO.getToParty()));
        
        return messageDetails;
    }
    
    @Nullable DomibusConnectorParty mapParty(@Nullable DomibusConnectorPartyType partyTO) {
        if (partyTO == null) {
            return null;
        }
        DomibusConnectorParty party = new DomibusConnectorParty();
        BeanUtils.copyProperties(partyTO, party);
        return party;
    }

    @Nullable DomibusConnectorService mapService(@Nullable DomibusConnectorServiceType serviceTO) {
        if (serviceTO == null) {
            return null;
        }
        DomibusConnectorService service = new DomibusConnectorService();
        BeanUtils.copyProperties(serviceTO, service);
        return service;
    }

    @Nullable DomibusConnectorAction mapAction(@Nullable DomibusConnectorActionType actionTO) {
        if (actionTO == null) {
            return null;
        }
        DomibusConnectorAction action = new DomibusConnectorAction();
        BeanUtils.copyProperties(actionTO, action);
        return action;
    }
    
    
    @Nonnull MessageContent mapMessageContent(@Nonnull DomibusConnectorMessageContentType messageContentTO) {
        LOGGER.debug("#mapMessageContent: map messageContent [{}]", messageContentTO);
        MessageContent messageContent = new MessageContent();

        Source xmlContent = messageContentTO.getXmlContent();
        messageContent.setInternationalContent(transformXmlSourceToByteArray(xmlContent));
        
        DomibusConnectorMessageDocumentType document = messageContentTO.getDocument();
        if (document != null) {
            messageContent.setPdfDocument(convertDataHandlerToByteArray(document.getDocument()));
            messageContent.setPdfDocumentName(document.getDocumentName());
            
            DomibusConnectorDetachedSignatureType detachedSignature = document.getDetachedSignature();
            if (detachedSignature != null) {
                messageContent.setDetachedSignature(detachedSignature.getDetachedSignature());
                messageContent.setDetachedSignatureName(detachedSignature.getDetachedSignatureName());
                messageContent.setDetachedSignatureMimeType(mapDetachedSignatureMimeType(detachedSignature.getMimeType()));                
            } else {
                LOGGER.debug("#mapMessageContent: document does not contain a detached signature");
            }
            
        } else {
            LOGGER.debug("#mapMessageContent: document does not contain a document");
        }                
        return messageContent;
    }
    
    DetachedSignatureMimeType mapDetachedSignatureMimeType(DomibusConnectorDetachedSignatureMimeType mimeType) {
        return DetachedSignatureMimeType.valueOf(mimeType.value());
    }    
    
    @Nullable byte[] convertDataHandlerToByteArray(@Nullable DataHandler dh) {
        if (dh == null) {
            return null;
        }
        try {
            InputStream inputStream = dh.getInputStream();
            return StreamUtils.copyToByteArray(inputStream);            
        } catch (IOException ex) {
            String error = String.format("#convertDataHandlerToByteArray: cannot get InputStream from DataHandler [%s]", dh);
            LOGGER.error(error, ex);
            throw new RuntimeException(error, ex);
        }        
    }
    
    
    @Nullable byte[] transformXmlSourceToByteArray(@Nullable Source xmlContent) {
        if (xmlContent == null) {
            return null;
        }
        try {
            ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult xmlOutput = new StreamResult(resultStream);
            transformer.transform(xmlContent, xmlOutput);                        
            return resultStream.toByteArray();
        } catch (TransformerException ex) {
            String error = String.format("#transformXmlSourceToByteArray: Error while transforming xml source [%s] to byte array", xmlContent);
            LOGGER.error(error, ex);
            throw new RuntimeException(error, ex);
        } 
    }

    @Nonnull EvidenceType mapEvidenceType(@Nonnull DomibusConnectorConfirmationType confirmationType) {
        return EvidenceType.valueOf(confirmationType.value());
    }
    
}

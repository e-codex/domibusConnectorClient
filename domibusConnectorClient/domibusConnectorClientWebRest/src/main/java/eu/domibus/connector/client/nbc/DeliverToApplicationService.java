package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.events.BusinessMessageReceivedEvent;
import eu.domibus.connector.client.events.ConfirmationReceivedEvent;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.storage.dao.BusinessMessageRepo;
import eu.domibus.connector.client.storage.entity.*;
import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import eu.domibus.connector.client.storage.service.MessageStorageService;
import eu.domibus.connector.domain.transition.*;
import eu.domibus.connector.domain.transition.tools.TransitionHelper;
import eu.domibus.connector.helper.DomibusConnectorHelper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import javax.xml.transform.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliverToApplicationService implements DomibusConnectorNationalBackendClientDelivery {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliverToApplicationService.class);

    @Autowired
    NationalMessageIdGeneratorImpl idGenerator;

    @Autowired
    MessageStorageService messageStorageService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private LargeFileStorageService largeFileStorageService;

    @Autowired
    private BusinessMessageRepo businessMessageRepo;

    @Override
    public DomibusConnectorMessageResponseType processMessageFromConnector(DomibusConnectorMessageType message) throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        String appId = idGenerator.generateNationalId();
        message.getMessageDetails().setBackendMessageId(appId);


        Transport transport = new Transport();
        transport.setReceived(LocalDateTime.now());
        transport.setMessageDetails(mapMessageDetails(message.getMessageDetails()));
        transport.setTransportDirection(Transport.TransportDirection.INCOMING);
        transport.setTransportId(appId);


        if (TransitionHelper.isConfirmationMessage(message)) {
            processConfirmationMessage(message, transport);
        } else {
            processBusinessMessage(message, transport);
        }
        DomibusConnectorMessageResponseType responseType = new DomibusConnectorMessageResponseType();
        responseType.setAssignedMessageId(appId);
        responseType.setResult(true);
        return  responseType;
    }

    private void processConfirmationMessage(DomibusConnectorMessageType message, Transport transport) {
        if (message.getMessageDetails() == null) {
            throw new IllegalArgumentException("MessageDetails are not allowed to be null!");
        }
        String nationalMessageId = message.getMessageDetails().getRefToMessageId();
        if (nationalMessageId == null) {
            throw new IllegalArgumentException("No refToMessageId provided cannot assign the received confirmation to a business message!");
        }

//        String appId = idGenerator.generateNationalId();
//        message.getMessageDetails().setBackendMessageId(appId);
//
//        Transport transport = new Transport();
//        transport.setMessageDetails(mapMessageDetails(message.getMessageDetails()));

        for (DomibusConnectorMessageConfirmationType confirmation : message.getMessageConfirmations()) {
            ConfirmationReceivedEvent confirmationRcvEvent = new ConfirmationReceivedEvent();
            confirmationRcvEvent.setConfirmationType(confirmation);
            confirmationRcvEvent.setNationalMessageId(nationalMessageId);
            applicationEventPublisher.publishEvent(message);
//            messageStorageService.addConfirmation(nationalMessageId, confirmation);
            messageStorageService.addConfirmation(nationalMessageId, mapConfirmation(confirmation, transport));

            applicationEventPublisher.publishEvent(confirmationRcvEvent);
        }

    }

    @Transactional
    void processBusinessMessage(DomibusConnectorMessageType message, Transport transport) {


        BusinessMessage businessMessage = mapBusinessMessage(message, transport);
        businessMessage.setApplicationMessageId(UUID.randomUUID().toString() + "@appReceived");
        businessMessageRepo.save(businessMessage);


        //inform ui!
        BusinessMessageReceivedEvent rcvEvent = new BusinessMessageReceivedEvent();
        rcvEvent.setMessage(businessMessage);
        applicationEventPublisher.publishEvent(rcvEvent);
    }

    private BusinessMessage mapBusinessMessage(DomibusConnectorMessageType message, Transport transport) {
        BusinessMessage businessMessage = new BusinessMessage();
        businessMessage.setTransport(transport);

        DomibusConnectorMessageContentType messageContent = message.getMessageContent();
        if (messageContent == null) {
            throw new IllegalArgumentException("MessageContent is not allowed to be null!");
        }
        Source xmlContent = messageContent.getXmlContent();
        if (xmlContent != null) {
            businessMessage.setBusinessXml(DomibusConnectorHelper.convertXmlSourceToString(xmlContent));
        }
        DomibusConnectorMessageDocumentType document = messageContent.getDocument();
        if (document != null) {
            Attachment businessAttachment = mapDocument(document);
            businessMessage.setBusinessAttachment(businessAttachment);
            DetachedSignature detachedSignature = mapDetachedSignatureToStorage(document.getDetachedSignature());
        }

        businessMessage.setAttachments(mapAttachments(message.getMessageAttachments()));
        businessMessage.setConfirmations(mapConfirmations(message.getMessageConfirmations(), transport));

        return businessMessage;
    }

    private DetachedSignature mapDetachedSignatureToStorage(DomibusConnectorDetachedSignatureType detachedSignature) {
        if (detachedSignature != null) {
            DetachedSignature sig = new DetachedSignature();
            sig.setSignatureName(detachedSignature.getDetachedSignatureName());
            sig.setDetachedSignature(detachedSignature.getDetachedSignature());
            sig.setSignatureMimeType(DetachedSignature.SignatureType.fromValue(detachedSignature.getMimeType().value()));
            return sig;
        }
        return null;
    }

    private List<Confirmation> mapConfirmations(List<DomibusConnectorMessageConfirmationType> messageConfirmations, Transport transport) {
        return messageConfirmations.stream().map(c -> mapConfirmation(c, transport)).collect(Collectors.toList());
    }

    private Confirmation mapConfirmation(DomibusConnectorMessageConfirmationType messageConfirmationType, Transport transport) {
        Confirmation c = new Confirmation();
        c.setConfirmationType(Confirmation.ConfirmationType.fromValue(messageConfirmationType.getConfirmationType().value()));
        c.setConfirmationXml(DomibusConnectorHelper.convertXmlSourceToString(messageConfirmationType.getConfirmation()));
        c.setTransport(transport);
        return c;
    }

    private List<Attachment> mapAttachments(List<DomibusConnectorMessageAttachmentType> messageAttachments) {
        return messageAttachments.stream().map(this::mapAttachment)
        .collect(Collectors.toList());
    }

    private Attachment mapAttachment(DomibusConnectorMessageAttachmentType domibusConnectorMessageAttachmentType) {
        Attachment attachment = new Attachment();

        attachment.setMimeType(domibusConnectorMessageAttachmentType.getMimeType());
        attachment.setDocumentName(domibusConnectorMessageAttachmentType.getName());
        attachment.setIdentifier(domibusConnectorMessageAttachmentType.getIdentifier());
        attachment.setDescription(domibusConnectorMessageAttachmentType.getDescription());

        LargeFileStorageService.LargeFileReference largeFileReference = largeFileStorageService.createLargeFileReference();
        attachment.setDataReference(largeFileReference.getStorageIdReference().getStorageIdReference());
        try (InputStream inputStream = domibusConnectorMessageAttachmentType.getAttachment().getInputStream();
             OutputStream outputStream = largeFileStorageService.getOutputStream(largeFileReference)
        ) {
            StreamUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while copying attachment [%s]", attachment));
        }

        return attachment;
    }


    private Attachment mapDocument(DomibusConnectorMessageDocumentType document) {
        Attachment businessAttachment = new Attachment();
        businessAttachment.setDocumentName(document.getDocumentName());
        businessAttachment.setMimeType(MediaType.APPLICATION_PDF_VALUE);
        LargeFileStorageService.LargeFileReference largeFileReference = largeFileStorageService.createLargeFileReference();
        businessAttachment.setDataReference(largeFileReference.getStorageIdReference().getStorageIdReference());
        try (InputStream inputStream = document.getDocument().getInputStream();
             OutputStream outputStream = largeFileStorageService.getOutputStream(largeFileReference)
        ) {
            StreamUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error while copying business document");
        }

        DomibusConnectorDetachedSignatureType detachedSignature = document.getDetachedSignature();
        if (detachedSignature != null) {
            DetachedSignature signature = new DetachedSignature();
            signature.setDetachedSignature(detachedSignature.getDetachedSignature());
            signature.setSignatureName(detachedSignature.getDetachedSignatureName());
            signature.setSignatureMimeType(DetachedSignature.SignatureType.fromValue(detachedSignature.getMimeType().value()));
            businessAttachment.setDetachedSignature(signature);
        }
        return businessAttachment;
    }

    private MessageDetails mapMessageDetails(DomibusConnectorMessageDetailsType messageDetails) {
        MessageDetails details = new MessageDetails();
        if (messageDetails.getAction() != null) {
            details.setActionName(messageDetails.getAction().getAction());
        } else {
            throw new IllegalArgumentException("Action is not allowed to be null!");
        }
        if (messageDetails.getService() != null) {
            details.setServiceName(messageDetails.getService().getService());
        } else {
            throw new IllegalArgumentException("Service is not allowed to be null!");
        }
        details.setBackendMessageId(messageDetails.getBackendMessageId());
        details.setConversationId(messageDetails.getConversationId());
        details.setEbmsMessageId(messageDetails.getEbmsMessageId());
        details.setFinalRecipient(messageDetails.getFinalRecipient());
        details.setOriginalSender(messageDetails.getOriginalSender());
        if (messageDetails.getFromParty() != null) {
            details.setFromPartyId(messageDetails.getFromParty().getPartyId());
        } else {
            throw new IllegalArgumentException("From Party is not allowed to be null!");
        }
        if (messageDetails.getToParty() != null) {
            details.setToPartyId(messageDetails.getToParty().getPartyId());
        } else {
            throw new IllegalArgumentException("To Party is not allowed to be null!");
        }
        details.setRefToMessageId(messageDetails.getRefToMessageId());

        return details;

    }



}

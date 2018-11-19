package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.events.TriggerConfirmationEvent;
import eu.domibus.connector.client.events.TriggerMessageSendEvent;
import eu.domibus.connector.client.storage.dao.BusinessMessageRepo;
import eu.domibus.connector.client.storage.dao.TransportRepo;
import eu.domibus.connector.client.storage.entity.*;
import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import eu.domibus.connector.client.storage.service.MessageStorageService;
import eu.domibus.connector.domain.transition.*;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SentToConnectorService {

    static final String RETRIEVAL_NON_RETRIEVAL_TO_RECIPIENT_ACTION = "RetrievalNonRetrievalToRecipient";
    static final String DELIVERY_NON_DELIVERY_TO_RECIPIENT_ACTION = "DeliveryNonDeliveryToRecipient";


    private static final Logger LOGGER = LogManager.getLogger(SentToConnectorService.class);

    @Autowired
    NationalMessageIdGeneratorImpl idGenerator;

    @Autowired
    MessageStorageService messageStorageService;

//    @Autowired
//    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private LargeFileStorageService largeFileStorageService;

    @Autowired
    private BusinessMessageRepo businessMessageRepo;

    @Autowired
    private DomibusConnectorBackendWebService connectorBackendWebService;

    @Autowired
    private TransportRepo transportRepo;


    @EventListener
    @Transactional
    public void handleSendMessageEvent(TriggerMessageSendEvent sendMessageEvent) {
        String nationalId = sendMessageEvent.getNationalMessageId();
        Optional<BusinessMessage> byNationalMessageId = businessMessageRepo.findByApplicationMessageId(nationalId);
        if (!byNationalMessageId.isPresent()) {
            LOGGER.error("Cannot send message with national id [{}] because it was not found in storage!", nationalId);
            return;
        }
        BusinessMessage businessMessage = byNationalMessageId.get();

        if (businessMessage.getTransport() != null) {
            //TODO: prevent duplicate transport of same message!
            //Maybe message has multiple transports?
            LOGGER.error("Message already transported!");
            return;
        }

        MessageDetails transportMessageDetails;
        Transport transport = new Transport();
        transport.setTransportId(idGenerator.generateNationalId());
        transport.setTransportDirection(Transport.TransportDirection.OUTGOING);

        transportMessageDetails = businessMessage.getMessageDetails();
        transport.setMessageDetails(transportMessageDetails);
        transport.setCreated(LocalDateTime.now());

        if (transportMessageDetails.getBackendMessageId() == null && sendMessageEvent.isIdUpdate()) {
            transportMessageDetails.setBackendMessageId(transport.getTransportId());
        }
//        if (transportMessageDetails.getConversationId() == null) {
//            transportMessageDetails.setConversationId(transport.getTransportId());
//        }

        transportRepo.save(transport);
        businessMessage.setTransport(transport);
        businessMessage.setDraft(false);
        businessMessageRepo.save(businessMessage);

        DomibusConnectorMessageType transferMessage = mapMessageToMessageType(businessMessage);

        if (transferMessage.getMessageDetails().getBackendMessageId() == null) {
            String genBackendid = idGenerator.generateNationalId();
            transportMessageDetails.setBackendMessageId(genBackendid);
            transferMessage.getMessageDetails().setBackendMessageId(genBackendid);
        }

        //TODO: split transaction into two parts...transport init part and start transport part...

        DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType = connectorBackendWebService.submitMessage(transferMessage);
        domibsConnectorAcknowledgementType.getResultMessage();
        String remoteMessageId = domibsConnectorAcknowledgementType.getMessageId();

        transport.setRemoteTransportId(remoteMessageId);
        transport.setSent(LocalDateTime.now());

        transportRepo.save(transport);

    }

    private DomibusConnectorMessageType mapMessageToMessageType(BusinessMessage businessMessage) {
        DomibusConnectorMessageType msg = new DomibusConnectorMessageType();

        MessageDetails messageDetails = businessMessage.getMessageDetails();
        msg.setMessageDetails(mapStorageMessageDetailsToTOMessageDetails(messageDetails));

        msg.setMessageContent(mapMessageToMessageContentType(businessMessage));
        msg.getMessageAttachments().addAll(businessMessage.getAttachments().stream().map(this::mapAttachmentToTransferAttachment).collect(Collectors.toList()));

        //do not append confirmations...

        return msg;
    }

    private DomibusConnectorMessageAttachmentType mapAttachmentToTransferAttachment(Attachment attachment) {
        DomibusConnectorMessageAttachmentType transferAttachment = new DomibusConnectorMessageAttachmentType();

        transferAttachment.setDescription(attachment.getDescription());
        transferAttachment.setIdentifier(attachment.getIdentifier());
        transferAttachment.setMimeType(attachment.getMimeType());
        transferAttachment.setName(attachment.getDocumentName());

        String dataReference = attachment.getDataReference();
        Optional<LargeFileStorageService.LargeFileReference> largeFileReference = largeFileStorageService.getLargeFileReference(new LargeFileStorageService.LargeFileReferenceId(dataReference));
        transferAttachment.setAttachment(convertLargeFileReferenceToDataHandler(largeFileReference.get()));

        return transferAttachment;
    }

    private DomibusConnectorMessageContentType mapMessageToMessageContentType(BusinessMessage businessMessage) {
        DomibusConnectorMessageContentType contentType = new DomibusConnectorMessageContentType();

        StringReader reader = new StringReader(businessMessage.getBusinessXml());
        contentType.setXmlContent(new StreamSource(reader));

        if (businessMessage.getBusinessAttachment() != null) {
            Attachment businessAttachment = businessMessage.getBusinessAttachment();
            DomibusConnectorMessageDocumentType doc = new DomibusConnectorMessageDocumentType();
            doc.setDocumentName(businessAttachment.getDocumentName());


            String dataReference = businessAttachment.getDataReference();
            Optional<LargeFileStorageService.LargeFileReference> largeFileReference = largeFileStorageService.getLargeFileReference(new LargeFileStorageService.LargeFileReferenceId(dataReference));
            doc.setDocument(convertLargeFileReferenceToDataHandler(largeFileReference.get()));

            if (businessAttachment.getDetachedSignature() != null) {
                DetachedSignature detachedSig = businessAttachment.getDetachedSignature();
                DomibusConnectorDetachedSignatureType transferDetachedSig = new DomibusConnectorDetachedSignatureType();
                transferDetachedSig.setDetachedSignature(detachedSig.getDetachedSignature());
                transferDetachedSig.setDetachedSignatureName(detachedSig.getSignatureName());
                transferDetachedSig.setMimeType(DomibusConnectorDetachedSignatureMimeType.fromValue(detachedSig.getSignatureMimeType().getValue()));
                doc.setDetachedSignature(transferDetachedSig);
            }
            contentType.setDocument(doc);
        }
        return contentType;
    }

    DataHandler convertLargeFileReferenceToDataHandler(LargeFileStorageService.LargeFileReference ref) {
        DataSource dataSource = new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return largeFileStorageService.getInputStream(ref);
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                return null;
            }

            @Override
            public String getContentType() {
                return ref.getContentType();
            }

            @Override
            public String getName() {
                return ref.getName();
            }
        };
        return new DataHandler(dataSource);
    }



    @Async
    @EventListener
    public void handleTriggerConfirmationEvent(TriggerConfirmationEvent triggerConfirmationEvent) {
        DomibusConnectorMessageConfirmationType confirmationType = new DomibusConnectorMessageConfirmationType();
        confirmationType.setConfirmationType(DomibusConnectorConfirmationType.fromValue(triggerConfirmationEvent.getConfirmationType().value()));

        Optional<BusinessMessage> origMessage = businessMessageRepo.findByApplicationMessageId(triggerConfirmationEvent.getNationalMessageId());
        if (!origMessage.isPresent()) {
            LOGGER.error("Cannot send confirmation for message [{}], because this national id does not exist.", triggerConfirmationEvent.getNationalMessageId());
            return;
        }
        BusinessMessage origMsg = origMessage.get();

        MessageDetails reverseMessageDetail = createReverseMessageDetail(origMsg.getTransport().getMessageDetails());

        String actionName;
        switch (triggerConfirmationEvent.getConfirmationType()) {
            case DELIVERY:
            case NON_DELIVERY:
                actionName = DELIVERY_NON_DELIVERY_TO_RECIPIENT_ACTION;
                break;
            case RETRIEVAL:
            case NON_RETRIEVAL:
                actionName = RETRIEVAL_NON_RETRIEVAL_TO_RECIPIENT_ACTION;
                break;
            default:
                throw new RuntimeException(String.format("Unsupported Confirmation Type [%s]", triggerConfirmationEvent.getConfirmationType()));
        }
        reverseMessageDetail.setActionName(actionName);

        //TODO: service name - which service name should be used, where ist the config source for this?!
        //reverseMessageDetail.setServiceName();
        DomibusConnectorMessageDetailsType msgDetails = mapStorageMessageDetailsToTOMessageDetails(reverseMessageDetail);
        msgDetails.setRefToMessageId(reverseMessageDetail.getEbmsMessageId());
        DomibusConnectorMessageType transferMessage = new DomibusConnectorMessageType();
        transferMessage.setMessageDetails(msgDetails);
        transferMessage.getMessageConfirmations().add(confirmationType);

        //TODO: store as transport
        LOGGER.info("Calling webservice to send message [{}] to connector", transferMessage);
        DomibsConnectorAcknowledgementType domibsConnectorAcknowledgementType = connectorBackendWebService.submitMessage(transferMessage);
        //TODO: update transport state...
        domibsConnectorAcknowledgementType.getResultMessage();
    }

    private DomibusConnectorMessageDetailsType mapStorageMessageDetailsToTOMessageDetails(MessageDetails reverseMessageDetail) {
        DomibusConnectorMessageDetailsType messageDetailsType = new DomibusConnectorMessageDetailsType();
        BeanUtils.copyProperties(reverseMessageDetail, messageDetailsType);

        messageDetailsType.setBackendMessageId(reverseMessageDetail.getBackendMessageId());
        messageDetailsType.setRefToMessageId(reverseMessageDetail.getRefToMessageId());
        messageDetailsType.setEbmsMessageId(reverseMessageDetail.getEbmsMessageId());

        DomibusConnectorPartyType fromParty = new DomibusConnectorPartyType();
        fromParty.setPartyId(reverseMessageDetail.getFromPartyId());
        fromParty.setRole("GW");
        messageDetailsType.setFromParty(fromParty);

        DomibusConnectorPartyType toParty = new DomibusConnectorPartyType();
        toParty.setPartyId(reverseMessageDetail.getToPartyId());
        toParty.setRole("GW");
        messageDetailsType.setToParty(toParty);

        DomibusConnectorActionType action = new DomibusConnectorActionType();
        action.setAction(reverseMessageDetail.getActionName());
        messageDetailsType.setAction(action);

        DomibusConnectorServiceType service = new DomibusConnectorServiceType();
        service.setService(reverseMessageDetail.getServiceName());
        messageDetailsType.setService(service);

        return messageDetailsType;
    }

    private MessageDetails createReverseMessageDetail(MessageDetails messageDetails) {
        MessageDetails reversed = new MessageDetails();
        BeanUtils.copyProperties(messageDetails, reversed);

        reversed.setToPartyId(messageDetails.getFromPartyId());
        reversed.setFromPartyId(messageDetails.getToPartyId());
        reversed.setFinalRecipient(messageDetails.getOriginalSender());
        reversed.setOriginalSender(messageDetails.getFinalRecipient());
        reversed.setRefToMessageId(messageDetails.getBackendMessageId());

        return reversed;
    }

}

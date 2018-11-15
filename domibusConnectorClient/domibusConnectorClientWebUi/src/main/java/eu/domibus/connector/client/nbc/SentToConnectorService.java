package eu.domibus.connector.client.nbc;

import eu.domibus.connector.client.events.TriggerConfirmationEvent;
import eu.domibus.connector.client.storage.dao.BusinessMessageRepo;
import eu.domibus.connector.client.storage.entity.BusinessMessage;
import eu.domibus.connector.client.storage.entity.MessageDetails;
import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import eu.domibus.connector.client.storage.service.MessageStorageService;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.transition.*;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import sun.plugin2.message.Message;

import java.util.Optional;

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


    @Async
    @EventListener
    public void handleTriggerConfirmationEvent(TriggerConfirmationEvent triggerConfirmationEvent) {
        DomibusConnectorMessageConfirmationType confirmationType = new DomibusConnectorMessageConfirmationType();
        confirmationType.setConfirmationType(DomibusConnectorConfirmationType.fromValue(triggerConfirmationEvent.getConfirmationType().value()));

        Optional<BusinessMessage> origMessage = businessMessageRepo.findByNationalMessageId(triggerConfirmationEvent.getNationalMessageId());
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
        reversed.setRefToMessageId(messageDetails.getNationalMessageId());

        return reversed;
    }

}

package eu.domibus.connector.client.rest.controller;

import eu.domibus.connector.client.events.TriggerConfirmationEvent;
import eu.domibus.connector.client.events.TriggerMessageSendEvent;

import eu.domibus.connector.client.rest.restobject.AttachmentRO;
import eu.domibus.connector.client.rest.restobject.BusinessMessageRO;
import eu.domibus.connector.client.rest.restobject.ConfirmationRO;
import eu.domibus.connector.client.rest.restobject.MessageDetailsRO;
import eu.domibus.connector.client.storage.entity.*;
import eu.domibus.connector.client.storage.service.MessageStorageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class BusinessMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessMessageController.class);


    @Autowired
    private MessageStorageService messageStorageService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<BusinessMessageRO> getAllMessages() {
        List<BusinessMessage> domibusConnectorMessageTypes = messageStorageService.loadMessages();
        return domibusConnectorMessageTypes.stream()
                .map(this::mapBusinessMessage)
                .collect(Collectors.toList());
    }


    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public BusinessMessageRO getMessage(@PathVariable("id") String id) {
        Optional<BusinessMessage> businessMessage = messageStorageService.loadMessage(id);
        BusinessMessage msg = businessMessage.orElseThrow(RuntimeException::new);
        BusinessMessageRO businessMessageRO = this.mapBusinessMessage(msg);
        return businessMessageRO;
    }

    @ApiOperation(value = "Create a new Business message in status draft")
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BusinessMessageRO> createMessage(@RequestBody BusinessMessageRO restBusinessMessage) {
        BusinessMessage businessMessage = mapRestBusinessMessageToBusinessMessage(restBusinessMessage);
        businessMessage.setDraft(true);
        businessMessage.setApplicationMessageId(messageStorageService.generateApplicationMessageId());
        //no confirmations on create!
        businessMessage.setConfirmations(new ArrayList<>());
        //also no transport!
        businessMessage.setTransport(null);

        messageStorageService.saveMessage(businessMessage);

        return new ResponseEntity<>(mapBusinessMessage(businessMessage), HttpStatus.OK);
    }

    @ApiOperation(value = "Copy an existing Business message")
    @PostMapping(value = "/copy/{id}")
    public ResponseEntity<String> copyMessage(
            @PathVariable("id")
            @ApiParam(value = "The application message id")
                    String msgId) {
        BusinessMessage orig = messageStorageService.copyMessage(msgId);

        return new ResponseEntity<>(orig.getApplicationMessageId(), HttpStatus.OK);
    }

    private BusinessMessage mapRestBusinessMessageToBusinessMessage(BusinessMessageRO restBusinessMessage) {
        BusinessMessage businessMessage = new BusinessMessage();

//        BeanUtils.copyProperties(restBusinessMessage, businessMessage);

        MessageDetailsRO messageDetailsDTO = restBusinessMessage.getMessageDetailsDTO();
        if (messageDetailsDTO == null) {
            throw new IllegalArgumentException("message details of rest business messag must not be null!");
        }
        MessageDetails msgDetails = mapRestMessageDetailsToMsgDetails(messageDetailsDTO);
        businessMessage.setMessageDetails(msgDetails);


        businessMessage.setBusinessAttachment(mapRestAttachmentToAttachment(restBusinessMessage.getBusinessAttachment()));
        businessMessage.setAttachments(restBusinessMessage.getAttachments().stream().map(this::mapRestAttachmentToAttachment).collect(Collectors.toList()));

        businessMessage.setConfirmations(restBusinessMessage
                .getConfirmationROS()
                .stream()
                .map(this::mapRestConfirmationToConfirmation)
                .collect(Collectors.toList()));

        businessMessage.setBusinessXml(restBusinessMessage.getBusinessXml());

        return businessMessage;
    }

    private Confirmation mapRestConfirmationToConfirmation(ConfirmationRO confirmationRO) {
        Confirmation c = new Confirmation();
        c.setConfirmationType(Confirmation.ConfirmationType.valueOf(confirmationRO.getConfirmationType().value()));
        c.setConfirmationXml(confirmationRO.getConfirmationXml());
        return c;
    }

    private Attachment mapRestAttachmentToAttachment(AttachmentRO attachmentRO) {
        if (attachmentRO == null) {
            return null;
        }
        Attachment attachment = new Attachment();
        BeanUtils.copyProperties(attachmentRO, attachment);

        attachment.setDataReference(attachmentRO.getStorageReference()); //TODO: check input values!


        if (attachmentRO.getDetachedSignatureRO() != null) {
            DetachedSignature detachedSignature = new DetachedSignature();
            BeanUtils.copyProperties(attachmentRO.getDetachedSignatureRO(), detachedSignature);
            attachment.setDetachedSignature(detachedSignature);
        }

        return attachment;
    }

    private MessageDetails mapRestMessageDetailsToMsgDetails(MessageDetailsRO messageDetailsDTO) {
        MessageDetails msgDetails = new MessageDetails();
        BeanUtils.copyProperties(messageDetailsDTO, msgDetails);
        return msgDetails;
    }

    @RequestMapping(value = "/triggersend/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity triggerSend(@PathVariable("id") String messageId) {
//        messageStorageService.loadMessage(messageId);

        TriggerMessageSendEvent triggerMessageSendEvent = new TriggerMessageSendEvent();
        triggerMessageSendEvent.setNationalMessageId(messageId);

        applicationEventPublisher.publishEvent(triggerMessageSendEvent);

        return ResponseEntity.ok("OK");
    }

    @RequestMapping(value = "/triggerconfirmation/{id}/{confirmation-type}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity triggerConfirmation(@PathVariable("confirmation-type") String confirmationType, @PathVariable("id") String messageId) {
        Confirmation.ConfirmationType type = Confirmation.ConfirmationType.fromValue(confirmationType);

        TriggerConfirmationEvent triggerConfirmationEvent = new TriggerConfirmationEvent();
        triggerConfirmationEvent.setNationalMessageId(messageId);
        triggerConfirmationEvent.setConfirmationType(type);
        applicationEventPublisher.publishEvent(triggerConfirmationEvent);

        return ResponseEntity.ok("OK");
    }


    private BusinessMessageRO mapBusinessMessage(BusinessMessage message) {
//        try {
            BusinessMessageRO businessMessageRO = new BusinessMessageRO();

            //map message details
            MessageDetails messageDetails;
            if (message.getTransport() != null) {
                messageDetails = message.getTransport().getMessageDetails();
            } else if (message.isDraft() && message.getMessageDetails() != null ) {
                messageDetails = message.getMessageDetails();
            } else {
                throw new RuntimeException("error");
            }
            businessMessageRO.setMessageDetailsDTO(mapMessageDetails(messageDetails));

            //map content
            businessMessageRO.setBusinessXml(message.getBusinessXml());

            businessMessageRO.setApplicationMessageId(message.getApplicationMessageId());
            businessMessageRO.setDraft(message.isDraft());

            businessMessageRO.setBusinessAttachment(mapAttachment(message.getBusinessAttachment()));

            //map attachments

            //map confirmations
            businessMessageRO.setConfirmationROS(
                message.getConfirmations()
                    .stream()
                    .map(this::mapConfirmation)
                    .collect(Collectors.toList()));
            return businessMessageRO;

//        } catch (MalformedURLException malformedUrl) {
//            throw new RuntimeException(malformedUrl);
//        }

    }

    private AttachmentRO mapAttachment(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        AttachmentRO dto = new AttachmentRO();
        BeanUtils.copyProperties(attachment, dto);
        dto.setUrl(LargeFileController.PUBLISH_URL + "/" + attachment.getDataReference());
        dto.setStorageReference(attachment.getDataReference());
        return dto;
    }

    private ConfirmationRO mapConfirmation(Confirmation messageConfirmationType) {
        if (messageConfirmationType == null) {
            return null;
        }
        ConfirmationRO confirmationRO = new ConfirmationRO();
        ConfirmationRO.ConfirmationType confirmationType = ConfirmationRO.ConfirmationType.fromValue(messageConfirmationType.getConfirmationType().value());
        confirmationRO.setConfirmationType(confirmationType);
        confirmationRO.setConfirmationXml(messageConfirmationType.getConfirmationXml());
        return confirmationRO;
    }



    private MessageDetailsRO mapMessageDetails(MessageDetails msgDetails) {
        if (msgDetails == null) {
            throw new IllegalArgumentException("msgDetails are not allowed to be null!");
        }
        MessageDetailsRO detailsDTO = new MessageDetailsRO();
        BeanUtils.copyProperties(msgDetails, detailsDTO);
        return detailsDTO;
    }



}

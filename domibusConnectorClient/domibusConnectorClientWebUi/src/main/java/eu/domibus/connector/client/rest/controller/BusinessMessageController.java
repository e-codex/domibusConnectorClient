package eu.domibus.connector.client.rest.controller;

import eu.domibus.connector.client.events.TriggerConfirmationEvent;
import eu.domibus.connector.client.rest.dto.AttachmentDTO;
import eu.domibus.connector.client.rest.dto.BusinessMessageDTO;
import eu.domibus.connector.client.rest.dto.ConfirmationDTO;
import eu.domibus.connector.client.rest.dto.MessageDetailsDTO;
import eu.domibus.connector.client.storage.entity.Attachment;
import eu.domibus.connector.client.storage.entity.BusinessMessage;
import eu.domibus.connector.client.storage.entity.Confirmation;
import eu.domibus.connector.client.storage.entity.MessageDetails;
import eu.domibus.connector.client.storage.service.MessageStorageService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.helper.DomibusConnectorHelper;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages/")
public class BusinessMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessMessageController.class);


    @Autowired
    private MessageStorageService messageStorageService;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @GetMapping("/")
    public List<BusinessMessageDTO> getAllMessages() {
        List<BusinessMessage> domibusConnectorMessageTypes = messageStorageService.loadMessages();
        return domibusConnectorMessageTypes.stream()
                .map(this::mapBusinessMessage)
                .collect(Collectors.toList());
    }

    @RequestMapping("/{id}")
    public BusinessMessageDTO getMessage(@PathVariable("id") String id) {
        Optional<BusinessMessage> businessMessage = messageStorageService.loadMessage(id);
        BusinessMessage msg = businessMessage.orElseThrow(RuntimeException::new);
        BusinessMessageDTO businessMessageDTO = this.mapBusinessMessage(msg);
        return businessMessageDTO;
    }

    @RequestMapping(value = "/trigger/{id}/{confirmation-type}/", method = RequestMethod.POST)
    public ResponseEntity triggerConfirmation(@PathVariable("confirmation-type") String confirmationType, @PathVariable("id") String messageId) {
        Confirmation.ConfirmationType type = Confirmation.ConfirmationType.fromValue(confirmationType);

        TriggerConfirmationEvent triggerConfirmationEvent = new TriggerConfirmationEvent();
        triggerConfirmationEvent.setNationalMessageId(messageId);
        triggerConfirmationEvent.setConfirmationType(type);
        applicationEventPublisher.publishEvent(triggerConfirmationEvent);

        return ResponseEntity.ok("OK");
    }


    private BusinessMessageDTO mapBusinessMessage(BusinessMessage message) {
//        try {
            BusinessMessageDTO businessMessageDTO = new BusinessMessageDTO();

            //map message details
            MessageDetails messageDetails = message.getTransport().getMessageDetails();
            businessMessageDTO.setMessageDetailsDTO(mapMessageDetails(messageDetails));

            //map content
            businessMessageDTO.setBusinessXml(message.getBusinessXml());

            businessMessageDTO.setBusinessAttachment(mapAttachment(message.getBusinessAttachment()));

            //map attachments

            //map confirmations
            businessMessageDTO.setConfirmationDTOS(
                message.getConfirmations()
                    .stream()
                    .map(this::mapConfirmation)
                    .collect(Collectors.toList()));
            return businessMessageDTO;

//        } catch (MalformedURLException malformedUrl) {
//            throw new RuntimeException(malformedUrl);
//        }

    }

    private AttachmentDTO mapAttachment(Attachment attachment) {
        AttachmentDTO dto = new AttachmentDTO();
        BeanUtils.copyProperties(attachment, dto);
        dto.setUrl(LargeFileController.PUBLISH_URL + "/" + attachment.getDataReference());
        return dto;
    }

    private ConfirmationDTO mapConfirmation(Confirmation messageConfirmationType) {
        ConfirmationDTO confirmationDTO = new ConfirmationDTO();
        ConfirmationDTO.ConfirmationType confirmationType = ConfirmationDTO.ConfirmationType.fromValue(messageConfirmationType.getConfirmationType().value());
        confirmationDTO.setConfirmationType(confirmationType);
        confirmationDTO.setConfirmationXml(messageConfirmationType.getConfirmationXml());
        return confirmationDTO;
    }



    private MessageDetailsDTO mapMessageDetails(MessageDetails msgDetails) {
        MessageDetailsDTO detailsDTO = new MessageDetailsDTO();
        BeanUtils.copyProperties(msgDetails, detailsDTO);
        return detailsDTO;
    }



}

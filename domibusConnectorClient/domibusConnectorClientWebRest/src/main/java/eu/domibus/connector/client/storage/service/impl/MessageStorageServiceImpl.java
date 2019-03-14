package eu.domibus.connector.client.storage.service.impl;

import eu.domibus.connector.client.storage.dao.BusinessMessageRepo;
import eu.domibus.connector.client.storage.dao.ConfirmationRepo;
import eu.domibus.connector.client.storage.entity.*;
import eu.domibus.connector.client.storage.service.MessageStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageStorageServiceImpl implements MessageStorageService {

    private static final Logger LOGGER = LogManager.getLogger(MessageStorageService.class);

    @Autowired
    BusinessMessageRepo businessMessageRepo;

    @Autowired
    ConfirmationRepo confirmationRepo;

    @Override
    public void saveMessage(BusinessMessage messageType) {
        businessMessageRepo.save(messageType);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BusinessMessage> loadMessage(String nationalMessageId) {
        return businessMessageRepo.findByApplicationMessageId(nationalMessageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessMessage> loadMessages() {
        return businessMessageRepo.findAll();
    }

    @Override
    public void addConfirmation(String nationalMessageId, Confirmation confirmation) {
        Optional<BusinessMessage> byNationalMessageId = businessMessageRepo.findByApplicationMessageId(nationalMessageId);
        if (byNationalMessageId.isPresent()) {
            confirmation.setBusinessMessage(byNationalMessageId.get());
            confirmationRepo.save(confirmation);
        }
    }

    @Override
    public BusinessMessage copyMessage(String msgId) {
        Optional<BusinessMessage> origMessage = businessMessageRepo.findByApplicationMessageId(msgId);
        if (!origMessage.isPresent()) {
            throw new EntityNotFoundException(String.format("No message found with application id [%s]", msgId));
        }
        BusinessMessage msg = origMessage.get();
        BusinessMessage newMessage = new BusinessMessage();

        BeanUtils.copyProperties(msg, newMessage);

        MessageDetails newMsgDetails = new MessageDetails();
        BeanUtils.copyProperties(msg.getMessageDetails(), newMsgDetails);
        newMsgDetails.setBackendMessageId(null);
        newMsgDetails.setId(null);
        newMsgDetails.setEbmsMessageId(null);
        newMsgDetails.setConversationId(null);
//        newMsgDetails.setRefToMessageId(null);

        newMessage.setMessageDetails(newMsgDetails);
        newMessage.setMessageId(null);
        newMessage.setTransport(null);
        newMessage.setApplicationMessageId(generateApplicationMessageId());

        newMessage.setConfirmations(new ArrayList<>());
        newMessage.setBusinessAttachment(copyAttachment(msg.getBusinessAttachment()));
        newMessage.setAttachments(msg.getAttachments().stream().map(this::copyAttachment).collect(Collectors.toList()));

        newMessage = businessMessageRepo.save(newMessage);

        return newMessage;
    }

    public String generateApplicationMessageId() {
        return UUID.randomUUID().toString() + "@appGenerated";
    }

    private Attachment copyAttachment(Attachment origAttachment) {
        Attachment newAttachment = new Attachment();
        BeanUtils.copyProperties(origAttachment, newAttachment);
        newAttachment.setId(null);

        if (origAttachment.getDetachedSignature() != null) {
            DetachedSignature newDetachedSignature = new DetachedSignature();
            DetachedSignature origDetachedSignature = origAttachment.getDetachedSignature();
            BeanUtils.copyProperties(origDetachedSignature, newDetachedSignature);
            newDetachedSignature.setId(null);
            newAttachment.setDetachedSignature(newDetachedSignature);
        }

        return newAttachment;
    }
}

package eu.domibus.connector.client.storage.service.impl;

import eu.domibus.connector.client.storage.dao.BusinessMessageRepo;
import eu.domibus.connector.client.storage.dao.ConfirmationRepo;
import eu.domibus.connector.client.storage.entity.BusinessMessage;
import eu.domibus.connector.client.storage.entity.Confirmation;
import eu.domibus.connector.client.storage.service.MessageStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        return businessMessageRepo.findByNationalMessageId(nationalMessageId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessMessage> loadMessages() {
        return businessMessageRepo.findAll();
    }

    @Override
    public void addConfirmation(String nationalMessageId, Confirmation confirmation) {
        Optional<BusinessMessage> byNationalMessageId = businessMessageRepo.findByNationalMessageId(nationalMessageId);
        if (byNationalMessageId.isPresent()) {
            confirmation.setBusinessMessage(byNationalMessageId.get());
            confirmationRepo.save(confirmation);
        }

    }
}

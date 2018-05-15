package eu.domibus.connector.client.storage.file;


import eu.domibus.connector.client.storage.MessageStorageService;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.testdata.LoadStoreTransitionMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageStorageServiceFileImpl implements MessageStorageService {

    private static final Logger LOGGER = LogManager.getLogger(MessageStorageServiceFileImpl.class);

    @Value("connector.client.file-storage.location")
    private String fileStorageLocation = null;

    private String messagePrefix = "msg_";

    private FileSystemResource fsr;


    @PostConstruct
    public void init() {
        File storageLocationDirectory = new File(fileStorageLocation);
        if (storageLocationDirectory == null) {
            throw new IllegalArgumentException("fileStoraceLocation is null! Check your configuration!");
        }

        if (!storageLocationDirectory.exists()) {
            storageLocationDirectory.mkdirs();
        } else if (!storageLocationDirectory.isDirectory()) {
            throw new IllegalArgumentException(String.format("[%s] is not a directory!", fileStorageLocation));
        }

        fsr = new FileSystemResource(storageLocationDirectory);
    }


    @Override
    public void saveMessage(DomibusConnectorMessageType messageType) {
        try {
            String backendMessageId = messageType.getMessageDetails().getBackendMessageId();
            LOGGER.debug("save message with backend id [{}]", backendMessageId);
            Resource relative = fsr.createRelative(messagePrefix + backendMessageId);
            File file = relative.getFile();
            file.mkdirs();

            LoadStoreTransitionMessage.storeMessageTo(relative, messageType, true);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    @Override
    public DomibusConnectorMessageType loadMessage(String nationalMessageId) {
        try {
            Resource relative = fsr.createRelative(messagePrefix + nationalMessageId);
            File file = relative.getFile();
            if (!file.exists()) {
                LOGGER.warn("Message does not exist, returning null!");
                return null;
            }
            DomibusConnectorMessageType message = LoadStoreTransitionMessage.loadMessageFrom(relative);
            return message;
        } catch (IOException e) {
            LOGGER.error("Exception occured", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DomibusConnectorMessageType> loadMessages() {
        File messagesDirectory = fsr.getFile();
        List<DomibusConnectorMessageType> messages =
                Arrays.asList(messagesDirectory.listFiles((dir, name) -> name.startsWith(messagePrefix))).stream()
                .map(file -> new FileSystemResource(file))
                .map(r -> LoadStoreTransitionMessage.loadMessageFrom(r))
                .collect(Collectors.toList());
        return messages;
    }
}

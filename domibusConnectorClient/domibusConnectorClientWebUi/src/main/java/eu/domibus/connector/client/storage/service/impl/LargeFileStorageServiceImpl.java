package eu.domibus.connector.client.storage.service.impl;

import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class LargeFileStorageServiceImpl implements LargeFileStorageService {

    @Autowired
    LargeFileStorageServiceProperties largeFileStorageServiceProperties;

    Path storageFolder;

    @PostConstruct
    public void init() {
        storageFolder = largeFileStorageServiceProperties.getStoragePath();
        if (storageFolder == null) {
            throw new IllegalArgumentException("storageFolder cannot be null!");
        }
        storageFolder = storageFolder.toAbsolutePath();
        if (!Files.exists(storageFolder)) {
            if (largeFileStorageServiceProperties.isCreateFolder()) {
                try {
                    Files.createDirectory(storageFolder);
                } catch (IOException e) {
                    throw new RuntimeException(String.format("Error while creating Folder [%s]!", storageFolder), e);
                }
            } else {
                throw new IllegalStateException(String.format("Storage path [%s] does not exist and create folder is [%s]", storageFolder, largeFileStorageServiceProperties.isCreateFolder()));
            }
        }
        if (!Files.isDirectory(storageFolder)) {
            throw new IllegalStateException(String.format("Storage path [%s] is not a directory!", storageFolder));
        }
    }

    @Override
    public Optional<LargeFileReference> getLargeFileReference(String key) {
        return Optional.empty();
    }

    @Override
    public LargeFileReference createLargeFileReference() {
        String ref = UUID.randomUUID().toString();

        Path newFile = storageFolder.resolve(ref);
        try {
            Files.createFile(newFile);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create new file [%s]",newFile));
        }

        LargeFileReference largeFileReference = new LargeFileReference();
        largeFileReference.setStorageIdReference(ref);

        return largeFileReference;
    }

    @Override
    public void deleteLargeFileReference(LargeFileReference reference) {
        String ref = reference.getStorageIdReference();
        Path file = storageFolder.resolve(ref);
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to delete file [%s]", e));
        }
    }

    @Override
    public OutputStream getOutputStream(LargeFileReference reference) {
        //TODO: file locking!
        String ref = reference.getStorageIdReference();
        Path file = storageFolder.resolve(ref);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file.toFile())) {
            return fileOutputStream;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File [%s] does not exist!", file));
        } catch (IOException e) {
            throw new RuntimeException(String.format("IOException while opening output stream on file [%s]", file));
        }
    }

    @Override
    public InputStream getInputStream(LargeFileReference reference) {
        //TODO: file locking!
        String ref = reference.getStorageIdReference();
        Path file = storageFolder.resolve(ref);
        try (FileInputStream inputStream = new FileInputStream(file.toFile())) {
            return inputStream;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File [%s] does not exist!", file));
        } catch (IOException e) {
            throw new RuntimeException(String.format("IOException while opening input stream on file [%s]", file));
        }
    }
}

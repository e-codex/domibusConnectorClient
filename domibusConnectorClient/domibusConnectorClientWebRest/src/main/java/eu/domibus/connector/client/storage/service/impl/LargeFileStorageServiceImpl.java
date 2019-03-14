package eu.domibus.connector.client.storage.service.impl;

import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@Service
public class LargeFileStorageServiceImpl implements LargeFileStorageService {

    private static final Logger LOGGER = LogManager.getLogger(LargeFileStorageServiceImpl.class);

    private static final String CONTENT_LENGTH_PROPERTY_NAME = "content-length";
    private static final String CONTENT_TYPE_PROPERTY_NAME = "content-type";
    private static final String LARGE_FILE_NAME = "file";
    private static final String PROPERTIES_FILE_NAME = "file.properties";

    @Autowired
    private LargeFileStorageServiceProperties largeFileStorageServiceProperties;

    private Path storageFolder;

    public void setLargeFileStorageServiceProperties(LargeFileStorageServiceProperties largeFileStorageServiceProperties) {
        this.largeFileStorageServiceProperties = largeFileStorageServiceProperties;
    }

    @PostConstruct
    public void init() {
        storageFolder = largeFileStorageServiceProperties.getStoragePath();
        LOGGER.debug("init file storage with path [{}]", storageFolder);
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
    public Optional<LargeFileReference> getLargeFileReference(LargeFileStorageService.LargeFileReferenceId key) {
        if (key == null) {
            throw new IllegalArgumentException("key is not allowed to be null!");
        }
        String ref = key.getStorageIdReference();
        if (ref == null) {
            return Optional.empty();
        }
        LOGGER.debug("Looking up large file with reference [{}] in [{}]", ref, storageFolder);
        Path folder = storageFolder.resolve(ref);

        if (!Files.exists(folder)) {
            LOGGER.info("No file found with reference [{}]", ref);
            return Optional.empty();
        }

        LargeFileReference lfr = new LargeFileReference();
        lfr.setStorageIdReference(key);
        loadProperties(folder, lfr);

        return Optional.of(lfr);
    }

    private void loadProperties(Path folder, LargeFileReference lfr) {
        Path propertiesFile = folder.resolve(PROPERTIES_FILE_NAME);
        try (InputStream is = new FileInputStream(propertiesFile.toFile())){

            Properties p = new Properties();
            p.load(is);

            lfr.setContentLength(Long.parseLong(p.getProperty(CONTENT_LENGTH_PROPERTY_NAME)));
            lfr.setContentType(p.getProperty(CONTENT_TYPE_PROPERTY_NAME));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("Failed to find file [%s]", propertiesFile), e);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to read properties from file [%s]", propertiesFile), e);
        }
    }

    public LargeFileReference createLargeFileReference(LargeFileReference largeFileReference) {
        if (largeFileReference.getStorageIdReference() != null) {
            throw new IllegalArgumentException("Storage Reference of largeFileReference must be null!");
        }

        String ref = UUID.randomUUID().toString();

        Path newFolder = storageFolder.resolve(ref);
        Path newFile = newFolder.resolve(LARGE_FILE_NAME);
        Path propertyFile = newFolder.resolve(PROPERTIES_FILE_NAME);
        try {
            Files.createDirectory(newFolder);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create new folder [%s]", newFolder), e);
        }
        try {
            Files.createFile(newFile);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create new empty data file [%s]", newFile), e);
        }
        try {
            Files.createFile(propertyFile);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to create new empty property file [%s]", propertyFile), e);
        }

        largeFileReference.setStorageIdReference(new LargeFileReferenceId(ref));

        return largeFileReference;
    }

    @Override
    public LargeFileReference createLargeFileReference() {
        return this.createLargeFileReference(new LargeFileReference());
    }

    @Override
    public void deleteLargeFileReference(LargeFileReference reference) {
        checkReference(reference);
        String ref = reference.getStorageIdReference().getStorageIdReference();
        Path file = storageFolder.resolve(ref);
        try {
            FileSystemUtils.deleteRecursively(file);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Failed to delete folder [%s]", e));
        }
    }

    private void checkReference(LargeFileReference reference) {
        if (reference.getStorageIdReference() == null || reference.getStorageIdReference().getStorageIdReference() == null) {
            throw new IllegalArgumentException("reference or reference.getStorageIdReference is not allowed to be null!");
        }
        Path fileFolder = storageFolder.resolve(reference.getStorageIdReference().getStorageIdReference());
        if (!Files.exists(fileFolder)) {
            throw new IllegalStateException(String.format("Folder [%s] is missing looks like the data repo is corrupted!", fileFolder));
        }

    }



    @Override
    public OutputStream getOutputStream(final LargeFileReference reference) {
        checkReference(reference);
        //TODO: file locking!
        String ref = reference.getStorageIdReference().getStorageIdReference();
        Path folder = storageFolder.resolve(ref);

        Path file = folder.resolve(LARGE_FILE_NAME);
        try {
            OutputStream fileOutputStream = new FileOutputStream(file.toFile());
            CountingCallbackOutputStream countingStream = new CountingCallbackOutputStream(fileOutputStream, bytesWritten -> {
                reference.setContentLength(bytesWritten);
                writeLargeFileReferenceToPropertiesFile(folder, reference);
            });
            return countingStream;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File [%s] does not exist!", file), e);
        } catch (IOException e) {
            throw new RuntimeException(String.format("IOException while opening output stream on file [%s]", file), e);
        }
    }



    private void writeLargeFileReferenceToPropertiesFile(Path folder, LargeFileReference reference) {
        Properties p = new Properties();

        p.put(CONTENT_LENGTH_PROPERTY_NAME, Long.toString(reference.getContentLength()));
        if (reference.getContentType() != null) {
            p.put(CONTENT_TYPE_PROPERTY_NAME, reference.getContentType());
        }

        Path propertiesFile = folder.resolve(PROPERTIES_FILE_NAME);
        try (OutputStream outputStream = new FileOutputStream(propertiesFile.toFile())){
            p.store(outputStream, "Properties for stored file");
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while writing propertiesFile [%s]", propertiesFile));
        }
    }

    @Override
    public InputStream getInputStream(LargeFileReference reference) {
        checkReference(reference);
        //TODO: file locking!
        String ref = reference.getStorageIdReference().getStorageIdReference();
        Path folder = storageFolder.resolve(ref);
        Path file = folder.resolve(LARGE_FILE_NAME);
        try {
            //TODO: wrap input stream! relase lock on close();
            FileInputStream inputStream = new FileInputStream(file.toFile());
            return inputStream;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File [%s] does not exist!", file));
        } catch (IOException e) {
            throw new RuntimeException(String.format("IOException while opening input stream on file [%s]", file));
        }
    }


    private interface BytesWrittenCallback {
        public void bytesWrittenOnClose(long bytesWritten);
    }


//    private interface CloseCallback {
//        void outputStreamClosed(LargeFileReference reference);
//    }
//
//    private class CloseCallbackOutputStream extends FilterOutputStream {
//        private final CloseCallback callback;
//        private LargeFileReference reference;
//
//        public CloseCallbackOutputStream(OutputStream out, LargeFileReference reference, CloseCallback callback) {
//            super(out);
//            this.reference = reference;
//            this.callback = callback;
//        }
//
//        public void close() throws IOException {
//            super.close();
//            this.callback.outputStreamClosed(this.reference);
//        }
//    }


    private class CountingCallbackOutputStream extends FilterOutputStream {
        private final BytesWrittenCallback callback;
        private long bytesWritten = 0;
        /**
         * Creates an output stream filter built on top of the specified
         * underlying output stream.
         *
         * @param out the underlying output stream to be assigned to
         *            the field <tt>this.out</tt> for later use, or
         *            <code>null</code> if this instance is to be
         *            created without an underlying stream.
         */
        public CountingCallbackOutputStream(OutputStream out, BytesWrittenCallback callback) {
            super(out);
            this.callback = callback;
        }

        @Override
        public void write(final int b) throws IOException {
            out.write(b);
            count(1);
        }
        @Override
        public void write(final byte[] b) throws IOException {
            write(b, 0, b.length);
        }
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            out.write(b, off, len);
            count(len);
        }

        protected void count(final long written) {
            if (written != -1) {
                bytesWritten += written;
            }
        }

        public long getBytesWritten() {
            return bytesWritten;
        }

        @Override
        public void close() throws IOException {
            super.flush();
            super.close();
            this.callback.bytesWrittenOnClose(this.bytesWritten);
        }

    }
}

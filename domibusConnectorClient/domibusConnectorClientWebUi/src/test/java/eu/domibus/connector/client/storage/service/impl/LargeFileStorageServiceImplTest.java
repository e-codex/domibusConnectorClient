package eu.domibus.connector.client.storage.service.impl;

import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Paths;

class LargeFileStorageServiceImplTest {

    LargeFileStorageServiceProperties largeFileStorageServiceProperties = new LargeFileStorageServiceProperties();

    LargeFileStorageServiceImpl largeFileStorageService = new LargeFileStorageServiceImpl();

    @BeforeEach
    public void setUpTest() {
        largeFileStorageServiceProperties.setCreateFolder(true);
        largeFileStorageServiceProperties.setStoragePath(Paths.get("./target/testLargeFileStorage/"));

        largeFileStorageService.setLargeFileStorageServiceProperties(largeFileStorageServiceProperties);
        largeFileStorageService.init();
    }

    @Test
    void getLargeFileReference() {


    }

    @Test
    void createLargeFileReference() {



    }

    @Test
    void deleteLargeFileReference() {
    }

    @Test
    void testGetOutputStream_shouldReturnWriteableOutputStream() throws IOException {
        LargeFileStorageService.LargeFileReference ref = largeFileStorageService.createLargeFileReference();
        OutputStream outs = largeFileStorageService.getOutputStream(ref);

        InputStream in = new ByteArrayInputStream("Hallo Welt".getBytes());

        StreamUtils.copy(in, outs);

    }

    @Test
    void getInputStream() {
    }
}
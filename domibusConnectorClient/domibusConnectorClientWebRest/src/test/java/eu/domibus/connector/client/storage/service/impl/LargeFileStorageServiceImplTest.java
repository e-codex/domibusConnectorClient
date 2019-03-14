package eu.domibus.connector.client.storage.service.impl;

import eu.domibus.connector.client.storage.service.LargeFileStorageService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LargeFileStorageServiceImplTest {

    public static final String TEST_FOLDER = "./target/testLargeFileStorage/";

    LargeFileStorageServiceProperties largeFileStorageServiceProperties = new LargeFileStorageServiceProperties();

    LargeFileStorageServiceImpl largeFileStorageService = new LargeFileStorageServiceImpl();

    @BeforeAll
    public static void beforeAll() throws IOException {
        Path p = Paths.get(TEST_FOLDER);
        FileSystemUtils.deleteRecursively(p);

        FileSystemUtils.copyRecursively(Paths.get("./target/test-classes/largefilestorage"), p);
    }

    @BeforeEach
    public void setUpTest() {
        largeFileStorageServiceProperties.setCreateFolder(true);
        largeFileStorageServiceProperties.setStoragePath(Paths.get(TEST_FOLDER));

        largeFileStorageService.setLargeFileStorageServiceProperties(largeFileStorageServiceProperties);
        largeFileStorageService.init();
    }

    @Test
    void getLargeFileReference() {
        LargeFileStorageService.LargeFileReferenceId key = new LargeFileStorageService.LargeFileReferenceId("TEST2");

        LargeFileStorageService.LargeFileReference largeFileReference = largeFileStorageService.getLargeFileReference(key).get();

        assertThat(largeFileReference).isNotNull();
        assertThat(largeFileReference.getStorageIdReference()).isNotNull();
        assertThat(largeFileReference.getStorageIdReference().getStorageIdReference()).isEqualTo("TEST2");


    }

    @Test
    void createLargeFileReference() {


    }

    @Test
    void deleteLargeFileReference() throws IOException {

        LargeFileStorageService.LargeFileReferenceId key = new LargeFileStorageService.LargeFileReferenceId("HALLO_WELT");

        LargeFileStorageService.LargeFileReference largeFileReference = largeFileStorageService.getLargeFileReference(key).get();
        assertThat(largeFileReference).isNotNull();
        largeFileStorageService.deleteLargeFileReference(largeFileReference);

        assertThat(Paths.get(TEST_FOLDER).resolve("HALLO_WELT")).doesNotExist();
    }

    @Test
    void testGetOutputStream_shouldReturnWriteableOutputStream() throws IOException {
        LargeFileStorageService.LargeFileReference ref = largeFileStorageService.createLargeFileReference();
        OutputStream outs = largeFileStorageService.getOutputStream(ref);

        InputStream in = new ByteArrayInputStream("Hallo Welt".getBytes());

        StreamUtils.copy(in, outs);
        outs.close();
    }

    @Test
    void getInputStream() {
    }
}
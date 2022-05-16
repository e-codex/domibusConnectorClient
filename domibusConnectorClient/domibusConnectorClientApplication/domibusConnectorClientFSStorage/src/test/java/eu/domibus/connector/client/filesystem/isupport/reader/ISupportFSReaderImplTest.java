package eu.domibus.connector.client.filesystem.isupport.reader;

import eu.domibus.connector.client.filesystem.DomibusConnectorClientFileSystemException;
import eu.domibus.connector.client.filesystem.configuration.DirectoryConfigurationConfigurationProperties;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSConfigurationProperties;
import eu.domibus.connector.client.filesystem.configuration.DomibusConnectorClientFSStorageConfiguration;
import eu.domibus.connector.client.filesystem.isupport.ISupportFSMessageProperties;
import eu.domibus.connector.client.filesystem.isupport.sbdh.SBDHJaxbConverter;
import eu.domibus.connector.client.filesystem.standard.DomibusConnectorClientFSMessageProperties;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;

@SpringBootTest
//@Import(eu.domibus.connector.client.filesystem.isupport.reader.ISupportFSReaderImpl.class)
//@Import({
//        ISupportFSReaderImpl.class,
//        DomibusConnectorClientFSMessageProperties.class,
//        DomibusConnectorClientFSConfigurationProperties.class
//})
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@ActiveProfiles("iSupport")
class ISupportFSReaderImplTest {

    @Autowired
    private ISupportFSReaderImpl sut;

    @SpringBootApplication(
            scanBasePackages = {"eu.domibus.connector.client.filesystem"}
    )
    public static class TestContext {}

    @Autowired
    private ResourceLoader resourceLoader;

    private File testFolder;

    @org.junit.jupiter.api.Test
    void processMessageFolderFiles() throws DomibusConnectorClientFileSystemException {
        final File testdata = new File("testdata");
        sut.processMessageFolderFiles(testFolder);
    }

    @BeforeEach
    void init() throws IOException {
        testFolder = resourceLoader.getResource("classpath:testdata").getFile();
    }
}
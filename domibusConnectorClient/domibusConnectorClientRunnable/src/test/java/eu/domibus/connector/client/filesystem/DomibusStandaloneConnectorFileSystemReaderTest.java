package eu.domibus.connector.client.filesystem;

import eu.domibus.connector.client.runnable.configuration.StandaloneClientProperties;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import org.apache.cxf.helpers.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;


public class DomibusStandaloneConnectorFileSystemReaderTest {

    StandaloneClientProperties standaloneClientProperties;

    DomibusStandaloneConnectorFileSystemReader domibusStandaloneConnectorFileSystemReader;

    String testFolder = "./target/testfolder";

    @Before
    public void setUp() throws IOException {
        standaloneClientProperties = new StandaloneClientProperties();

        domibusStandaloneConnectorFileSystemReader = new DomibusStandaloneConnectorFileSystemReader();
        domibusStandaloneConnectorFileSystemReader.setStandaloneClientProperties(standaloneClientProperties);
        domibusStandaloneConnectorFileSystemReader.init();

        //extract testdata into testFolder
        InputStream resourceAsStream = getClass().getResourceAsStream("message1.zip");
        File folder = new File(testFolder);
        folder.mkdirs();

        for (File f : folder.listFiles()) {
            System.out.println("delete file: " + f);
            FileUtils.removeDir(f);
        }

        //extract zip folder with test message
        unzip(resourceAsStream, folder);


    }



    @Test
    @Ignore("test incomplete")
    public void readUnsentMessages() throws Exception {

    }

    @Test
    public void readMessageFromFolder() throws Exception {
        DomibusConnectorMessageType domibusConnectorMessageType = domibusStandaloneConnectorFileSystemReader.readMessageFromFolder(new File(testFolder + "/message1"));

        assertThat(domibusConnectorMessageType).isNotNull();
        assertThat(domibusConnectorMessageType.getMessageAttachments()).hasSize(1);

        DomibusConnectorMessageContentType messageContent = domibusConnectorMessageType.getMessageContent();
        assertThat(messageContent).isNotNull();

        DomibusConnectorMessageDocumentType document = messageContent.getDocument();
        assertThat(document).isNotNull();
        assertThat(document.getDocumentName()).isNotBlank();
        assertThat(document.getDocument()).as("document of document should not be null!").isNotNull();

        assertThat(messageContent.getXmlContent()).isNotNull();

    }

    public static void unzip(InputStream source, File target) throws IOException {
        final ZipInputStream zipStream = new ZipInputStream(source);
        ZipEntry nextEntry;
        while ((nextEntry = zipStream.getNextEntry()) != null) {
            final String name = nextEntry.getName();
            // only extract files
            if (!name.endsWith("/")) {
                final File nextFile = new File(target, name);

                // create directories
                final File parent = nextFile.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                // write file
                try (OutputStream targetStream = new FileOutputStream(nextFile)) {
                    StreamUtils.copy(zipStream, targetStream);
                    //copy(zipStream, targetStream);
                }
            }
        }
        zipStream.close();
    }

}
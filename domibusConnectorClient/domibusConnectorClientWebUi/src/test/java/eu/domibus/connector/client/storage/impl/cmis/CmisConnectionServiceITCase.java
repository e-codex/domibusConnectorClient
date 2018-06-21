package eu.domibus.connector.client.storage.impl.cmis;


import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CmisConnectionServiceITCase {

    private CmisConnectionService cmisConnectionService;

    @Before
    public void setUp() {
        this.cmisConnectionService = new CmisConnectionService();
    }


    @Test
    public void testSessionSetUp() {

        Session session = cmisConnectionService.createSession();

        //session.createDocument()

        assertThat(session).isNotNull();

    }

    @Test
    public void createDocument() throws UnsupportedEncodingException {
        Session session = cmisConnectionService.createSession();

        Folder rootFolder = session.getRootFolder();


        String textFileName = "test.txt";

// prepare content - a simple text file
        String content = "Hello World!";

        String filename = textFileName;
        String mimetype = "text/plain; charset=UTF-8";

        byte[] contentBytes = content.getBytes("UTF-8");
        ByteArrayInputStream stream = new ByteArrayInputStream(contentBytes);

        ContentStream contentStream = session.getObjectFactory().createContentStream(filename, contentBytes.length, mimetype, stream);

// prepare properties
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, textFileName);
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

// create the document
        Document newDoc = rootFolder.createDocument(properties, contentStream, VersioningState.NONE);
    }



}
package eu.domibus.connector.helper;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class DomibusConnectorHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorHelper.class);

    public static byte[] convertXmlSourceToByteArray(Source xmlContent) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult xmlOutput = new StreamResult(new OutputStreamWriter(byteArrayOutputStream));
            transformer.transform(xmlContent, xmlOutput);
            return byteArrayOutputStream.toByteArray();
        } catch (TransformerConfigurationException e) {
            LOGGER.error("Exception occured", e);
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

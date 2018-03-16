package eu.domibus.connector.client.runnable.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.MimetypesFileTypeMap;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import eu.domibus.connector.client.runnable.exception.DomibusConnectorRunnableException;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;

//@Component
public class DomibusConnectorRunnableUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorRunnableUtil.class);

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
    

    private static final MimetypesFileTypeMap mimeMap = new MimetypesFileTypeMap();

//    @Autowired
//    StandaloneClientProperties standaloneClientProperties;
    

    public static void storeMessagePropertiesToFile(DomibusConnectorMessageProperties messageProperties,
            File messagePropertiesFile) {
        if (!messagePropertiesFile.exists()) {
            try {
                messagePropertiesFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        messageProperties.storePropertiesToFile(messagePropertiesFile);
    }

 

    public static DomibusConnectorMessageProperties loadMessageProperties(File message, String messagePropertiesFileName) {
        String pathname = message.getAbsolutePath() + File.separator + messagePropertiesFileName;
        LOGGER.debug("#loadMessageProperties: Loading message properties from file {}", pathname);
        File messagePropertiesFile = new File(pathname);
        if (!messagePropertiesFile.exists()) {
            LOGGER.error("#loadMessageProperties: Message properties file '" + messagePropertiesFile.getAbsolutePath()
                    + "' does not exist. Message cannot be processed!");
            return null;
        }
        DomibusConnectorMessageProperties details = new DomibusConnectorMessageProperties();
        details.loadPropertiesFromFile(messagePropertiesFile);

        return details;
    }

    public static String generateNationalMessageId(String postfix, Date messageReceived) {
        String natMessageId = sdf.format(messageReceived) + "_" + postfix;
        return natMessageId;
    }

    public static String getMimeTypeFromFileName(File file) {
        return mimeMap.getContentType(file.getName());
    }

}


package eu.domibus.connector.runnable.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */
public class DomibusConnectorRunnableUtilTest {

    public static String TEST_FILE_RESULTS_DIR_PROPERTY_NAME = "test.file.results";
    

    @Test
    public void testLoadMessagePropertiesFromFile() throws IOException {
        String testResultFolder = System.getenv().getOrDefault(TEST_FILE_RESULTS_DIR_PROPERTY_NAME, "./target/testfileresults/" + DomibusConnectorRunnableUtilTest.class.getName());
        File testFolder = new File(testResultFolder);
        testFolder.mkdirs();
        
        File messageProperties = new File(testFolder.getAbsolutePath() + File.separator + "message.properties");
        InputStream resourceAsStream = getClass().getResourceAsStream("message.properties");
        if (resourceAsStream == null) {
            throw new RuntimeException("InputStream is null!");
        }
        FileUtils.copyInputStreamToFile(resourceAsStream, messageProperties);
        
        
        DomibusConnectorMessageProperties msgProps = DomibusConnectorRunnableUtil.loadMessageProperties(testFolder, "message.properties");
        
        assertThat(msgProps.getFinalRecipient()).isEqualTo("ich");
        
    }

}
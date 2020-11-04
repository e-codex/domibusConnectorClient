package eu.domibus.connector.client.filesystem.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FSMessageDetails {


	private final Properties messageDetails = new Properties();

    public Properties getMessageDetails() {
		return messageDetails;
	}

	public void loadPropertiesFromFile(File messagePropertiesFile) {
        // properties = new Properties();
        
        try (FileInputStream fileInputStream = new FileInputStream(messagePropertiesFile)) {            
            messageDetails.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

    }

    public void storePropertiesToFile(File messagePropertiesFile) {
        if (!messagePropertiesFile.exists()) {
            try {
                messagePropertiesFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        try (FileOutputStream fos = new FileOutputStream(messagePropertiesFile) ) {                    
            messageDetails.store(fos, null);
        } catch (IOException e1) {
            throw new RuntimeException(e1);
        }

    }
}


package eu.domibus.connector.client.gui.main.details;

import eu.domibus.connector.client.gui.main.data.Message;
import eu.domibus.connector.client.gui.main.tab.SendNewMessageTab;
import eu.domibus.connector.client.runnable.util.DomibusConnectorMessageProperties;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableUtil;
import eu.domibus.connector.client.runnable.util.StandaloneClientProperties;
import eu.domibus.connector.gui.config.properties.ConnectorProperties;

import java.io.File;
import java.util.Date;
import javax.swing.JOptionPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Component
public class NewMessageDetailFactory {

    @Autowired
    StandaloneClientProperties standaloneClientProperties;
    
    public void showNewMessageDetail() {
        String nationalMessageId = DomibusConnectorRunnableUtil.generateNationalMessageId("test", new Date());

        String outgoingMessagesDirectory = standaloneClientProperties.getMessages().getOutgoing().getDirectory();

        File messageFolder = new File(outgoingMessagesDirectory + File.separator + nationalMessageId + DomibusConnectorRunnableConstants.MESSAGE_NEW_FOLDER_POSTFIX);
        messageFolder.mkdir();
//        if (!messageFolder.mkdir()) {
//            JOptionPane.showMessageDialog(SendNewMessageTab.this, "The Folder " + messageFolder.getAbsolutePath() + " could not be created!", "Exception", JOptionPane.ERROR_MESSAGE);
//        }

        Message newMessage = new Message();
        DomibusConnectorMessageProperties messageProperties = new DomibusConnectorMessageProperties();
        newMessage.setMessageProperties(messageProperties);
        newMessage.getMessageProperties().setNationalMessageId(nationalMessageId);
        newMessage.setMessageDir(messageFolder);
        newMessage.getMessageProperties().setFromPartyId(standaloneClientProperties.getGateway().getName());
        newMessage.getMessageProperties().setFromPartyRole(standaloneClientProperties.getGateway().getRole());
        File messagePropertiesFile = new File(messageFolder, ConnectorProperties.messagePropertiesFileName);
        DomibusConnectorRunnableUtil.storeMessagePropertiesToFile(messageProperties, messagePropertiesFile);
        
        NewMessageDetail newMessageDetail = new NewMessageDetail(newMessage, null, standaloneClientProperties);
    }
    
}

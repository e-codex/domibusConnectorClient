
package eu.domibus.connector.client.gui.main.details;

import java.io.File;
import java.util.Date;

import org.springframework.stereotype.Component;

import eu.domibus.connector.client.gui.main.data.Message;
import eu.domibus.connector.client.runnable.configuration.ConnectorClientProperties;
import eu.domibus.connector.client.runnable.util.DomibusConnectorMessageProperties;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableUtil;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Component
public class NewMessageDetailFactory {
    
    public void showNewMessageDetail() {
        String nationalMessageId = DomibusConnectorRunnableUtil.generateNationalMessageId("test", new Date());

        String outgoingMessagesDirectory = ConnectorClientProperties.outgoingMessagesDirectory;

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
        newMessage.getMessageProperties().setFromPartyId(ConnectorClientProperties.gatewayNameValue);
        newMessage.getMessageProperties().setFromPartyRole(ConnectorClientProperties.gatewayRoleValue);
        File messagePropertiesFile = new File(messageFolder, ConnectorClientProperties.messagePropertiesFileName);
        DomibusConnectorRunnableUtil.storeMessagePropertiesToFile(messageProperties, messagePropertiesFile);
        
        NewMessageDetail newMessageDetail = new NewMessageDetail(newMessage, null);
    }
    
}


package eu.domibus.connector.client.runnable.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;

import static eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants.MESSAGE_PROPERTIES_DEFAULT_FILE_NAME;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Component
@ConfigurationProperties(prefix="connector.client")
public class StandaloneClientProperties {

    MessagesSettingsProperties messages = new MessagesSettingsProperties();

    GatewayProperties gateway = new GatewayProperties();

    public MessagesSettingsProperties getMessages() {
        return messages;
    }

    public void setMessages(MessagesSettingsProperties messages) {
        this.messages = messages;
    }

    public GatewayProperties getGateway() {
        return gateway;
    }

    public void setGateway(GatewayProperties gateway) {
        this.gateway = gateway;
    }

    public static class MessagesSettingsProperties {
        MessageFolderProperties incoming = new MessageFolderProperties();
        MessageFolderProperties outgoing = new MessageFolderProperties();
        String messagePropertiesFileName = DomibusConnectorRunnableConstants.MESSAGE_PROPERTIES_DEFAULT_FILE_NAME;

        public MessageFolderProperties getIncoming() {
            return incoming;
        }

        public void setIncoming(MessageFolderProperties incoming) {
            this.incoming = incoming;
        }

        public MessageFolderProperties getOutgoing() {
            return outgoing;
        }

        public void setOutgoing(MessageFolderProperties outgoing) {
            this.outgoing = outgoing;
        }

        public String getMessagePropertiesFileName() {
            return messagePropertiesFileName;
        }

        public void setMessagePropertiesFileName(String messagePropertiesFileName) {
            this.messagePropertiesFileName = messagePropertiesFileName;
        }
    }
    
    public static class MessageFolderProperties {        
        String directory;        
        boolean createDirectory = true; 

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public boolean isCreateDirectory() {
            return createDirectory;
        }

        public void setCreateDirectory(boolean createDirectory) {
            this.createDirectory = createDirectory;
        }        
    }
    
    public static class GatewayProperties {
        private String name;
        private String role;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
        
        
    }
    
}

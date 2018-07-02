package eu.domibus.connector.client.gui.main.tab;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.gui.main.data.Message;
import eu.domibus.connector.client.gui.main.reader.MessagesReader;
import eu.domibus.connector.client.runnable.configuration.ConnectorClientProperties;
import eu.domibus.connector.client.runnable.util.DomibusConnectorRunnableConstants;

@Component
@Profile("swing-gui")
public class ReceivedMessagesTab extends MessagesTab {
    
	@Autowired
	MessagesReader messagesReader;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3111650760607967459L;


	public ReceivedMessagesTab() {
		super();
	}

	@Override
	public List<Message> loadMessages() throws Exception {
        return messagesReader.readMessagesFromDirectory(ConnectorClientProperties.incomingMessagesDirectory);
		//return MessagesReader.readMessagesFromDirectory(ConnectorProperties.incomingMessagesDirectory);
	}

	@Override
	public String getTableHeader5() {
		return "Received";
	}


	@Override
	public String getMessageStatus(Message msg) {
		return "STORED";
	}

	@Override
	public int getMessageType() {
		return DomibusConnectorRunnableConstants.MESSAGE_TYPE_INCOMING;
	}

	@Override
	public String getMessageDatetime(Message msg) {
		return msg.getMessageProperties().getMessageReceivedDatetime();
	}
}

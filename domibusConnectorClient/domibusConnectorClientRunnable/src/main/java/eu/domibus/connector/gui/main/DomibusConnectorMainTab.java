package eu.domibus.connector.gui.main;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.domibus.connector.gui.main.tab.ReceivedMessagesTab;
import eu.domibus.connector.gui.main.tab.SendNewMessageTab;
import eu.domibus.connector.gui.main.tab.SentMessagesTab;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DomibusConnectorMainTab extends JTabbedPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3277796843774847324L;

    @Autowired
    SentMessagesTab sentMessages; // = new SentMessagesTab();
    
    @Autowired
    ReceivedMessagesTab receivedMessages; // = new ReceivedMessagesTab();
    
    @Autowired
    SendNewMessageTab sendMessageTab;
    
    @PostConstruct
    public void init() {
        this.add("Received messages", receivedMessages);
        this.add("Sent messages", sentMessages);

        this.add("Send new message", sendMessageTab);
        this.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                receivedMessages.refresh();
                sentMessages.refresh();
            }
        });
    }
}

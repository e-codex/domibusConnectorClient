package eu.domibus.connector.client.gui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.annotation.PostConstruct;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import eu.domibus.connector.client.exception.DomibusConnectorClientException;
import eu.domibus.connector.client.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.client.exception.ImplementationMissingException;
import eu.domibus.connector.client.transport.TransportMessagesFromConnectorToNationalService;
import eu.domibus.connector.client.transport.TransportMessagesFromNationalToConnectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("swing-gui")
public class DomibusConnectorMainMenu extends JMenuBar implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3537576845229906480L;

	@Autowired
    TransportMessagesFromNationalToConnectorService transportMessagesFromNationalToConnectorService;

    @Autowired
    TransportMessagesFromConnectorToNationalService transportMessagesFromConnectorToNationalService;

    
    @PostConstruct
    public void init() {
        JMenu fileMenu = buildFileMenu();
		this.add(fileMenu);
		this.add(buildSendReceiveMenu());
    }
	
	private JMenu buildFileMenu() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);

		JMenuItem exitItem = new JMenuItem("Shutdown domibusConnector");
		exitItem.setMnemonic(KeyEvent.VK_E);
		exitItem.addActionListener(this);
		fileMenu.add(exitItem);
		return fileMenu;
	}

	private JMenu buildSendReceiveMenu() {
    	JMenu sendRcvMenu = new JMenu("Send Receive");

    	JMenuItem sendItem = new JMenuItem("trigger send");
    	sendItem.setMnemonic(KeyEvent.VK_S);
    	sendItem.addActionListener( e -> {
            try {
                transportMessagesFromNationalToConnectorService.submitMessageFromNationalToConnector();
            } catch (DomibusConnectorNationalBackendClientException | ImplementationMissingException e1) {
                e1.printStackTrace();
                showError(e1);
            }
        });
		sendRcvMenu.add(sendItem);

		JMenuItem receiveItem = new JMenuItem("trigger receive");
		receiveItem.setMnemonic(KeyEvent.VK_R);
		receiveItem.addActionListener( e -> {
            try {
                transportMessagesFromConnectorToNationalService.transportMessageToNational();
            } catch (DomibusConnectorClientException e1) {
                e1.printStackTrace();
                showError(e1);
            }
        });
        sendRcvMenu.add(receiveItem);

		return sendRcvMenu;
	}

    private void showError(Exception e1) {
        JOptionPane.showConfirmDialog(this, "Error occured!", "Error", JOptionPane.ERROR_MESSAGE);
    }


    @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Shutdown domibusConnector")) {
			if (JOptionPane.showConfirmDialog(this, 
					"This will also shut down the domibusConnector itself. \n Continue?", "Exit", 
	        		JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION){
	            System.exit(0);
	        }
		}
		
	}

}

package eu.domibus.connector.gui.main.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import eu.domibus.connector.gui.config.properties.ConnectorProperties;
import eu.domibus.connector.gui.config.tabs.ConfigTabHelper;
import eu.domibus.connector.gui.main.data.Message;
import eu.domibus.connector.gui.main.details.NewMessageDetail;
import eu.domibus.connector.gui.main.details.NewMessageDetailFactory;
import eu.domibus.connector.runnable.util.DomibusConnectorMessageProperties;
import eu.domibus.connector.runnable.util.DomibusConnectorRunnableConstants;
import eu.domibus.connector.runnable.util.DomibusConnectorRunnableUtil;
import eu.domibus.connector.runnable.util.StandaloneClientProperties;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendNewMessageTab extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9030411701417070566L;
	private File messageFolder;
//	private String nationalMessageId;
	
    @Autowired
    private StandaloneClientProperties standaloneClientProperties;
    
    @Autowired
    private NewMessageDetailFactory newMessageDetailFactory;
    
    
	public SendNewMessageTab(){

    }
		

    @PostConstruct
    public void init() {        
//		JPanel helpPanel = ConfigTabHelper.buildHelpPanel("Send new message Help", "DatabaseConfigurationHelp.htm");
//		BorderLayout mgr = new BorderLayout();
//		setLayout(mgr);
//		add(helpPanel, BorderLayout.EAST);
        
		JPanel disp = new JPanel();
		
		if (messageFolder==null || !messageFolder.exists() || !messageFolder.isDirectory()){
			JPanel createMessageFolderPanel = buildCreateMessageFolderPanel();
			createMessageFolderPanel.setVisible(true);
			disp.add(createMessageFolderPanel);
            disp.setVisible(true);
		}else{
			
		}
		
		
		add(disp);
        this.setVisible(true);
	}
	
	private JPanel buildCreateMessageFolderPanel(){
		JPanel textPanel = createEmptyPanel();

		JLabel importLabel = new JLabel(
				"To be able to send a new message the connector first needs to create a message folder.");

		importLabel.setVisible(true);

		textPanel.add(importLabel);
		
		JButton createMessageFolderButton = new JButton();
		createMessageFolderButton.setText("Create message folder");
		createMessageFolderButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
                newMessageDetailFactory.showNewMessageDetail();
                
			}
		});
		
		createMessageFolderButton.setVisible(true);
		
		textPanel.add(createMessageFolderButton);
		return textPanel;
	}
	
	
	
	private JPanel createEmptyPanel(){
		FlowLayout panelLayout = new FlowLayout();
		panelLayout.setAlignment(FlowLayout.LEFT);
		
		JPanel panel = new JPanel();
		
		
		panel.setLayout(panelLayout);
		return panel;
	}
}

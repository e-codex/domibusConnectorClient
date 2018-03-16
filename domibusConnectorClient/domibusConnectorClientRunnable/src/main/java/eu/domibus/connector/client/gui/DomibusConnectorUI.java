package eu.domibus.connector.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import eu.domibus.connector.client.gui.main.DomibusConnectorMainMenu;
import eu.domibus.connector.client.gui.main.DomibusConnectorMainTab;

@Component
@Profile("swing-gui")
public class DomibusConnectorUI extends JFrame {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6655274520853778448L;

    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorUI.class);
    
//    @Autowired
    DomibusConnectorMainTab mainTab;
//    
//    @Autowired
    DomibusConnectorMainMenu mainMenu;
    
    @Autowired
    public DomibusConnectorUI(DomibusConnectorMainTab mainTab, DomibusConnectorMainMenu mainMenu) {       
        this.mainTab = mainTab;
        this.mainMenu = mainMenu;
    }
    
    @PostConstruct
	public void init(){
        LOGGER.debug("#init");
//		if(ConnectorProperties.CONNECTOR_PROPERTIES_FILE.exists()){
//			try {
//				ConnectorProperties.loadConnectorProperties();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        if (JOptionPane.showConfirmDialog(DomibusConnectorUI.this, 
		        		"This will also shut down the domibusConnector itself. \n Continue?", "Exit", 
		        		JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.OK_OPTION){
		            System.exit(0);
		        }
		    }
		});
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(new Dimension(800, 650));
        setTitle("DomibusConnector");

        setState(Frame.NORMAL);
        getContentPane().add(mainMenu, BorderLayout.NORTH);
        getContentPane().add(mainTab, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        repaint();
        setVisible(true);
	}
}

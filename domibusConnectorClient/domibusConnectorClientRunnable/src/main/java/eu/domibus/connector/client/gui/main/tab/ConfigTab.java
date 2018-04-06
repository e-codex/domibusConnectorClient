package eu.domibus.connector.client.gui.main.tab;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.stereotype.Component;

import eu.domibus.connector.client.gui.layout.SpringUtilities;
import eu.domibus.connector.client.gui.utils.ConfigButtonBar;
import eu.domibus.connector.client.gui.utils.ConfigTabHelper;
import eu.domibus.connector.client.runnable.configuration.ConnectorClientProperties;

@Component
public class ConfigTab extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8898156391956995907L;

	@PostConstruct
    public void init() {  
		JPanel helpPanel = ConfigTabHelper.buildHelpPanel("Other Configuration Help", "OtherConfigurationHelp.htm");
		BorderLayout mgr = new BorderLayout();
		setLayout(mgr);
		add(helpPanel, BorderLayout.EAST);
		
		JPanel disp = new JPanel(new SpringLayout());
		
		JPanel connectorPanel = buildConnectorConfigurationPanel();
		
		disp.add(connectorPanel);
		
		JPanel standalonePanel = buildStandalonePanel();
		
		disp.add(standalonePanel);

		JPanel gwConfigPanel = buildGwConfigPanel();
		
		disp.add(gwConfigPanel);
		
		JPanel keystorePanel = buildKeystorePanel();
		
		disp.add(keystorePanel);
		
		JPanel proxyConfigPanel = buildProxyConfigPanel();
		
		disp.add(proxyConfigPanel);
		
		JPanel timerConfigPanel = buildTimerConfigurationPanel();
		
		disp.add(timerConfigPanel);
		
		JPanel configButtonBar = new ConfigButtonBar();
		
		disp.add(configButtonBar);
		
		SpringUtilities.makeCompactGrid(disp,
                7, 1, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
		disp.setOpaque(true);
		
		add(disp);
	}
	
	private JPanel buildConnectorConfigurationPanel() {
		JPanel springPanel = new JPanel(new SpringLayout());
		
		JLabel header = new JLabel("Connector configuration: ");
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		springPanel.add(header);
		springPanel.add(new JLabel(""));
		
//		final JFormattedTextField clientNameValue = ConfigTabHelper.addTextFieldRow(null, springPanel, ConnectorClientProperties.CONNECTOR_CLIENT_NAME_LABEL, ConnectorClientProperties.connectorClientNameValue, 40);
//		clientNameValue.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			public void propertyChange(PropertyChangeEvent evt) {
//				ConnectorClientProperties.connectorClientNameValue = clientNameValue.getText();
//			}
//		});
		
		final JFormattedTextField connectorBackendUrlValue = ConfigTabHelper.addTextFieldRow(null, springPanel, ConnectorClientProperties.CONNECTOR_BACKEND_SERVCE_ADDRESS_LABEL, ConnectorClientProperties.connectorBackendServiceAddressValue, 40);
		connectorBackendUrlValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.connectorBackendServiceAddressValue = connectorBackendUrlValue.getText();
			}
		});
		
	
		
        SpringUtilities.makeCompactGrid(springPanel,
                2, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        springPanel.setOpaque(true);
		return springPanel;
	}
	
	private JPanel buildTimerConfigurationPanel() {
		JPanel springPanel = new JPanel(new SpringLayout());
		
		JLabel header = new JLabel("Timer configuration: ");
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		springPanel.add(header);
		springPanel.add(new JLabel(""));
		
		final JFormattedTextField checkOutgoingPeriodValue = ConfigTabHelper.addTextFieldRow(null, springPanel, ConnectorClientProperties.CHECK_OUTGOING_MESSAGES_PERIOD_LABEL, Long.toString(ConnectorClientProperties.checkOutgiongPeriodValue), 10);
		checkOutgoingPeriodValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.checkOutgiongPeriodValue = Long.parseLong(checkOutgoingPeriodValue.getText());
			}
		});
		
		final JFormattedTextField checkIncomingPeriodValue = ConfigTabHelper.addTextFieldRow(null, springPanel, ConnectorClientProperties.CHECK_INCOMING_MESSAGES_PERIOD_LABEL, Long.toString(ConnectorClientProperties.checkIncomingPeriodValue), 10);
		checkIncomingPeriodValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.checkIncomingPeriodValue = Long.parseLong(checkIncomingPeriodValue.getText());
			}
		});
		
	
		
        SpringUtilities.makeCompactGrid(springPanel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        springPanel.setOpaque(true);
		return springPanel;
	}
	
	private JPanel buildProxyConfigPanel() {
		JPanel proxyConfigPanel = new JPanel(new SpringLayout());
		
		JLabel header = new JLabel("Proxy configuration: ");
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		proxyConfigPanel.add(header);
		proxyConfigPanel.add(new JLabel(""));
		
		final JCheckBox proxyEnabled = new JCheckBox(ConnectorClientProperties.PROXY_ACTIVE_LABEL);
		proxyEnabled.setSelected(ConnectorClientProperties.proxyEnabled);
		
		proxyConfigPanel.add(new JLabel(""));
		proxyConfigPanel.add(proxyEnabled);
		
		final JFormattedTextField proxyHost = ConfigTabHelper.addTextFieldRow(null, proxyConfigPanel, ConnectorClientProperties.PROXY_HOST_LABEL, ConnectorClientProperties.proxyHost, 50);
		proxyHost.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.proxyHost = proxyHost.getText();
			}
		});
		proxyHost.setEditable(ConnectorClientProperties.proxyEnabled);
		
		final JFormattedTextField proxyPort = ConfigTabHelper.addTextFieldRow(null, proxyConfigPanel, ConnectorClientProperties.PROXY_PORT_LABEL, ConnectorClientProperties.proxyPort, 10);
		proxyPort.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.proxyPort = proxyPort.getText();
			}
		});
		proxyPort.setEditable(ConnectorClientProperties.proxyEnabled);
		
		final JFormattedTextField proxyUsername = ConfigTabHelper.addTextFieldRow(null, proxyConfigPanel, ConnectorClientProperties.PROXY_USERNAME_LABEL, ConnectorClientProperties.proxyUser, 10);
		proxyUsername.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.proxyUser = proxyUsername.getText();
			}
		});
		proxyUsername.setEditable(ConnectorClientProperties.proxyEnabled);
		
		final JFormattedTextField proxyPassword = ConfigTabHelper.addTextFieldRow(null, proxyConfigPanel, ConnectorClientProperties.PROXY_PASSWORD_LABEL, ConnectorClientProperties.proxyPassword, 10);
		proxyPassword.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.proxyPassword = proxyPassword.getText();
			}
		});
		proxyPassword.setEditable(ConnectorClientProperties.proxyEnabled);
		
		proxyEnabled.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				ConnectorClientProperties.proxyEnabled = proxyEnabled.isSelected();
				proxyHost.setEditable(proxyEnabled.isSelected());
				proxyPort.setEditable(proxyEnabled.isSelected());
				proxyUsername.setEditable(proxyEnabled.isSelected());
				proxyPassword.setEditable(proxyEnabled.isSelected());
			}
		});
		
        SpringUtilities.makeCompactGrid(proxyConfigPanel,
                6, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        proxyConfigPanel.setOpaque(true);
		return proxyConfigPanel;
	}
	
	private JPanel buildGwConfigPanel() {
		JPanel gwConfigPanel = new JPanel(new SpringLayout());
		
		JLabel header = new JLabel("Gateway configuration: ");
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		gwConfigPanel.add(header);
		gwConfigPanel.add(new JLabel(""));
		
		final JFormattedTextField gatewayName = ConfigTabHelper.addTextFieldRow(null, gwConfigPanel, ConnectorClientProperties.GATEWAY_NAME_LABEL, ConnectorClientProperties.gatewayNameValue, 10);
		gatewayName.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.gatewayNameValue = gatewayName.getText();
			}
		});
		
		final JFormattedTextField gatewayRole = ConfigTabHelper.addTextFieldRow(null, gwConfigPanel, ConnectorClientProperties.GATEWAY_ROLE_LABEL, ConnectorClientProperties.gatewayRoleValue, 10);
		gatewayRole.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.gatewayRoleValue = gatewayRole.getText();
			}
		});
		
		
        SpringUtilities.makeCompactGrid(gwConfigPanel,
                3, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        gwConfigPanel.setOpaque(true);
		return gwConfigPanel;
	}
	
	private JPanel buildKeystorePanel() {
		JPanel keystorePanel = new JPanel(new SpringLayout());
		
		JLabel header = new JLabel("Keystore configuration: ");
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		keystorePanel.add(header);
		keystorePanel.add(new JLabel(""));
		keystorePanel.add(new JLabel(""));

		final JFormattedTextField keystoreTypeValue = ConfigTabHelper.addTextFieldRow(null, keystorePanel, ConnectorClientProperties.KEYSTORE_TYPE_LABEL, ConnectorClientProperties.keystoreTypeValue, 40);
		keystoreTypeValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.keystoreTypeValue = keystoreTypeValue.getText();
			}
		});
		keystorePanel.add(new JLabel(""));

		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		final JTextField log = new JTextField(40);
		final JButton keystorePath = ConfigTabHelper.addFileChooserRow(keystorePanel, ConnectorClientProperties.KEYSTORE_PATH_LABEL, ConnectorClientProperties.keystoreFileValue, log);
		keystorePath.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				    int returnVal = fc.showOpenDialog(ConfigTab.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            	String value = "file:"+fc.getSelectedFile().getAbsolutePath().replace("\\", "//");
		            	ConnectorClientProperties.keystoreFileValue = value;
		            	log.setText(value);
		            }
		   
			}
		});
		
		final JFormattedTextField keystorePasswordValue = ConfigTabHelper.addTextFieldRow(null, keystorePanel, ConnectorClientProperties.KEYSTORE_PW_LABEL, ConnectorClientProperties.keystorePasswordValue, 40);
		keystorePasswordValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.keystorePasswordValue = keystorePasswordValue.getText();
			}
		});
		keystorePanel.add(new JLabel(""));
		
		final JFormattedTextField keyAliasValue = ConfigTabHelper.addTextFieldRow(null, keystorePanel, ConnectorClientProperties.KEY_ALIAS_LABEL, ConnectorClientProperties.keyAlias, 40);
		keyAliasValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.keyAlias = keyAliasValue.getText();
			}
		});
		keystorePanel.add(new JLabel(""));
		
		final JFormattedTextField keyPasswordValue = ConfigTabHelper.addTextFieldRow(null, keystorePanel, ConnectorClientProperties.KEY_PW_LABEL, ConnectorClientProperties.keyPassword, 40);
		keyPasswordValue.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.keyPassword = keyPasswordValue.getText();
			}
		});
		keystorePanel.add(new JLabel(""));
		
		
        SpringUtilities.makeCompactGrid(keystorePanel,
                6, 3, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
        keystorePanel.setOpaque(true);
		return keystorePanel;
	}

	private JPanel buildStandalonePanel() {
		JPanel standalonePanel = new JPanel(new SpringLayout());
		
		JLabel header = new JLabel("Standalone connector settings:");
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		standalonePanel.add(header);
		standalonePanel.add(new JLabel(""));
		standalonePanel.add(new JLabel(""));
		
		final JFileChooser incomingMsgDirFc = new JFileChooser();
		incomingMsgDirFc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		incomingMsgDirFc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		final JTextField incomingMsgDir = new JTextField(35);
		final JButton incomingMsgDirPath = ConfigTabHelper.addFileChooserRow(standalonePanel, ConnectorClientProperties.INCOMING_MSG_DIR_LABEL, 
				ConnectorClientProperties.incomingMessagesDirectory, incomingMsgDir);
		incomingMsgDirPath.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				    int returnVal = incomingMsgDirFc.showOpenDialog(ConfigTab.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            	ConnectorClientProperties.incomingMessagesDirectory = incomingMsgDirFc.getSelectedFile().getAbsolutePath().replace("\\", "//");
		            	incomingMsgDir.setText(incomingMsgDirFc.getSelectedFile().getAbsolutePath().replace("\\", "//"));
		            }
		   
			}
		});
		incomingMsgDir.setEditable(false);
		incomingMsgDirFc.setEnabled(false);
		
		final JCheckBox createDirectoryIncomingEnabled = new JCheckBox(ConnectorClientProperties.CREATE_INCOMING_MSG_DIR_LABEL);
		createDirectoryIncomingEnabled.setSelected(ConnectorClientProperties.createIncomingMessagesDirectory);

		standalonePanel.add(new JLabel(""));
		standalonePanel.add(createDirectoryIncomingEnabled);
		standalonePanel.add(new JLabel(""));

		final JFileChooser outgoingMsgDirFc = new JFileChooser();
		outgoingMsgDirFc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		outgoingMsgDirFc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		final JTextField outgoingMsgDir = new JTextField(35);
		final JButton outgoingMsgDirPath = ConfigTabHelper.addFileChooserRow(standalonePanel, ConnectorClientProperties.OUTGOING_MSG_DIR_LABEL, 
				ConnectorClientProperties.outgoingMessagesDirectory, outgoingMsgDir);
		outgoingMsgDirPath.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				    int returnVal = outgoingMsgDirFc.showOpenDialog(ConfigTab.this);

		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            	ConnectorClientProperties.outgoingMessagesDirectory = outgoingMsgDirFc.getSelectedFile().getAbsolutePath().replace("\\", "//");
		            	outgoingMsgDir.setText(outgoingMsgDirFc.getSelectedFile().getAbsolutePath().replace("\\", "//"));
		            }
		   
			}
		});
		outgoingMsgDir.setEditable(false);
		outgoingMsgDirFc.setEnabled(false);
		
		final JCheckBox createDirectoryOutgoingEnabled = new JCheckBox(ConnectorClientProperties.CREATE_OUTGOING_MSG_DIR_LABEL);
		createDirectoryOutgoingEnabled.setSelected(ConnectorClientProperties.createOutgoingMessagesDirectory);

		standalonePanel.add(new JLabel(""));
		standalonePanel.add(createDirectoryOutgoingEnabled);
		standalonePanel.add(new JLabel(""));
		
		final JFormattedTextField msgPropertiesFileName = ConfigTabHelper.addTextFieldRow(null, standalonePanel, 
				ConnectorClientProperties.MSG_PROPERTY_FILE_NAME_LABEL, ConnectorClientProperties.messagePropertiesFileName,10);
		msgPropertiesFileName.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.messagePropertiesFileName = msgPropertiesFileName.getText();
			}
		});
		standalonePanel.add(new JLabel(""));
		
		SpringUtilities.makeCompactGrid(standalonePanel,
                6, 3, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
		standalonePanel.setOpaque(true);
		
		return standalonePanel;
	}

	private JPanel buildFrameworkPanel() {
		JPanel frameworkPanel = new JPanel(new SpringLayout());
		
		JLabel header = new JLabel("Connector as Framework settings:");
		header.setFont(header.getFont().deriveFont(Font.BOLD));
		frameworkPanel.add(header);
		frameworkPanel.add(new JLabel(""));

		
		final JCheckBox contentMapperEnabled = new JCheckBox(ConnectorClientProperties.CONTENT_MAPPER_ACTIVE_LABEL);
		contentMapperEnabled.setSelected(ConnectorClientProperties.useContentMapper);

		frameworkPanel.add(new JLabel(""));
		frameworkPanel.add(contentMapperEnabled);

		final JFormattedTextField contentMapperImplClassName = ConfigTabHelper.addTextFieldRow(null, frameworkPanel, 
				ConnectorClientProperties.CONTENT_MAPPER_IMPL_CLASSNAME_LABEL, ConnectorClientProperties.contentMapperImplementaitonClassName, 35);
		contentMapperImplClassName.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ConnectorClientProperties.contentMapperImplementaitonClassName = contentMapperImplClassName.getText();
			}
		});
		contentMapperImplClassName.setEditable(ConnectorClientProperties.useContentMapper);
		
		contentMapperEnabled.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				ConnectorClientProperties.useContentMapper = contentMapperEnabled.isSelected();
				contentMapperImplClassName.setEditable(contentMapperEnabled.isSelected());
			}
		});
		
		
		
		
		
		SpringUtilities.makeCompactGrid(frameworkPanel,
                6, 2, //rows, cols
                6, 6,        //initX, initY
                6, 6);       //xPad, yPad
        
		frameworkPanel.setOpaque(true);
        
		return frameworkPanel;
	}
}

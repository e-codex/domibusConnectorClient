package eu.domibus.connector.client.ui.view.sendmessage;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.form.DynamicMessageForm;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@UIScope
public class SendMessage  extends VerticalLayout {

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendMessage.class);

	private VaadingConnectorClientUIServiceClient messageService;
	private DynamicMessageForm messageForm = new DynamicMessageForm();
	private VerticalLayout messageFilesArea = new VerticalLayout();
	private Map<String, DomibusConnectorClientMessageFileType> filesAtStorage = null;
	
	public SendMessage(@Autowired VaadingConnectorClientUIServiceClient messageService) {
		
		this.messageService = messageService;
		
		VerticalLayout messageDetailsArea = new VerticalLayout(); 
		messageForm.getStyle().set("margin-top","25px");

		messageDetailsArea.add(messageForm);
		//setAlignItems(Alignment.START);
		messageForm.setEnabled(true);
//		validateMessageForm();
//		messageDetailsArea.setHeight("100vh");
		messageDetailsArea.setWidth("500px");
		add(messageDetailsArea);
		
		Button saveBtn = new Button(new Icon(VaadinIcon.EDIT));
		saveBtn.setText("Save new Message");
		saveBtn.addClickListener(e -> {
			BinderValidationStatus<DomibusConnectorClientMessage> validationStatus = messageForm.getBinder().validate();
			if(validationStatus.isOk()) {
				
			}
//			if(!messageForm.getBinder().isValid()) messageForm.getAction().;
		});
		HorizontalLayout buttons = new HorizontalLayout(
				saveBtn
			    );
		buttons.setWidth("100vw");
		add(buttons);
		
		add(messageFilesArea);
		
		setSizeFull();
		
	}
	
	public void loadPreparedMessage(Long connectorMessageId) {
		DomibusConnectorClientMessage messageByConnectorId = messageService.getMessageById(connectorMessageId);
		messageForm.setConnectorClientMessage(messageByConnectorId);

		buildMessageFilesArea(messageByConnectorId);


	}
	
	private void buildMessageFilesArea(DomibusConnectorClientMessage messageByConnectorId) {
		
		messageFilesArea.removeAll();
		
		Div files = new Div();
		files.setWidth("100vw");
		LumoLabel filesLabel = new LumoLabel();
		filesLabel.setText("Files:");
		filesLabel.getStyle().set("font-size", "20px");
		files.add(filesLabel);
		
		messageFilesArea.add(files);
		
		Div details = new Div();
		details.setWidth("100vw");
		
		VerticalLayout messageFilesLayout = new VerticalLayout();
		
		boolean filesEnabled = messageForm.getConnectorClientMessage().getId()!=null;
		
		if(filesEnabled) {
			filesAtStorage = messageService.listContentAtStorage(messageByConnectorId.getStorageInfo());
		
			filesAtStorage.keySet().forEach(fileName -> {
				final StreamResource resource = new StreamResource(fileName,
						() -> new ByteArrayInputStream(messageService.loadContentFromStorageLocation(messageByConnectorId.getStorageInfo(), fileName)));
			Anchor downloadAnchor = createDownloadButton(fileName, filesEnabled, resource);
			messageFilesLayout.add(downloadAnchor);
			
			
		});
		}
		details.add(messageFilesLayout);
		messageFilesArea.add(details);
		
		messageFilesArea.setWidth("100vw");
//		add(messageEvidencesArea);
		messageFilesArea.setVisible(filesEnabled);
	}
	
	private Anchor createDownloadButton(String buttonTitle, boolean enabled, StreamResource resource) {
		Button button = new Button(buttonTitle);
		button.setEnabled(enabled);
		Anchor downloadAnchor = new Anchor();
		if (enabled) {
			downloadAnchor.setHref(resource);
		}else {
			downloadAnchor.removeHref();
		}
		downloadAnchor.getElement().setAttribute("download", true);
		downloadAnchor.setTarget("_blank");
		downloadAnchor.setTitle(buttonTitle);
		downloadAnchor.add(button);
		
		return downloadAnchor;
	}

}

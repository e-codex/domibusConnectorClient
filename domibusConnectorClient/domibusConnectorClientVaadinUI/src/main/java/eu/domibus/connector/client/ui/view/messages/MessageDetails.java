package eu.domibus.connector.client.ui.view.messages;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.form.DomibusConnectorClientMessageForm;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;
import okio.BufferedSink;

@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@UIScope
public class MessageDetails extends VerticalLayout {

	private VaadingConnectorClientUIServiceClient messageService;
	private DomibusConnectorClientMessageForm messageForm = new DomibusConnectorClientMessageForm();
	private VerticalLayout messageEvidencesArea = new VerticalLayout(); 
	private VerticalLayout messageFilesArea = new VerticalLayout();

	public MessageDetails(@Autowired VaadingConnectorClientUIServiceClient messageService) {

		this.messageService = messageService;
		
		Button refreshBtn = new Button(new Icon(VaadinIcon.REFRESH));
		refreshBtn.setText("Refresh");
		refreshBtn.addClickListener(e -> {
			if(!StringUtils.isEmpty(messageForm.getConnectorClientMessage().getId()))loadMessageDetails(messageForm.getConnectorClientMessage().getId());
			});
		
		HorizontalLayout buttons = new HorizontalLayout(
				refreshBtn
			    );
		buttons.setWidth("100vw");
		add(buttons);

		VerticalLayout messageDetailsArea = new VerticalLayout(); 
		messageForm.getStyle().set("margin-top","25px");

		messageDetailsArea.add(messageForm);
		//setAlignItems(Alignment.START);
		messageForm.setEnabled(true);
//		messageDetailsArea.setHeight("100vh");
		messageDetailsArea.setWidth("500px");
		add(messageDetailsArea);
		
		add(messageFilesArea);
		
		add(messageEvidencesArea);

		setSizeFull();
//		setHeight("100vh");
	}


	public void loadMessageDetails(Long connectorMessageId) {
		DomibusConnectorClientMessage messageByConnectorId = messageService.getMessageById(connectorMessageId);
		messageForm.setConnectorClientMessage(messageByConnectorId);
		
		buildMessageFilesArea(messageByConnectorId);
		
		buildMessageEvidencesArea(messageByConnectorId);

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
		
		boolean filesEnabled = messageByConnectorId.getStorageInfo()!=null && !messageByConnectorId.getStorageInfo().isEmpty() && messageByConnectorId.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED.name());
		
		List<String> filesAtStorage = messageService.listContentAtStorage(messageByConnectorId.getStorageInfo());
		
		filesAtStorage.forEach(fileName -> {
			final StreamResource resource = new StreamResource(fileName,
					() -> new ByteArrayInputStream(messageService.loadContentFromStorageLocation(messageByConnectorId.getStorageInfo(), fileName)));
//			resource.setContentType("text/xml");
			Anchor downloadAnchor = createDownloadButton(fileName, filesEnabled, resource);
			messageFilesLayout.add(downloadAnchor);
			
			
		});
		details.add(messageFilesLayout);
		messageFilesArea.add(details);
		
		messageFilesArea.setWidth("100vw");
//		add(messageEvidencesArea);
		messageFilesArea.setVisible(true);
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
	
	private void buildMessageEvidencesArea(DomibusConnectorClientMessage messageByConnectorId) {
		if(!messageByConnectorId.getEvidences().isEmpty()) {
			messageEvidencesArea.removeAll();
			
			Div evidences = new Div();
			evidences.setWidth("100vw");
			LumoLabel evidencesLabel = new LumoLabel();
			evidencesLabel.setText("Evidences:");
			evidencesLabel.getStyle().set("font-size", "20px");
			evidences.add(evidencesLabel);
			
			messageEvidencesArea.add(evidences);
			
			Div details = new Div();
			details.setWidth("100vw");
			
			Grid<DomibusConnectorClientConfirmation> grid = new Grid<>();
			
			grid.setItems(messageByConnectorId.getEvidences());
			
			grid.addColumn(DomibusConnectorClientConfirmation::getConfirmationType).setHeader("Confirmation Type").setWidth("250px");
			grid.addColumn(DomibusConnectorClientConfirmation::getReceived).setHeader("Received").setWidth("300px");
			grid.addColumn(DomibusConnectorClientConfirmation::getStorageInfo).setHeader("Storage Info").setWidth("300px");
			grid.addColumn(DomibusConnectorClientConfirmation::getStorageStatus).setHeader("Storage Status").setWidth("150px");
			
			grid.setWidth("1000px");
			grid.setHeight("210px");
			grid.setMultiSort(true);
			
			for(Column<DomibusConnectorClientConfirmation> col : grid.getColumns()) {
				col.setSortable(true);
				col.setResizable(true);
			}
			
			details.add(grid);
			
			
			
			messageEvidencesArea.add(details);
			
			messageEvidencesArea.setWidth("100vw");
//			add(messageEvidencesArea);
			messageEvidencesArea.setVisible(true);
		}
		
	}

}

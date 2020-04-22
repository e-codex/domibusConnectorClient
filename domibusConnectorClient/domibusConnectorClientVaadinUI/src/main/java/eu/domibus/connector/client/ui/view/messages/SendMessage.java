package eu.domibus.connector.client.ui.view.messages;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageFile;
import eu.domibus.connector.client.storage.DomibusConnectorClientMessageFileType;
import eu.domibus.connector.client.storage.DomibusConnectorClientStorageStatus;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.form.DynamicMessageForm;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

@Component
@UIScope
@Route(value = SendMessage.ROUTE, layout= Messages.class)
public class SendMessage  extends VerticalLayout implements HasUrlParameter<Long>,AfterNavigationObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String ROUTE = "sendMessage";

	org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendMessage.class);

	private VaadingConnectorClientUIServiceClient messageService;
	private Messages messagesView;
	private DynamicMessageForm messageForm = new DynamicMessageForm();
	private VerticalLayout messageFilesArea = new VerticalLayout();
	private Map<String, DomibusConnectorClientMessageFileType> filesAtStorage = null;
	
	public void setMessagesView(Messages messagesView) {
		this.messagesView = messagesView;
	}
	
	public SendMessage(@Autowired VaadingConnectorClientUIServiceClient messageService, @Autowired Messages messagesView) {
		
		this.messageService = messageService;
		setMessagesView(messagesView);
		this.messagesView.setSendMessageView(this);
		
		VerticalLayout messageDetailsArea = new VerticalLayout(); 
		messageForm.getStyle().set("margin-top","25px");

		messageDetailsArea.add(messageForm);
		messageForm.setEnabled(true);
		messageDetailsArea.setWidth("500px");
		add(messageDetailsArea);
		
		Button saveBtn = new Button(new Icon(VaadinIcon.EDIT));
		saveBtn.setText("Save new Message");
		saveBtn.addClickListener(e -> {
			BinderValidationStatus<DomibusConnectorClientMessage> validationStatus = messageForm.getBinder().validate();
			if(validationStatus.isOk()) {
				DomibusConnectorClientMessage msg = this.messageService.saveMessage(messageForm.getConnectorClientMessage());
				messageForm.setConnectorClientMessage(msg);
				loadPreparedMessage(msg.getId());
			}
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
		
		boolean filesEnabled = messageByConnectorId.getStorageInfo()!=null && !messageByConnectorId.getStorageInfo().isEmpty() && messageByConnectorId.getStorageStatus().equals(DomibusConnectorClientStorageStatus.STORED.name());

		if(filesEnabled) {
			
			Grid<DomibusConnectorClientMessageFile> grid = new Grid<>();

			grid.setItems(messageByConnectorId.getFiles().getFiles());

			grid.addComponentColumn(domibusConnectorClientMessageFile -> createDownloadButton(filesEnabled,domibusConnectorClientMessageFile.getFileName(),messageByConnectorId.getStorageInfo())).setHeader("Filename").setWidth("500px");
			grid.addColumn(DomibusConnectorClientMessageFile::getFileType).setHeader("Filetype").setWidth("450px");

			grid.setWidth("1000px");
			grid.setMultiSort(true);

			for(Column<DomibusConnectorClientMessageFile> col : grid.getColumns()) {
				col.setSortable(true);
				col.setResizable(true);
			}

			details.add(grid);

		}
		messageFilesArea.add(details);
		
		messageFilesArea.setWidth("100vw");
		messageFilesArea.setVisible(filesEnabled);
	}
	
	private Anchor createDownloadButton(boolean enabled, String fileName, String storageLocation) {
		Label button = new Label(fileName);
		final StreamResource resource = new StreamResource(fileName,
				() -> new ByteArrayInputStream(messageService.loadContentFromStorageLocation(storageLocation, fileName)));

		Anchor downloadAnchor = new Anchor();
		if (enabled) {
			downloadAnchor.setHref(resource);
		}else {
			downloadAnchor.removeHref();
		}
		downloadAnchor.getElement().setAttribute("download", true);
		downloadAnchor.setTarget("_blank");
		downloadAnchor.setTitle(fileName);
		downloadAnchor.add(button);

		return downloadAnchor;
	}
	
	private void clearSendMessage() {
		messageForm.setConnectorClientMessage(new DomibusConnectorClientMessage());
		
		messageFilesArea.removeAll();
		messageFilesArea.setVisible(false);
	}

	@Override
	public void afterNavigation(AfterNavigationEvent arg0) {
		if(this.messagesView.getMessagesListView()!=null)this.messagesView.getMessagesListView().setVisible(false);
		if(this.messagesView.getMessageDetailsView()!=null)this.messagesView.getMessageDetailsView().setVisible(false);
		this.setVisible(true);
	}

	@Override
	public void setParameter(BeforeEvent event
		    , @OptionalParameter Long parameter) {
		if(parameter!=null) {
	    	loadPreparedMessage(parameter);
	    }else {
	    	clearSendMessage();
	    }
	}

}

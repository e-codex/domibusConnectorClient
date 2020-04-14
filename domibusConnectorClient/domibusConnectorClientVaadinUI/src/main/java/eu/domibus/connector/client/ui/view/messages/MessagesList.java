package eu.domibus.connector.client.ui.view.messages;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;
import eu.domibus.connector.client.ui.view.sendmessage.ReplyToMessageDialog;

@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@UIScope
public class MessagesList extends VerticalLayout {
	
	private Grid<DomibusConnectorClientMessage> grid = new Grid<>();
	private List<DomibusConnectorClientMessage> fullList = null;
	private Messages messagesView;
	private VaadingConnectorClientUIServiceClient messageService;
	
	TextField fromPartyIdFilterText = new TextField();
	TextField toPartyIdFilterText = new TextField();
	TextField serviceFilterText = new TextField();
	TextField actionFilterText = new TextField();
//	TextField backendClientFilterText = new TextField();
	
	public void setMessagesView(Messages messagesView) {
		this.messagesView = messagesView;
	}

	public MessagesList(@Autowired VaadingConnectorClientUIServiceClient messageService) {
		this.messageService = messageService;
		
		fullList = messageService.getAllMessages().getMessages();
		
		grid.setItems(fullList);
		grid.addComponentColumn(domibusConnectorClientMessage -> getDetailsLink(domibusConnectorClientMessage.getId())).setHeader("Details").setWidth("50px");
		grid.addComponentColumn(domibusConnectorClientMessage -> deleteMessageLink(domibusConnectorClientMessage.getId())).setHeader("Delete").setWidth("50px");
		grid.addColumn(DomibusConnectorClientMessage::getEbmsMessageId).setHeader("ebmsMessageID").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getBackendMessageId).setHeader("backendMessageID").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getConversationId).setHeader("conversationID").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getFromPartyId).setHeader("From Party ID").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getToPartyId).setHeader("To Party ID").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getService).setHeader("Service").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getAction).setHeader("Action").setWidth("100px");
		grid.addColumn(DomibusConnectorClientMessage::getCreated).setHeader("Created").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getLastConfirmationReceived).setHeader("last confirmation received").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getStorageInfo).setHeader("Storage info").setWidth("150px");
		grid.addColumn(DomibusConnectorClientMessage::getStorageStatus).setHeader("Storage status").setWidth("100px");
		grid.setWidth("2380px");
		grid.setHeight("700px");
		grid.setMultiSort(true);
		
		for(Column<DomibusConnectorClientMessage> col : grid.getColumns()) {
			col.setSortable(true);
			col.setResizable(true);
		}
		
		HorizontalLayout filtering = createFilterLayout();
		
//		HorizontalLayout downloadLayout = createDownloadLayout();
			
		VerticalLayout main = new VerticalLayout(filtering, grid
//				, downloadLayout
				);
		main.setAlignItems(Alignment.STRETCH);
		main.setHeight("700px");
		add(main);
		setHeight("100vh");
		setWidth("100vw");
		reloadList();
		
	}
	
//	private HorizontalLayout createDownloadLayout() {
//		Div downloadExcel = new Div();
//		
//		Button download = new Button();
//		download.setIcon(new Image("frontend/images/xls.png", "XLS"));
//		
//		download.addClickListener(e -> {
//		
//			Element file = new Element("object");
//			Element dummy = new Element("object");
//			
//			Input oName = new Input();
//			
//			String name = "MessagesList.xls";
//			
//			StreamResource resource = new StreamResource(name,() -> getMessagesListExcel());
//			
//			resource.setContentType("application/xls");
//			
//			file.setAttribute("data", resource);
//			
//			Anchor link = null;
//			link = new Anchor(file.getAttribute("data"), "Download Document");
//			
//			UI.getCurrent().getElement().appendChild(oName.getElement(), file,
//					dummy);
//			oName.setVisible(false);
//			file.setVisible(false);
//			this.getUI().get().getPage().executeJavaScript("window.open('"+link.getHref()+"');");
//		});
//		
//		downloadExcel.add(download);
//		
//		HorizontalLayout downloadLayout = new HorizontalLayout(
//				downloadExcel
//			    );
//		downloadLayout.setWidth("100vw");
//		
//		return downloadLayout;
//	}
	
//	private InputStream getMessagesListExcel() {
//		return messageService.generateExcel(fullList);
//	}
	
	private HorizontalLayout createFilterLayout() {
		
		fromPartyIdFilterText.setPlaceholder("Filter by From Party ID");
		fromPartyIdFilterText.setWidth("180px");
		fromPartyIdFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		fromPartyIdFilterText.addValueChangeListener(e -> filter());

		
		toPartyIdFilterText.setPlaceholder("Filter by To Party ID");
		toPartyIdFilterText.setWidth("180px");
		toPartyIdFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		toPartyIdFilterText.addValueChangeListener(e -> filter());
		
		
		serviceFilterText.setPlaceholder("Filter by Service");
		serviceFilterText.setWidth("180px");
		serviceFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		serviceFilterText.addValueChangeListener(e -> filter());
		
		
		actionFilterText.setPlaceholder("Filter by Action");
		actionFilterText.setWidth("180px");
		actionFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		actionFilterText.addValueChangeListener(e -> filter());
		
		
//		backendClientFilterText.setPlaceholder("Filter by backend");
//		backendClientFilterText.setWidth("180px");
//		backendClientFilterText.setValueChangeMode(ValueChangeMode.EAGER);
//		backendClientFilterText.addValueChangeListener(e -> filter());
		
		Button clearAllFilterTextBtn = new Button(
				new Icon(VaadinIcon.CLOSE_CIRCLE));
		clearAllFilterTextBtn.setText("Clear Filter");
		clearAllFilterTextBtn.addClickListener(e -> {
			fromPartyIdFilterText.clear();
			toPartyIdFilterText.clear();
			serviceFilterText.clear();
			actionFilterText.clear();
//			backendClientFilterText.clear();
			});
		
		Button refreshListBtn = new Button(new Icon(VaadinIcon.REFRESH));
		refreshListBtn.setText("RefreshList");
		refreshListBtn.addClickListener(e -> {reloadList();});
		
		HorizontalLayout filtering = new HorizontalLayout(
				fromPartyIdFilterText,
				toPartyIdFilterText,
				serviceFilterText,
				actionFilterText,
//				backendClientFilterText,
				clearAllFilterTextBtn,
				refreshListBtn
			    );
		filtering.setWidth("100vw");
		
		return filtering;
	}
	
	private void filter() {
		LinkedList<DomibusConnectorClientMessage> target = new LinkedList<DomibusConnectorClientMessage>();
		for(DomibusConnectorClientMessage msg : fullList) {
			if((fromPartyIdFilterText.getValue().isEmpty() || msg.getFromPartyId()!=null && msg.getFromPartyId().toUpperCase().contains(fromPartyIdFilterText.getValue().toUpperCase()))
				&& (toPartyIdFilterText.getValue().isEmpty() || msg.getToPartyId()!=null && msg.getToPartyId().toUpperCase().contains(toPartyIdFilterText.getValue().toUpperCase()))
				&& (serviceFilterText.getValue().isEmpty() || msg.getService()!=null && msg.getService().toUpperCase().contains(serviceFilterText.getValue().toUpperCase()))
				&& (actionFilterText.getValue().isEmpty() || msg.getAction()!=null && msg.getAction().toUpperCase().contains(actionFilterText.getValue().toUpperCase()))
				) {
				target.addLast(msg);
			}
		}
		
		grid.setItems(target);
	}
	
	private Button getDetailsLink(Long l) {
		Button getDetails = new Button(new Icon(VaadinIcon.SEARCH));
		getDetails.addClickListener(e -> showConnectorMessage(l));
		return getDetails;
	}
	
	private Button deleteMessageLink(Long l) {
		Button deleteMessage = new Button(new Icon(VaadinIcon.DEL_A));
		deleteMessage.addClickListener(e -> deleteMessage(l));
		return deleteMessage;
	}
	
	private void deleteMessage(Long l) {
		Dialog diag = new Dialog();
		
		Div headerContent = new Div();
		Label header = new Label("Delete message");
		header.getStyle().set("font-weight", "bold");
		header.getStyle().set("font-style", "italic");
		headerContent.getStyle().set("text-align", "center");
		headerContent.getStyle().set("padding", "10px");
		headerContent.add(header);
		diag.add(headerContent);

		LumoLabel label = new LumoLabel("Are you sure you want to delete that message? All database references and storage files (if available) are deleted as well!");
		
		diag.add(label);

		diag.open();
	}
	
	private void showConnectorMessage(long l) {
		messagesView.showMessageDetails(l);
	}

	public void reloadList() {
		grid.setItems(messageService.getAllMessages().getMessages());
	}
	
	
	
}

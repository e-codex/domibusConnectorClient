package eu.domibus.connector.client.ui.view.messages;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessageList;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@UIScope
public class Search extends VerticalLayout {
	
	private Messages messagesView;
	private VaadingConnectorClientUIServiceClient messageService;
	
//	TextField searchMessageIdText = new TextField();
	TextField searchEbmsIdText = new TextField();
	TextField searchBackendMessageIdText = new TextField();
	TextField searchConversationIdText = new TextField();
	
	private VerticalLayout main = new VerticalLayout();

	public Search(@Autowired VaadingConnectorClientUIServiceClient messageService) {
		this.messageService = messageService;
		
//		HorizontalLayout connectorMessageIdSearch = new HorizontalLayout();
//		
//		searchMessageIdText.setPlaceholder("Search by Connector Message ID");
//		searchMessageIdText.setWidth("300px");
//		connectorMessageIdSearch.add(searchMessageIdText);
//		
//		Button searchConnectorMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
//		searchConnectorMessageIdBtn.addClickListener(e -> searchByConnectorMessageId(searchMessageIdText.getValue()));
//		connectorMessageIdSearch.add(searchConnectorMessageIdBtn);
//		
//		add(connectorMessageIdSearch);
		
		HorizontalLayout ebmsIdSearch = new HorizontalLayout();
		
		searchEbmsIdText.setPlaceholder("Search by EBMS Message ID");
		searchEbmsIdText.setWidth("300px");
		ebmsIdSearch.add(searchEbmsIdText);
		
		Button searchEbmsIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchEbmsIdBtn.addClickListener(e -> searchByEbmsId(searchEbmsIdText.getValue()));
		ebmsIdSearch.add(searchEbmsIdBtn);
		
		add(ebmsIdSearch);
		
		HorizontalLayout backendMessageIdSearch = new HorizontalLayout();
		
		searchBackendMessageIdText.setPlaceholder("Search by Backend Message ID");
		searchBackendMessageIdText.setWidth("300px");
		backendMessageIdSearch.add(searchBackendMessageIdText);
		
		Button searchBackendMessageIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchBackendMessageIdBtn.addClickListener(e -> searchByBackendMessageId(searchBackendMessageIdText.getValue()));
		backendMessageIdSearch.add(searchBackendMessageIdBtn);
		
		add(backendMessageIdSearch);
		
		HorizontalLayout conversationIdSearch = new HorizontalLayout();
		
		searchConversationIdText.setPlaceholder("Search by Conversation ID");
		searchConversationIdText.setWidth("300px");
		conversationIdSearch.add(searchConversationIdText);
		
		Button searchConversationIdBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchConversationIdBtn.addClickListener(e -> searchByConversationId(searchConversationIdText.getValue()));
		conversationIdSearch.add(searchConversationIdBtn);
		
		add(conversationIdSearch);
		
		HorizontalLayout dateSearch = new HorizontalLayout();
		
		DatePicker fromDate = new DatePicker();
//		fromDate.setLocale(Locale.ENGLISH);
		fromDate.setLabel("From Date");
		fromDate.setErrorMessage("From Date invalid!");
		dateSearch.add(fromDate);
		
		DatePicker toDate = new DatePicker();
//		toDate.setLocale(Locale.ENGLISH);
		toDate.setLabel("To Date");
		toDate.setErrorMessage("To Date invalid!");
		dateSearch.add(toDate);
		
		Button searchPeriodBtn = new Button(new Icon(VaadinIcon.SEARCH));
		searchPeriodBtn.addClickListener(e -> searchByPeriod(asDate(fromDate.getValue()), asDate(toDate.getValue())));
		dateSearch.add(searchPeriodBtn);
		
		dateSearch.setAlignItems(Alignment.END);
		
		add(dateSearch);
		
		add(main);

		setHeight("100vh");
		setWidth("100vh");
	}
	
	private void addGridWithData(List<DomibusConnectorClientMessage> messages) {
		main.removeAll();
		
		Grid<DomibusConnectorClientMessage> grid = new Grid<>();
		
		grid.setItems(messages);
		grid.addComponentColumn(domibusConnectorClientMessage -> getDetailsLink(domibusConnectorClientMessage.getId())).setHeader("Details").setWidth("20px");
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
		grid.setWidth("2330px");
		grid.setHeight("700px");
		grid.setMultiSort(true);
		
		for(Column<DomibusConnectorClientMessage> col : grid.getColumns()) {
			col.setSortable(true);
			col.setResizable(true);
		}
		grid.setVisible(true);
		
		
		main.add(grid);
		
//		HorizontalLayout downloadLayout = createDownloadLayout(messages);
		
//		main.add(downloadLayout);
		main.setAlignItems(Alignment.STRETCH);
		main.setHeight("400px");
//		main.setSizeFull();
//		main.setVisible(false);
		
//		add(main);
	}
	
//	private HorizontalLayout createDownloadLayout(LinkedList<WebMessage> messages) {
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
//			StreamResource resource = new StreamResource(name,() -> getMessagesListExcel(messages));
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
	
//	private InputStream getMessagesListExcel(LinkedList<WebMessage> messages) {
//		return messageService.generateExcel(messages);
//	}

	private void searchByBackendMessageId(String backendMessageId) {
		DomibusConnectorClientMessageList fullList = messageService.getMessagesByBackendMessageId(backendMessageId);
		addGridWithData(fullList.getMessages());
	}

	private void searchByEbmsId(String ebmsId) {
		DomibusConnectorClientMessageList fullList = messageService.getMessagesByEbmsId(ebmsId);
		addGridWithData(fullList.getMessages());
	}

	private void searchByPeriod(Date fromDate, Date toDate) {
		toDate = new Date(toDate.getTime() + TimeUnit.DAYS.toMillis( 1 ));
		DomibusConnectorClientMessageList fullList = messageService.getMessagesByPeriod(fromDate, toDate);
		addGridWithData(fullList.getMessages());
	}
	
	private void searchByConversationId(String conversationId) {
		DomibusConnectorClientMessageList fullList = messageService.getMessagesByConversationId(conversationId);
		addGridWithData(fullList.getMessages());
	}

//	private void searchByConnectorMessageId(String connectorMessageId) {
//		searchMessageIdText.setValue("");
//		showConnectorMessage(connectorMessageId);
//	}
	
	private Button getDetailsLink(long l) {
		Button getDetails = new Button(new Icon(VaadinIcon.SEARCH));
		getDetails.addClickListener(e -> showConnectorMessage(l));
		return getDetails;
	}
	
	private void showConnectorMessage(long l) {
		messagesView.showMessageDetails(l);
	}
	
	public static Date asDate(LocalDate localDate) {
	    return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	  }

	public void setMessagesView(Messages messagesView) {
		this.messagesView = messagesView;
	}
}

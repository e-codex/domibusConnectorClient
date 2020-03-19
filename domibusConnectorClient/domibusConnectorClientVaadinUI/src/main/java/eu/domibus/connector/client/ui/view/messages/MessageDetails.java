package eu.domibus.connector.client.ui.view.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientConfirmation;
import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;
import eu.domibus.connector.client.ui.component.LumoLabel;
import eu.domibus.connector.client.ui.form.DomibusConnectorClientMessageForm;
import eu.domibus.connector.client.ui.service.VaadingConnectorClientUIServiceClient;

@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@UIScope
public class MessageDetails extends VerticalLayout {

	private VaadingConnectorClientUIServiceClient messageService;
	private DomibusConnectorClientMessageForm messageForm = new DomibusConnectorClientMessageForm();
	private VerticalLayout messageEvidencesArea = new VerticalLayout();  

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
		
		add(messageEvidencesArea);

		setSizeFull();
//		setHeight("100vh");
	}


	public void loadMessageDetails(Long connectorMessageId) {
		DomibusConnectorClientMessage messageByConnectorId = messageService.getMessageById(connectorMessageId);
		messageForm.setConnectorClientMessage(messageByConnectorId);

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

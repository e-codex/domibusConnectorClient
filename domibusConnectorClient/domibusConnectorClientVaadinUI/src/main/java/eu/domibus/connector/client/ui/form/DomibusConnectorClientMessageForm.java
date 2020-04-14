package eu.domibus.connector.client.ui.form;

import java.time.ZoneId;
import java.util.Date;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.LocalDateTimeToDateConverter;
import com.vaadin.flow.data.converter.StringToDateConverter;
import com.vaadin.flow.function.ValueProvider;

import eu.domibus.connector.client.rest.model.DomibusConnectorClientMessage;

@HtmlImport("styles/shared-styles.html")
public class DomibusConnectorClientMessageForm extends FormLayout {

	private TextField backendMessageId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField ebmsMessageId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField conversationId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField originalSender = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField finalRecipient = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField service = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField action = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField fromPartyId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField fromPartyRole = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField fromPartyType = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField toPartyId = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField toPartyRole = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField toPartyType = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField storageStatus = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField storageInfo = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField lastConfirmationReceived = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField createdString = FormsUtil.getFormattedTextFieldReadOnly();
	private TextField confirmationTriggeredString = FormsUtil.getFormattedTextFieldReadOnly();
	
	private Binder<DomibusConnectorClientMessage> binder = new Binder<>(DomibusConnectorClientMessage.class);
	
	public DomibusConnectorClientMessageForm() {
		fillForm();
//		setResponsiveSteps(new ResponsiveStep("500px", 2, LabelsPosition.TOP));
	}
	
	private void fillForm() {
		binder.bindInstanceFields(this);
		
		addFormItem(backendMessageId, "Backend Message ID"); 
		addFormItem(ebmsMessageId, "EBMS Message ID"); 
		addFormItem(conversationId, "Conversation ID"); 
		addFormItem(originalSender, "Original Sender"); 
		addFormItem(finalRecipient, "Final Recipient"); 
		addFormItem(service, "Service"); 
		addFormItem(action, "Action"); 
		addFormItem(fromPartyId, "From Party ID"); 
		addFormItem(fromPartyRole, "From Party Role"); 
		addFormItem(fromPartyType, "From Party Type"); 
		addFormItem(toPartyId, "To Party ID"); 
		addFormItem(toPartyRole, "From Party Role"); 
		addFormItem(toPartyType, "From Party Type"); 
		addFormItem(storageStatus, "Storage Status");
		addFormItem(storageInfo, "Storage Info");
		addFormItem(lastConfirmationReceived, "Last Confirmation Received");
		addFormItem(createdString, "Message created at");
		addFormItem(confirmationTriggeredString, "Confirmation triggered at");
		
		
	}

	public void setConnectorClientMessage(DomibusConnectorClientMessage message) {
		this.removeAll();
		fillForm();
		binder.setBean(message);
	}
	
	public DomibusConnectorClientMessage getConnectorClientMessage() {
		return binder.getBean();
	}
	
}

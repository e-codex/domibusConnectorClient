package eu.domibus.connector.client.controller.persistence.model;

public enum PDomibusConnectorClientMessageStatus {
	PREPARED, SENDING, SENT, RECEIVING, RECEIVED, CONFIRMATION_TRIGGERED, CONFIRMATION_RECEPTION_FAILED, CONFIRMED, FAILED, REJECTED, ACCEPTED, DELIVERED, DELIVERY_FAILED;
}

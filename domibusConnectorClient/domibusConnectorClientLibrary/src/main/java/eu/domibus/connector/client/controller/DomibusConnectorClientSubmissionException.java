package eu.domibus.connector.client.controller;

import eu.domibus.connector.client.link.exception.DomibusConnectorBackendWebServiceClientException;

public class DomibusConnectorClientSubmissionException extends RuntimeException {

    public DomibusConnectorClientSubmissionException(String error, Exception e) {
        super(error, e);
    }
}

package eu.domibus.connector.nbc;

import java.util.List;

import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageError;
import eu.domibus.connector.nbc.exception.DomibusConnectorNationalBackendClientException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(DomibusConnectorNationalBackendClient.class)
public class DomibusConnectorNationalBackendClientDefaultImpl implements DomibusConnectorNationalBackendClient {

    @Override
    public void deliverMessage(Message message) throws DomibusConnectorNationalBackendClientException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "deliverMessage");

    }

    @Override
    public String[] requestMessagesUnsent() throws DomibusConnectorNationalBackendClientException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "requestMessagesUnsent");
    }

    @Override
    public void requestMessage(Message message) throws DomibusConnectorNationalBackendClientException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "requestMessage");
    }

    @Override
    public Message[] requestConfirmations() throws DomibusConnectorNationalBackendClientException,
            ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "requestConfirmations");
    }

    @Override
    public void deliverLastEvidenceForMessage(Message confirmationMessage)
            throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorNationalBackendClient",
                "deliverLastEvidenceForMessage");
    }

    @Override
    public String requestMessageStatusFromGateway(Message message)
            throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorNationalBackendClient",
                "requestMessageStatusFromGateway");
    }

    @Override
    public List<MessageError> requestMessageErrors(Message message)
            throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        throw new ImplementationMissingException("DomibusConnectorNationalBackendClient", "requestMessageErrors");
    }

}

package eu.domibus.connector.mapping;

import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.mapping.exception.DomibusConnectorContentMapperException;
import org.springframework.stereotype.Component;

/**
 * This is a very simple example for the implementation of the interface
 * {@link DomibusConnectorContentMapper}.
 * 
 * @author riederb
 * 
 */
@Component
public class DomibusConnectorContentMapperDefaultImpl implements DomibusConnectorContentMapper {

    /**
     * This implementation of the interface method
     * {@link DomibusConnectorContentMapper#mapInternationalToNational(Message)}
     * only copies the contents of
     * {@link MessageContent#getInternationalContent()} and stores them into
     * {@link MessageContent#setNationalXmlContent(byte[])}.
     * 
     */
    @Override
    public void mapInternationalToNational(Message message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException {
        MessageContent messageContent = message.getMessageContent();
        messageContent.setNationalXmlContent(messageContent.getInternationalContent());
    }

    /**
     * This implementation of the interface method
     * {@link DomibusConnectorContentMapper#mapNationalToInternational(Message)}
     * only copies the contents of
     * {@link MessageContent#getNationalXmlContent()} and stores them into
     * {@link MessageContent#setInternationalContent(byte[])}.
     * 
     */
    @Override
    public void mapNationalToInternational(Message message) throws DomibusConnectorContentMapperException,
            ImplementationMissingException {
        MessageContent messageContent = message.getMessageContent();
        messageContent.setInternationalContent(messageContent.getNationalXmlContent());
    }

}

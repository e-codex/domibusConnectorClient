
package eu.domibus.connector.v35client.testutil;

import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.common.message.MessageContent;
import eu.domibus.connector.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.mapping.exception.DomibusConnectorContentMapperException;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class SimpleContentMapper implements DomibusConnectorContentMapper {

    @Override
    public void mapInternationalToNational(Message message) throws DomibusConnectorContentMapperException, ImplementationMissingException {
        MessageContent messageContent = message.getMessageContent();
        if (messageContent != null) {
            byte[] internationalContent = messageContent.getInternationalContent();
            messageContent.setNationalXmlContent(internationalContent);
        }
    }

    @Override
    public void mapNationalToInternational(Message message) throws DomibusConnectorContentMapperException, ImplementationMissingException {
        MessageContent messageContent = message.getMessageContent();
        if (messageContent != null) {
            byte[] nationalXmlContent = messageContent.getNationalXmlContent();
            messageContent.setInternationalContent(nationalXmlContent);
        }
    }

}

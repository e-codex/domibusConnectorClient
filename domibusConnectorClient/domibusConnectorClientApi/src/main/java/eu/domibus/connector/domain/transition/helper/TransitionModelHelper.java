
package eu.domibus.connector.domain.transition.helper;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.math.BigDecimal;
import javax.annotation.Nonnull;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class TransitionModelHelper {

    /**
     *  A message is a evidence message if there is no message content AND
     *  at least on confirmation
     * @param message - the message that should be checked
     * @return if it is an evidence message
     */
    public static boolean isEvidenceMessage(@Nonnull DomibusConnectorMessageType message) {
        return (message.getMessageContent() == null && 
                message.getMessageConfirmations() != null && 
                message.getMessageConfirmations().size() > 0);
        
    }

}

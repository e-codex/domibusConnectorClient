package eu.domibus.connector.v35client;

import org.springframework.util.CollectionUtils;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class TransitionModelHelper {
	
	public static boolean isEvidenceMessage(DomibusConnectorMessageType messageTO) {
		return messageTO.getMessageContent()==null && !CollectionUtils.isEmpty(messageTO.getMessageConfirmations());
	}

}

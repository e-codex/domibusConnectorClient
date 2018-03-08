package eu.domibus.connector.client.exception;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;

public class DomibusConnectorMessageException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 2899706995862182574L;

    public DomibusConnectorMessageException() {
    }

    public DomibusConnectorMessageException(DomibusConnectorMessageType message, Class<?> source) {
        super();
//        storeException(message, this, source);
    }

    public DomibusConnectorMessageException(DomibusConnectorMessageType message, Throwable cause, Class<?> source) {
        super(cause);
//        storeException(message, this, source);
    }

    public DomibusConnectorMessageException(DomibusConnectorMessageType message, String text, Class<?> source) {
        super(text);
//        storeException(message, this, source);
    }

    public DomibusConnectorMessageException(DomibusConnectorMessageType message, String text, Throwable cause, Class<?> source) {
        super(text, cause);
//        storeException(message, this, source);
    }

//    private void storeException(Message message, Throwable cause, Class<?> source) {
//        DomibusConnectorPersistenceService persistenceService = (DomibusConnectorPersistenceService) DomibusApplicationContextManager
//                .getApplicationContext().getBean("persistenceService");
//        try {
//            persistenceService.persistMessageErrorFromException(message, cause, source);
//        } catch (PersistenceException e) {
//            e.printStackTrace();
//        }
//    }

}

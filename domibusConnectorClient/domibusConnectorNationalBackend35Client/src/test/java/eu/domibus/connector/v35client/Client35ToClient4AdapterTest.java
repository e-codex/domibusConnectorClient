
package eu.domibus.connector.v35client;

import eu.domibus.connector.client.connection.SubmitMessageToConnector;
import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.nbc.exception.DomibusConnectorNationalBackendClientException;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */
public class Client35ToClient4AdapterTest {

    Client35ToClient4Adapter translateService;

    DomibusConnectorNationalBackendClient nationalBackendClient;    
    SubmitMessageToConnector submitMessageToConnectorService;
    Map35MessageTov4Message map35MessageTov4Message;
    
    @Before
    public void setUp() {
        translateService = new Client35ToClient4Adapter();
        
        nationalBackendClient = Mockito.mock(DomibusConnectorNationalBackendClient.class);        
        submitMessageToConnectorService = Mockito.mock(SubmitMessageToConnector.class);        
        map35MessageTov4Message = Mockito.spy(new Map35MessageTov4Message());
        
    }

    @Test
    public void testTransportMessagesToController() throws DomibusConnectorNationalBackendClientException, ImplementationMissingException {
        Mockito.when(nationalBackendClient.requestMessagesUnsent()).thenReturn(new String[] {"nmsg1", "nmsg2", "nmsg3"});
        
        translateService.transportMessagesToController();
                
        //TODO: complete tests!
    }

}
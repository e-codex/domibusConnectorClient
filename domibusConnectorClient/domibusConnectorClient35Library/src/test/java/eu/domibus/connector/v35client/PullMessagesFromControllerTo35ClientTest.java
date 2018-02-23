
package eu.domibus.connector.v35client;

import eu.domibus.connector.client.connection.FetchMessagesFromConnector;
import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.testutil.TransitionCreator;
import eu.domibus.connector.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.nbc.exception.DomibusConnectorNationalBackendClientException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */
public class PullMessagesFromControllerTo35ClientTest {

    @Mock
    private DomibusConnectorNationalBackendClient nationalBackendClient;
        
    @Mock
    private FetchMessagesFromConnector fetchMessagesFromConnector;
        
    private MapV4TransitionMessageTo35Message mapTo35Message;
    
    @Mock
    private DomibusConnectorContentMapper domibusConnectorContentMapper;
    
    private PullMessagesFromControllerTo35Client pullMessagesFromControllerTo35Client;
    
    @Before
    public void setUp() {
        mapTo35Message = Mockito.spy(new MapV4TransitionMessageTo35Message());
        MockitoAnnotations.initMocks(this);
        
        pullMessagesFromControllerTo35Client = new PullMessagesFromControllerTo35Client();
        pullMessagesFromControllerTo35Client.setDomibusConnectorContentMapper(domibusConnectorContentMapper);
        pullMessagesFromControllerTo35Client.setFetchMessagesFromConnector(fetchMessagesFromConnector);
        pullMessagesFromControllerTo35Client.setMapTo35Message(mapTo35Message);
        pullMessagesFromControllerTo35Client.setNationalBackendClient(nationalBackendClient);
    }

    @Test
    public void testPullMessagesFromController() throws DomibusConnectorNationalBackendClientException, ImplementationMissingException, DomibusConnectorContentMapperException {        
        List<DomibusConnectorMessageType> messagesTo = new ArrayList<>();
        messagesTo.add(TransitionCreator.createEpoMessage());
        messagesTo.add(TransitionCreator.createEvidenceNonDeliveryMessage());
        
        Mockito.when(fetchMessagesFromConnector.fetchMessages()).thenReturn(messagesTo);
        
        
        pullMessagesFromControllerTo35Client.pullMessagesFromController();
        
        //Mockito.verify(domibusConnectorContentMapper, times(1)).mapInternationalToNational(any(Message.class));
        
        Mockito.verify(nationalBackendClient, times(1)).deliverLastEvidenceForMessage(any(Message.class));
        Mockito.verify(nationalBackendClient, times(1)).deliverMessage(any(Message.class));
        
        
       
        
    }

}

package eu.domibus.connector.v35client;

import eu.domibus.connector.client.connection.SubmitMessageToConnector;
import eu.domibus.connector.common.exception.ImplementationMissingException;
import eu.domibus.connector.common.message.Message;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.mapping.DomibusConnectorContentMapper;
import eu.domibus.connector.mapping.exception.DomibusConnectorContentMapperException;
import eu.domibus.connector.nbc.DomibusConnectorNationalBackendClient;
import eu.domibus.connector.nbc.exception.DomibusConnectorNationalBackendClientException;
import eu.domibus.connector.v35client.testutil.SimpleContentMapper;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.Ignore;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */
public class Client35ToClient4AdapterTest {

    PushMessagesToControllerFrom35Client translateService;
    DomibusConnectorNationalBackendClient nationalBackendClient;    
    SubmitMessageToConnector submitMessageToConnectorService;
    Map35MessageTov4Message map35MessageTov4Message;
    DomibusConnectorContentMapper domibusConnectorContentMapper;
    
    @Before
    public void setUp() throws DomibusConnectorContentMapperException, ImplementationMissingException {
        translateService = new PushMessagesToControllerFrom35Client();
        
        nationalBackendClient = Mockito.mock(DomibusConnectorNationalBackendClient.class);        
        submitMessageToConnectorService = Mockito.mock(SubmitMessageToConnector.class);        
        map35MessageTov4Message = Mockito.spy(new Map35MessageTov4Message());        
        domibusConnectorContentMapper = Mockito.spy(new SimpleContentMapper());

        translateService.setDomibusConnectorContentMapper(domibusConnectorContentMapper);
        translateService.setNationalBackendClient(nationalBackendClient);
        translateService.setMap35MessageTov4Message(map35MessageTov4Message);
        translateService.setSubmitMessageToConnectorService(submitMessageToConnectorService);
        
    }

    @Test
    public void testTransportMessagesToController() throws DomibusConnectorNationalBackendClientException, ImplementationMissingException, DomibusConnectorContentMapperException {
        Mockito.when(nationalBackendClient.requestMessagesUnsent()).thenReturn(new String[] {"nmsg1", "nmsg2", "nmsg3"});
        
        translateService.transportMessagesToController();
        //3 Messages should be requested from national system
        Mockito.verify(nationalBackendClient, Mockito.times(3)).requestMessage(any(Message.class));
        
        //3 messages must be mapped to international content!
        Mockito.verify(domibusConnectorContentMapper, Mockito.times(3)).mapNationalToInternational(any(Message.class));
        
        //3 messages should be handed over to submitMessageService
        Mockito.verify(submitMessageToConnectorService, Mockito.times(3)).submitMessage(Mockito.any(DomibusConnectorMessageType.class));       
    }

    @Test
    public void testTransportOneMessageToController() throws DomibusConnectorNationalBackendClientException, DomibusConnectorContentMapperException, ImplementationMissingException {
        translateService.transportOneMessageToController("nmsg1");
        //1 Message should be requested from national system
        Mockito.verify(nationalBackendClient, Mockito.times(1)).requestMessage(any(Message.class));
        
        //1 Message must be mapped to international content!
        Mockito.verify(domibusConnectorContentMapper, Mockito.times(1)).mapNationalToInternational(any(Message.class));
        
        //1 Message should be handed over to submitMessageService
        Mockito.verify(submitMessageToConnectorService, Mockito.times(1)).submitMessage(Mockito.any(DomibusConnectorMessageType.class));  
    }
    
    
    @Test
    @Ignore("test is inncomplete - the national client must be informed about the failure!")
    public void testTransportOneMessageToController_mapNationalToInternationalFails_nationalClientShouldBeInformed() throws DomibusConnectorNationalBackendClientException, DomibusConnectorContentMapperException, ImplementationMissingException {
        Mockito.doThrow(new DomibusConnectorNationalBackendClientException()).when(nationalBackendClient).requestMessage(any(Message.class));
        
        translateService.transportOneMessageToController("nmsg1");
        //1 Message should be requested from national system
        Mockito.verify(nationalBackendClient, Mockito.times(1)).requestMessage(any(Message.class));
                
        //Mockito.when(nationalBackendClient.requestMessage(any(Message.class)));
        
        //1 Message must be mapped to international content!
        Mockito.verify(domibusConnectorContentMapper, Mockito.times(0)).mapNationalToInternational(any(Message.class));
        
        //1 Message should be handed over to submitMessageService
        Mockito.verify(submitMessageToConnectorService, Mockito.times(0)).submitMessage(Mockito.any(DomibusConnectorMessageType.class));        
                
    }
    
//    @Test
//    public void testTransportOneMessageToController_mapNationalToInternationalFails() throws DomibusConnectorNationalBackendClientException, DomibusConnectorContentMapperException, ImplementationMissingException {
//        translateService.transportOneMessageToController("nmsg1");
//        //1 Message should be requested from national system
//        Mockito.verify(nationalBackendClient, Mockito.times(3)).requestMessage(any(Message.class));
//        
//        //1 Message must be mapped to international content!
//        Mockito.verify(domibusConnectorContentMapper, Mockito.times(3)).mapNationalToInternational(any(Message.class));
//        
//        //1 Message should be handed over to submitMessageService
//        Mockito.verify(submitMessageToConnectorService, Mockito.times(3)).submitMessage(Mockito.any(DomibusConnectorMessage.class));          
//    }
    
}
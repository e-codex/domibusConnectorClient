
package eu.domibus.connector.client.connection.ws.impl;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.testutil.TransitionCreator;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephan Spindler <stephan.spindler@extern.brz.gv.at>
 */
public class DomibusConnectorClientWsTransportMessageServiceTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorClientWsTransportMessageServiceTest.class);
    
    private DomibusConnectorClientWsTransportMessageService service;
    
    private DomibusConnectorBackendWebService mockedWebService;

    
    @Before
    public void setUp() {
         
         
        mockedWebService = Mockito.mock(DomibusConnectorBackendWebService.class);
        Mockito.when(mockedWebService.requestMessages(any(EmptyRequestType.class))).thenReturn(TransitionCreator.createMessages());
         
         
        service = new DomibusConnectorClientWsTransportMessageService();
        service.setWebService(mockedWebService);
    }

    @Test
    public void testFetchMessages() {
        
        List<DomibusConnectorMessage> fetchMessages = service.fetchMessages();
        assertThat(fetchMessages).hasSize(1);
        
    }
    
    
    @Test
    public void testSubmitMessage() {
        final ArrayList<DomibusConnectorMessageType> rcvMessages = new ArrayList<>();
        
        Mockito.when(mockedWebService.submitMessage(any(DomibusConnectorMessageType.class)))
            .thenAnswer(new Answer<DomibsConnectorAcknowledgementType>() {
                    @Override
                    public DomibsConnectorAcknowledgementType answer(InvocationOnMock invocation) throws Throwable {
                        DomibusConnectorMessageType type = invocation.getArgumentAt(0, DomibusConnectorMessageType.class);
                        rcvMessages.add(type);

                        DomibsConnectorAcknowledgementType ack = new DomibsConnectorAcknowledgementType();
                        ack.setMessageId("MYID");
                        ack.setResult(true);

                        return ack;
                    }        
            });
      
        
        DomibusConnectorMessage msg = DomainEntityCreator.createMessage();        
        
        DomibusConnectorMessage submitMessage = service.submitMessage(msg);
        
        assertThat(submitMessage.getConnectorMessageId()).isEqualTo("MYID");
        assertThat(rcvMessages).hasSize(1);
        
    }
    
}
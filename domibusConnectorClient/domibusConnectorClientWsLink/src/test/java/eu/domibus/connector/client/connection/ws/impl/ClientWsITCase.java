
package eu.domibus.connector.client.connection.ws.impl;

import eu.domibus.connector.client.connection.ws.linktest.client.BackendClient;
import eu.domibus.connector.client.connection.ws.linktest.server.BackendServer;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import static eu.domibus.connector.domain.testutil.DomainEntityCreator.createEvidenceNonDeliveryMessage;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class ClientWsITCase {

    //TODO: start backend
    //TODO: start client
    
    static ApplicationContext backendContext;
    static ApplicationContext clientContext;
    
    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        String[] serverProfiles = new String[] {};
        String[] serverProperties = new String[] {"server.port=0"};
        backendContext = BackendServer.startSpringApplication(serverProfiles, serverProperties);
        
        String port = backendContext.getEnvironment().getProperty("local.server.port");
        String backendAddress = "http://localhost:" + port + "/services/backend";
        System.out.println("Backend address is: " + backendAddress);
        
        
        String[] clientProfiles = new String[] {};
        String[] clientProperties = new String[] {"server.port=0", 
            "connector.backend.ws.address=" + backendAddress, 
            "ws.backendclient.name=bob" };
        
        clientContext = BackendClient.startSpringApplication(clientProfiles, clientProperties);
        
        //Thread.sleep(20000);
    }
    
    List<DomibusConnectorMessage> messages;
    
    DomibusConnectorClientWsTransportMessageService transportWs;
    
    @Before
    public void setUp() {
        messages = backendContext.getBean("submittedMessages", List.class);
        transportWs = clientContext.getBean(DomibusConnectorClientWsTransportMessageService.class);
    }
    
    @Test
    public void testSendMessageToServer() {
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();      
        DomibusConnectorMessage submitMessage = transportWs.submitMessage(message);
        
        assertThat(submitMessage.getConnectorMessageId()).isNotNull();
    }
    
    @Test
    public void testSendNonDeliveryMessageToServer() {
        DomibusConnectorMessage message = createEvidenceNonDeliveryMessage();
        
        DomibusConnectorMessage submitMessage = transportWs.submitMessage(message);
        
        assertThat(submitMessage.getConnectorMessageId()).isNotNull();
    }
    
    
    @Test
    public void testRequestMessagesFromServer() {
        List<DomibusConnectorMessage> fetchMessages = transportWs.fetchMessages();

        assertThat(fetchMessages).hasSize(1);
    }
    
    
    
    
}

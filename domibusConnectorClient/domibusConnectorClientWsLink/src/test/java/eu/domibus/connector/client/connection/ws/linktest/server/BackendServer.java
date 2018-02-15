
package eu.domibus.connector.client.connection.ws.linktest.server;

import eu.domibus.connector.domain.transition.DomibsConnectorAcknowledgementType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessagesType;
import eu.domibus.connector.domain.transition.testutil.TransitionCreator;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.backend.webservice.EmptyRequestType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@SpringBootApplication(scanBasePackageClasses = BackendServer.class)
@Configuration
@ImportResource("classpath:/testservice.xml")
public class BackendServer {

    
    public static ApplicationContext startSpringApplication(String[] profiles, String[] properties) {

        boolean web = true;
        
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        SpringApplication springApp = builder.bannerMode(Banner.Mode.OFF)
                .sources(BackendServer.class)
                .properties(properties)
                .profiles(profiles)
                .web(web)
                .build();

        ConfigurableApplicationContext appContext = springApp.run();
        
        return appContext;
    }
    
    @Bean("submittedMessages")
    public List<DomibusConnectorMessageType> submittedMessages() {
        return Collections.synchronizedList(new ArrayList<>());
    }
    
    @Bean("connectorBackendImpl")
    public DomibusConnectorBackendWebService domibusConnectorBackendWebService() {
        
        List<DomibusConnectorMessageType> submittedMessages = submittedMessages();
        
        DomibusConnectorBackendWebService backend = new DomibusConnectorBackendWebService() {
            @Override
            public DomibusConnectorMessagesType requestMessages(EmptyRequestType requestMessagesRequest) {
                return TransitionCreator.createMessages();
            }

            @Override
            public DomibsConnectorAcknowledgementType submitMessage(DomibusConnectorMessageType submitMessageRequest) {
                submittedMessages.add(submitMessageRequest);
                DomibsConnectorAcknowledgementType response = new DomibsConnectorAcknowledgementType();
                response.setMessageId(UUID.randomUUID().toString());
                response.setResult(true);
                return response;
            }
            
        };
        
        return backend;
    }
    
}

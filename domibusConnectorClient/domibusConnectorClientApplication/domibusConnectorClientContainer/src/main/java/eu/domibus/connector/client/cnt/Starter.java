package eu.domibus.connector.client.cnt;

import eu.domibus.connector.client.starter.DomibusConnectorClient;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

@DomibusConnectorClient
public class Starter {

    public static void main(String []args) {
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder();
        springApplicationBuilder
                .sources(Starter.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}

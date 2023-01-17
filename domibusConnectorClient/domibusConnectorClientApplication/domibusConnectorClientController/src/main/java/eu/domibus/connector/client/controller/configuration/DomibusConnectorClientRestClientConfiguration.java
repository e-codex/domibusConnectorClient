package eu.domibus.connector.client.controller.configuration;

import eu.domibus.connector.client.controller.rest.impl.DomibusConnectorClientDeliveryRestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(DomibusConnectorClientRestClientConfigurationProperties.class)
@PropertySource("classpath:/connector-client-controller-default.properties")
@ConditionalOnProperty(prefix = DomibusConnectorClientRestClientConfigurationProperties.PREFIX, value = "enabled", havingValue = "true")
public class DomibusConnectorClientRestClientConfiguration {

    @Bean
    public DomibusConnectorClientDeliveryRestClient deliveryRestClient(WebClient.Builder builder, DomibusConnectorClientRestClientConfigurationProperties props) {
        WebClient webClient = builder.baseUrl(props.getUrl()).build();
        DomibusConnectorClientDeliveryRestClient restClient = new DomibusConnectorClientDeliveryRestClient();
        restClient.setDeliveryRestClient(webClient);
        restClient.setDeliverNewConfirmationMethodUrl(props.getDeliverNewConfirmationMethodUrl());
        restClient.setDeliverNewMessageMethodUrl(props.getDeliverNewMessageMethodUrl());
        return restClient;
    }

}

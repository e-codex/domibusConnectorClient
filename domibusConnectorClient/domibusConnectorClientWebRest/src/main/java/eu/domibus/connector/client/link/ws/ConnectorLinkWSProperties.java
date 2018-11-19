package eu.domibus.connector.client.link.ws;


import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreAndTrustStoreConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "connector-client.connector-link")
@Validated
@Valid
public class ConnectorLinkWSProperties {


    @NotNull
    private String connectorAddress;

    @NotNull
    private String publishAddress = "/domibusConnectorDeliveryWebservice";

    private Resource wsPolicy = new ClassPathResource("wsdl/backend.policy.xml");

    @NestedConfigurationProperty
    private CxfTrustKeyStoreConfigurationProperties cxf;

//    @NestedConfigurationProperty
//    private KeyAndKeyStoreAndTrustStoreConfigurationProperties tls;


    public String getConnectorAddress() {
        return connectorAddress;
    }

    public void setConnectorAddress(String connectorAddress) {
        this.connectorAddress = connectorAddress;
    }

    public String getPublishAddress() {
        return publishAddress;
    }

    public void setPublishAddress(String publishAddress) {
        this.publishAddress = publishAddress;
    }

    public Resource getWsPolicy() {
        return wsPolicy;
    }

    public void setWsPolicy(Resource wsPolicy) {
        this.wsPolicy = wsPolicy;
    }

    public CxfTrustKeyStoreConfigurationProperties getCxf() {
        return cxf;
    }

    public void setCxf(CxfTrustKeyStoreConfigurationProperties cxf) {
        this.cxf = cxf;
    }
}

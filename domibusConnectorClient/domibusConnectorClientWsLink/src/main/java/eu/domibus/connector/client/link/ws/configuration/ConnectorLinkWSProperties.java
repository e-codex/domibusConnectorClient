package eu.domibus.connector.client.link.ws.configuration;


import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Properties;


@ConfigurationProperties(prefix = ConnectorLinkWSProperties.PREFIX)
@Validated
@Valid
public class ConnectorLinkWSProperties {

    private static final Logger LOGGER = LogManager.getLogger(ConnectorLinkWSProperties.class);

    public static final String PREFIX = "connector-client.connector-link.ws";
    public static final String ENABLED_PROPERTY_NAME = "enabled";
    public static final String PUSH_ENABLED_PROPERTY_NAME = "pushEnabled";

    private String pushEnabled;

    private String enabled;

    @NotNull
    private String connectorAddress;

    @NotNull
    /**
     * Adress of the push webservice
     */
    private String publishAddress = "/domibusConnectorDeliveryWebservice";

    private Resource wsPolicy = new ClassPathResource("wsdl/backend.policy.xml");

    @NestedConfigurationProperty
    @NotNull
    private CxfTrustKeyStoreConfigurationProperties cxf;

//    @NestedConfigurationProperty
//    private KeyAndKeyStoreAndTrustStoreConfigurationProperties tls;


    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(String pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

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

    public Properties getWssProperties() {
        Properties p = mapCertAndStoreConfigPropertiesToMerlinProperties();
        LOGGER.debug("getSignatureProperties() are: [{}]", p);
        return p;
    }


    /**
     * Maps the own configured properties to the crypto Properties
     *  also see https://ws.apache.org/wss4j/config.html
     * @return the wss Properties
     */
    public Properties mapCertAndStoreConfigPropertiesToMerlinProperties() {
        Properties p = new Properties();
        p.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", "jks"); //TODO: also set type by config!
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.password", this.getCxf().getKeyStore().getPassword());
        LOGGER.debug("setting [org.apache.wss4j.crypto.merlin.keystore.file={}]", this.getCxf().getKeyStore().getPath());
        try {
            p.setProperty("org.apache.wss4j.crypto.merlin.keystore.file", this.getCxf().getKeyStore().getPathUrlAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error with property: [" + PREFIX + ".key.store.path]\n" +
                    "value is [" + this.getCxf().getKeyStore().getPath() + "]");
        }
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.alias", this.getCxf().getPrivateKey().getAlias());
        p.setProperty("org.apache.wss4j.crypto.merlin.keystore.private.password", this.getCxf().getPrivateKey().getPassword());
        p.setProperty("org.apache.wss4j.crypto.merlin.truststore.password", this.getCxf().getTrustStore().getPassword());
        try {
            LOGGER.debug("setting [org.apache.wss4j.crypto.merlin.truststore.file={}]", getCxf().getTrustStore().getPath());
            p.setProperty("org.apache.wss4j.crypto.merlin.truststore.file", getCxf().getTrustStore().getPathUrlAsString());
        } catch (Exception e) {
            LOGGER.info("Trust Store Property: [" + PREFIX + ".trust.store.path]" +
                            "\n cannot be processed. Using the configured key store [{}] as trust store",
                    p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file"));

            p.setProperty("org.apache.wss4j.crypto.merlin.truststore.file", p.getProperty("org.apache.wss4j.crypto.merlin.keystore.file"));
            p.setProperty("org.apache.wss4j.crypto.merlin.truststore.password", p.getProperty("org.apache.wss4j.crypto.merlin.keystore.password"));
        }
//        p.setProperty("org.apache.wss4j.crypto.merlin.load.cacerts", Boolean.toString(getCxf().isLoadCaCerts()));

        return p;
    }
}

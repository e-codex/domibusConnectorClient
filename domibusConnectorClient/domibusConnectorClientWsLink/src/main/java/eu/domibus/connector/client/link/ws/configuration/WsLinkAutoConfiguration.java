package eu.domibus.connector.client.link.ws.configuration;

import static eu.domibus.connector.client.link.ws.configuration.ConnectorLinkWSProperties.PUSH_ENABLED_PROPERTY_NAME;

import java.util.HashMap;
import java.util.Properties;

import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.spring.boot.autoconfigure.CxfAutoConfiguration;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import eu.domibus.connector.client.link.ws.impl.DomibusConnectorClientDeliveryWsImpl;
import eu.domibus.connector.client.link.ws.impl.DomibusConnectorClientWSLinkImpl;
import eu.domibus.connector.lib.spring.configuration.CxfTrustKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.link.common.DefaultWsCallbackHandler;
import eu.domibus.connector.link.common.WsPolicyLoader;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWSService;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWSService;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import eu.domibus.connector.ws.gateway.delivery.webservice.DomibusConnectorGatewayDeliveryWebService;


@Configuration
@ConditionalOnClass(SpringBus.class)
@EnableConfigurationProperties(value=ConnectorLinkWSProperties.class)
@AutoConfigureAfter(CxfAutoConfiguration.class)
@ConditionalOnProperty(prefix = ConnectorLinkWSProperties.PREFIX, name = "enabled", matchIfMissing = true)
public class WsLinkAutoConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(WsLinkAutoConfiguration.class);

    @Autowired
    private Bus cxfBus;

    @Autowired
    ConnectorLinkWSProperties connectorLinkWsProperties;

    @Configuration
    @ConditionalOnMissingBean({SpringBus.class})
    @ImportResource({"classpath:META-INF/cxf/cxf.xml"})
    protected static class SpringBusConfiguration {
        protected SpringBusConfiguration() {
        }
    }

    @Bean
    public WsPolicyLoader policyLoader() {
        WsPolicyLoader wsPolicyLoader = new WsPolicyLoader(connectorLinkWsProperties.getWsPolicy());
        return wsPolicyLoader;
    }

    @Bean
    public DomibusConnectorClientWSLinkImpl domibusConnectorClientServiceWsImpl() {
        return new DomibusConnectorClientWSLinkImpl();
    }

    @Bean
    public DomibusConnectorBackendWebService connectorWsClient() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorBackendWebService.class);
        jaxWsProxyFactoryBean.setBus(cxfBus);
        jaxWsProxyFactoryBean.setAddress(connectorLinkWsProperties.getConnectorAddress());
        jaxWsProxyFactoryBean.setServiceName(DomibusConnectorBackendWSService.SERVICE);
        jaxWsProxyFactoryBean.setEndpointName(DomibusConnectorBackendWSService.DomibusConnectorBackendWebService);
        jaxWsProxyFactoryBean.setWsdlURL(DomibusConnectorBackendWSService.WSDL_LOCATION.toString());
        jaxWsProxyFactoryBean.setBindingId(SOAPBinding.SOAP12HTTP_MTOM_BINDING);

        jaxWsProxyFactoryBean.getFeatures().add(policyLoader().loadPolicyFeature());

        if (jaxWsProxyFactoryBean.getProperties() == null) {
            jaxWsProxyFactoryBean.setProperties(new HashMap<>());
        }
        jaxWsProxyFactoryBean.getProperties().put("mtom-enabled", true);
        jaxWsProxyFactoryBean.getProperties().put("security.encryption.properties", connectorWsLinkEncryptionProperties());
        jaxWsProxyFactoryBean.getProperties().put("security.encryption.username", connectorLinkWsProperties.getCxf().getEncryptAlias());
        jaxWsProxyFactoryBean.getProperties().put("security.signature.properties", connectorWsLinkEncryptionProperties());
        jaxWsProxyFactoryBean.getProperties().put("security.callback-handler", new DefaultWsCallbackHandler());

        DomibusConnectorBackendWebService domibusConnectorBackendWebService = jaxWsProxyFactoryBean.create(DomibusConnectorBackendWebService.class);
        LOGGER.debug("Registered WS Client for [{}]", DomibusConnectorBackendWebService.class);

        return domibusConnectorBackendWebService;
    }


    @Bean
    @ConditionalOnProperty(prefix = ConnectorLinkWSProperties.PREFIX, value = PUSH_ENABLED_PROPERTY_NAME, matchIfMissing = false)
    public EndpointImpl domibusConnectorDeliveryServiceEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, domibusConnectorClientDeliveryWsImpl());
        endpoint.setAddress(connectorLinkWsProperties.getPublishAddress());
        endpoint.setWsdlLocation(DomibusConnectorBackendDeliveryWSService.WSDL_LOCATION.toString());
        endpoint.setServiceName(DomibusConnectorBackendDeliveryWSService.SERVICE);
        endpoint.setEndpointName(DomibusConnectorBackendDeliveryWSService.DomibusConnectorBackendDeliveryWebService);

        WSPolicyFeature wsPolicyFeature = policyLoader().loadPolicyFeature();
        endpoint.getFeatures().add(wsPolicyFeature);

        //endpoint.getFeatures().add(new MTOMFeature());

        endpoint.getProperties().put("mtom-enabled", true);
        endpoint.getProperties().put("security.encryption.properties", connectorWsLinkEncryptionProperties());
        endpoint.getProperties().put("security.signature.properties", connectorWsLinkEncryptionProperties());
        endpoint.getProperties().put("security.encryption.username", "useReqSigCert");

        endpoint.publish();
        LOGGER.debug("Published WebService {} under {}", DomibusConnectorGatewayDeliveryWebService.class, endpoint.getPublishedEndpointUrl());

        return endpoint;
    }

    @Bean
    public Properties connectorWsLinkEncryptionProperties() {
        Properties props = new Properties();

        CxfTrustKeyStoreConfigurationProperties cxf = connectorLinkWsProperties.getCxf();
        StoreConfigurationProperties cxfKeyStore = connectorLinkWsProperties.getCxf().getKeyStore();

        props.put("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        props.put("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
        props.put("org.apache.wss4j.crypto.merlin.keystore.file", cxfKeyStore.getPathUrlAsString());
        props.put("org.apache.wss4j.crypto.merlin.keystore.password", cxfKeyStore.getPassword());
        props.put("org.apache.wss4j.crypto.merlin.keystore.alias", cxf.getPrivateKey().getAlias());
        props.put("org.apache.wss4j.crypto.merlin.keystore.private.password", cxf.getPrivateKey().getPassword());

        props.put("org.apache.wss4j.crypto.merlin.truststore.type", "jks");
        props.put("org.apache.wss4j.crypto.merlin.truststore.file", cxf.getTrustStore().getPathUrlAsString());
        props.put("org.apache.wss4j.crypto.merlin.truststore.password", cxf.getTrustStore().getPassword());

        return props;
    }

    @Bean
    @ConditionalOnProperty(prefix = ConnectorLinkWSProperties.PREFIX, value = PUSH_ENABLED_PROPERTY_NAME, matchIfMissing = false)
    public DomibusConnectorClientDeliveryWsImpl domibusConnectorClientDeliveryWsImpl() {
        return new DomibusConnectorClientDeliveryWsImpl();
    }


}

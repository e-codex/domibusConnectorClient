package eu.domibus.connector.client.link.ws.impl;

import eu.domibus.connector.client.link.ws.ConnectorLinkWSProperties;
import eu.domibus.connector.link.common.WsPolicyLoader;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.support.JaxWsClientEndpointImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.Arrays;
import java.util.HashMap;

//@Configuration
public class ConnectorClientWebserviceClientConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(ConnectorClientWebserviceClientConfiguration.class);

//    @Autowired
    ConnectorLinkWSProperties connectorLinkWSProperties;


//    @Bean
    DomibusConnectorBackendWebService  connectorWsClient() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();

        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorBackendDeliveryWebService.class);

        Resource wsPolicy = connectorLinkWSProperties.getWsPolicy();
        WsPolicyLoader wsPolicyLoader = new WsPolicyLoader(wsPolicy);

        jaxWsProxyFactoryBean.setFeatures(Arrays.asList(new Feature[]{wsPolicyLoader.loadPolicyFeature()}));
        jaxWsProxyFactoryBean.setAddress(connectorLinkWSProperties.getConnectorAddress());
//        jaxWsProxyFactoryBean.setWsdlLocation();
//        jaxWsProxyFactoryBean.setWsdlURL(connectorLinkWSProperties.getConnectorAddress() + "?wsdl"); //maybe load own wsdl instead of remote one?

        HashMap<String, Object> props = new HashMap<>();
        props.put("security.encryption.properties", connectorLinkWSProperties);
        props.put("security.signature.properties", connectorLinkWSProperties.getWssProperties());
        props.put("security.encryption.username", connectorLinkWSProperties.getCxf().getEncryptAlias());
        props.put("security.signature.username", connectorLinkWSProperties.getCxf().getPrivateKey().getAlias());
        LOGGER.debug("#createWsClient: Configuring WsClient with following properties: [{}]", props);
        jaxWsProxyFactoryBean.setProperties(props);

        return (DomibusConnectorBackendWebService) jaxWsProxyFactoryBean.create();
    }

}

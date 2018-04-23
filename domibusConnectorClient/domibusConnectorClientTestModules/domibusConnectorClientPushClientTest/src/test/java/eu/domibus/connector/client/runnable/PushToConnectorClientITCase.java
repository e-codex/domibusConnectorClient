package eu.domibus.connector.client.runnable;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.testutil.TransitionCreator;
import eu.domibus.connector.ws.backend.delivery.webservice.DomibusConnectorBackendDeliveryWebService;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PushToConnectorClientITCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushToConnectorClientITCase.class);

    private static ApplicationContext CONNECTOR_CLIENT_APPLICATION_CONTEXT;

    @BeforeClass
    public static void beforeClass() {

        String[] args = new String[] {"-properties", "./target/test-classes/testclient.properties", "--server.port=0", "-web"};
        CONNECTOR_CLIENT_APPLICATION_CONTEXT = DomibusConnector.createApplicationContext(args);
    }


    @Before
    public void setUp() {

    }


    @Test
    @Ignore //TODO: repair security error
    public void testWebReachable() throws InterruptedException {

        String port = CONNECTOR_CLIENT_APPLICATION_CONTEXT.getEnvironment().getProperty("local.server.port");
        System.out.println("server port is " + port);

//        Thread.sleep(60000);

        DomibusConnectorBackendDeliveryWebService wsClient = createWsClient(port);

        DomibusConnectorMessageType msg = TransitionCreator.createEpoMessage();

        wsClient.deliverMessage(msg);


    }


    public DomibusConnectorBackendDeliveryWebService createWsClient(String serverPort) {

        String pushAddress = String.format("http://localhost:%s/services/connectorClientBackendDelivery", serverPort);
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(DomibusConnectorBackendDeliveryWebService.class);

        jaxWsProxyFactoryBean.setFeatures(Arrays.asList(new Feature[]{loadPolicyFeature()}));
        jaxWsProxyFactoryBean.setAddress(pushAddress);
        jaxWsProxyFactoryBean.setWsdlURL(pushAddress + "?wsdl"); //maybe load own wsdl instead of remote one?


        HashMap<String, Object> props = new HashMap<>();
        props.put("security.encryption.properties", "encrypt.properties");
        props.put("security.signature.properties", "encrypt.properties");
        props.put("security.encryption.username", "bob");
        props.put("security.signature.username", "connector");
        LOGGER.debug("#createWsClient: Configuring WsClient with following properties: [{}]", props);
        jaxWsProxyFactoryBean.setProperties(props);

        DomibusConnectorBackendDeliveryWebService webServiceClientEndpoint = (DomibusConnectorBackendDeliveryWebService) jaxWsProxyFactoryBean.create();

        return webServiceClientEndpoint;
    }


    public WSPolicyFeature loadPolicyFeature() {
        WSPolicyFeature policyFeature = new WSPolicyFeature();
        policyFeature.setEnabled(true);

        Resource res = new ClassPathResource("/wsdl/backend.policy.xml");

        InputStream is = null;
        try {
            is = res.getInputStream();
        } catch (IOException ioe) {
            throw new RuntimeException(String.format("ws policy [%s] cannot be read!", res), ioe);
        }
        if (is == null) {
            throw new RuntimeException(String.format("ws policy [%s] cannot be read! InputStream is null!",res));
        }
        List<Element> policyElements = new ArrayList<Element>();
        try {
            Element e = StaxUtils.read(is).getDocumentElement();
            LOGGER.debug("adding policy element [{}]", e);
            policyElements.add(e);
        } catch (XMLStreamException ex) {
            throw new RuntimeException("cannot parse policy: /wsdl/backend.policy.xml");
        }
        policyFeature.getPolicyElements().addAll(policyElements);
        LOGGER.debug("policyFeature: [{}]", policyFeature);

        return policyFeature;
    }

}
package eu.domibus.connector.client.runnable;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

public class PushToConnectorClientITCase {

    private static ApplicationContext CONNECTOR_CLIENT_APPLICATION_CONTEXT;

    @BeforeClass
    public static void beforeClass() {

        String[] args = new String[] {"-properties", "./target/test-classes/testclient.properties", "--server.port=0"};
        CONNECTOR_CLIENT_APPLICATION_CONTEXT = DomibusConnector.createApplicationContext(args);
    }


    @Before
    public void setUp() {

    }


    @Test
    public void testWebReachable() {

        String port = CONNECTOR_CLIENT_APPLICATION_CONTEXT.getEnvironment().getProperty("local.port");
        System.out.println("server port is " + port);

    }


}
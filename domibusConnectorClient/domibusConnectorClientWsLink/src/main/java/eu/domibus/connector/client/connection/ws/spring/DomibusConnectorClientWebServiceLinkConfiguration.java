
package eu.domibus.connector.client.connection.ws.spring;

import eu.domibus.connector.ws.backend.webservice.DomibusConnectorBackendWebService;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
//@Profile("wslink")
@ImportResource("classpath:/webservice/connectorclient.xml")
public class DomibusConnectorClientWebServiceLinkConfiguration {

       
}

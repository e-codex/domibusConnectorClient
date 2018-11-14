package eu.domibus.connector.client.configuration;

//import eu.domibus.connector.client.connection.ws.spring.DomibusConnectorClientWebServicePushLinkConfiguration;
import eu.domibus.connector.client.storage.dao.RepoPackage;
import eu.domibus.connector.client.storage.entity.EntityClassesPackage;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.ws.config.annotation.EnableWs;

@Configuration
@EnableJpaRepositories(basePackageClasses = RepoPackage.class)
@EntityScan(basePackageClasses = EntityClassesPackage.class)
//@EnableWs
//@Import(DomibusConnectorClientWebServicePushLinkConfiguration.class)
public class ConnectorClientRestWebConfiguration {


//
//    @Bean
//    public DispatcherServlet mvcDispatcherServlet(WebApplicationContext ctx) {
//        DispatcherServlet dispatcherServlet = new DispatcherServlet(ctx);
//        return dispatcherServlet;
//    }
//
//    @Bean
//    public ServletRegistrationBean mvcServletRegistrationBean(WebApplicationContext ctx) {
//        ServletRegistrationBean bean = new ServletRegistrationBean();
//        bean.setServlet(mvcDispatcherServlet(ctx));
//        ArrayList<String> list = new ArrayList<>();
//        list.add("/");
//        bean.setUrlMappings(list);
//        return bean;
//    }
//
//    @Bean
//    public MessageDispatcherServlet wsdispatcherServlet(WebApplicationContext ctx) {
//        MessageDispatcherServlet messageDispatcherServlet = new MessageDispatcherServlet(ctx);
//        return messageDispatcherServlet;
//    }
//
//    @Bean
//    public ServletRegistrationBean wsServletRegistrationBean(WebApplicationContext ctx) {
//        ServletRegistrationBean bean = new ServletRegistrationBean();
//        bean.setServlet(wsdispatcherServlet(ctx));
//        bean.addUrlMappings("/ws/*");
//        return bean;
//    }

}

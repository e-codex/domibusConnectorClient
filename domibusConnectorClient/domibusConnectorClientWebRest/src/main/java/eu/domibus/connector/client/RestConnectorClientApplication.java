package eu.domibus.connector.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = {"eu.domibus.connector.client"})
@EnableConfigurationProperties
@PropertySource("classpath:default-application.properties")
public class RestConnectorClientApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        builder.sources(RestConnectorClientApplication.class);
        builder.run(args);
    }

}

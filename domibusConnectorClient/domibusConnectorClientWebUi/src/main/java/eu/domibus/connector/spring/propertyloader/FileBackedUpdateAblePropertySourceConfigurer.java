package eu.domibus.connector.spring.propertyloader;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileBackedUpdateAblePropertySourceConfigurer  implements PropertySourceLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBackedUpdateAblePropertySourceConfigurer.class);

//    @Autowired
//    private ConfigurableEnvironment env;

//    @Value("connector.client.properties-file")
//    FileSystemResource fileSystemResource;

//    @Bean
//    @Lazy(false)
//    public FileBackedPropertySource fileBackedPropertySource() {
//        FileSystemResource fileSystemResource = env.getProperty("connector.client.properties-file", FileSystemResource.class);
//
//
////        FileSystemResource fileResource = new FileSystemResource();
//        FileBackedPropertySource fileBackedPropertySource =
//                new FileBackedPropertySource("myPropSource", fileSystemResource);
//
//
//        MutablePropertySources sources = env.getPropertySources();
//        sources.addFirst(fileBackedPropertySource);
//
//        return fileBackedPropertySource;
//
//    }


    @Override
    public PropertySource<?> locate(Environment environment) {
        FileSystemResource fileSystemResource = environment.getProperty("connector.client.properties-file", FileSystemResource.class);

//        FileSystemResource fileResource = new FileSystemResource();
        FileBackedPropertySource fileBackedPropertySource =
                new FileBackedPropertySource("myPropSource", fileSystemResource);
//        MutablePropertySources sources = environment.getPropertySources();
//        sources.addFirst(fileBackedPropertySource);

        LOGGER.info("Added file backed property source");
        return fileBackedPropertySource;
    }
}

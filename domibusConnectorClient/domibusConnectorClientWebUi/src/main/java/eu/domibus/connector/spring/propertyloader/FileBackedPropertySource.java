package eu.domibus.connector.spring.propertyloader;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FileBackedPropertySource extends EnumerablePropertySource implements UpdateAblePropertySource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBackedPropertySource.class);
    private final Resource resource;
    private Properties properties = new Properties();

    public FileBackedPropertySource(String name, FileSystemResource resource) {
        super(name);
        this.resource = resource;
        init();
    }

    private void init() {
        LOGGER.info("Reading properties from [{}]", resource.getDescription());
        try (InputStream is = resource.getInputStream()) {
            properties.load(is);
        } catch (IOException e) {
            LOGGER.warn("Error while reading properties from file!", e);
            //ignoring any errors when reading property file...
            // TODO: maybe make this configureable
        }
    }

    @Override
    public String[] getPropertyNames() {
        int size = properties.stringPropertyNames().size();
        return properties.stringPropertyNames().toArray(new String[size]);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public void updateProperty(String name, String property) {
        properties.setProperty(name, property);
        //TODO: update backend...
    }

}

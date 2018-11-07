package eu.domibus.connector.spring.propertyloader;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class FileBackedPropertySource extends EnumerablePropertySource implements UpdateAblePropertySource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileBackedPropertySource.class);
    private final FileSystemResource resource;
    private Properties properties = new Properties();

    @Autowired
    @Lazy
    public RefreshScope refreshScope;

    public FileBackedPropertySource(String name, FileSystemResource resource) {
        super(name);
        this.resource = resource;
        init();
    }

    private void init() {
        LOGGER.debug("Reading properties from [{}]", resource.getDescription());
        try (InputStream is = resource.getInputStream()) {
            properties.load(is);
        } catch (IOException e) {
            LOGGER.warn("Error while reading properties from file!", e);
            //ignoring any errors when reading property file...
            // TODO: maybe make this configureable

        }
    }

    private void saveProperties() {
        LOGGER.debug("Writing properties to [{}]", resource.getDescription());
        try (java.io.OutputStream os = resource.getOutputStream()) {
            properties.store(os, "");
        } catch (IOException e) {
            LOGGER.error("Cannot save properties, ioexception: ", e);
        }
    }

    @Override
    public String[] getPropertyNames() {
        int size = properties.stringPropertyNames().size();
        return properties.stringPropertyNames().toArray(new String[size]);
    }

    public Map<String, String> getProperties() {
        //return (Map<String, String>) properties.entrySet();
        Map<String , String> map = new HashMap<>();
        properties.entrySet().forEach((Map.Entry<Object, Object> entry)  -> {
            LOGGER.debug("converting {} {}={}", entry, entry.getKey().toString(), entry.getValue().toString());
            map.put(entry.getKey().toString(), entry.getValue().toString());
        });
        return map;
    }

    @Override
    public String getProperty(String name) {
        return (String) properties.get(name);
    }

    @Override
    public void updateProperty(String name, String property) {
        properties.setProperty(name, property);
        //TODO: update backend...
        this.saveProperties();

    }

}

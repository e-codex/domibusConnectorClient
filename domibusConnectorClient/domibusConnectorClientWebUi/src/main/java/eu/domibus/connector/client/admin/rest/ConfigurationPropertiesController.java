package eu.domibus.connector.client.admin.rest;

import eu.domibus.connector.client.admin.rest.dto.ConfigPropertyDTO;
import eu.domibus.connector.spring.propertyloader.FileBackedPropertySource;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/configs")
public class ConfigurationPropertiesController {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertiesController.class);

    @Autowired
    StandardEnvironment env;

    @Autowired
    FileBackedPropertySource fileBackedPropertySource;

    @Autowired
    @Lazy
    public org.springframework.cloud.context.scope.refresh.RefreshScope refreshScope;

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public ResponseEntity getConfigs() {
        Map<String, String> properties = fileBackedPropertySource.getProperties();
        List<ConfigPropertyDTO> collect = properties.entrySet()
                .stream()
                .map(entry -> new ConfigPropertyDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(collect);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/u/{propertyId}")
    public ResponseEntity readProperty(@PathVariable String propertyId) {
        LOGGER.debug("fetch property with ID: [{}]", propertyId);
        String value = fileBackedPropertySource.getProperty(propertyId);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        ConfigPropertyDTO configProperty = new ConfigPropertyDTO();
        configProperty.setName(propertyId);
        configProperty.setValue(value);
        return ResponseEntity.ok(configProperty);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ConfigPropertyDTO writeProperty(@RequestBody ConfigPropertyDTO configProperty) {
        String name = configProperty.getName();
        String value = configProperty.getValue();
        LOGGER.debug("name is [{}], value is [{}]");
        fileBackedPropertySource.updateProperty(name, value);
        return configProperty;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/refresh")
    public ResponseEntity refresh() {
        this.refreshScope.refreshAll();
        return ResponseEntity.ok().build();
    }


}

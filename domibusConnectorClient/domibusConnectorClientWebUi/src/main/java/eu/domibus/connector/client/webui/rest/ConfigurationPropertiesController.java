package eu.domibus.connector.client.webui.rest;

import eu.domibus.connector.client.webui.dto.ConfigProperty;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configs")
public class ConfigurationPropertiesController {

    private static final Logger LOGGER = LogManager.getLogger(ConfigurationPropertiesController.class);

    @Autowired
    StandardEnvironment env;

    @RequestMapping(method = RequestMethod.GET, value = "/{propertyId}")
    public String readProperty(@PathVariable String propertyId) {
        LOGGER.debug("fetch property with ID: [{}]", propertyId);
        return "Property is: " + env.getProperty(propertyId);
    }

//    @RequestMapping(method = RequestMethod.POST)
//    public ResponseEntity<?> add(@PathVariable String propertyId, @RequestBody ConfigProperty configProperty){
//        //TODO: update config
//        return ResponseEntity.created("/").build();
//    }

    //TODO: write update method!


}

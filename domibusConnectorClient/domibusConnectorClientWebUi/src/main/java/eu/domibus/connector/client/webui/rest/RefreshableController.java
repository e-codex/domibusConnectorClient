package eu.domibus.connector.client.webui.rest;

import eu.domibus.connector.spring.propertyloader.FileBackedPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/myfresh")
public class RefreshableController {

    @Autowired
    private FileBackedPropertySource fileBackedPropertySource;

    @Value("${myvalue}")
    private String myValue;

    @RequestMapping(method = RequestMethod.GET)
    public String getValue() {
        return myValue;
    }

    //TODO: write put method...
    @RequestMapping(method = RequestMethod.POST)
    public String setValue(@RequestParam("value") String value) {
        fileBackedPropertySource.updateProperty("myvalue", value);
        //TODO: propage environment change!
        return value;
    }

}

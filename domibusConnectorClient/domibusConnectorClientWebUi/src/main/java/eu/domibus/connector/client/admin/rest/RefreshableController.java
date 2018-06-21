package eu.domibus.connector.client.admin.rest;

import eu.domibus.connector.spring.propertyloader.FileBackedPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
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


    @Autowired
    @Lazy
    public org.springframework.cloud.context.scope.refresh.RefreshScope refreshScope;

    @Value("${myvalue}")
    private String myValue;

    @RequestMapping(method = RequestMethod.GET)
    public String getValue() {
        return myValue;
    }


    @RequestMapping(method = RequestMethod.POST)
    public String setValue(@RequestParam("value") String value) {
        fileBackedPropertySource.updateProperty("myvalue", value);
        //TODO: propage environment change!

        refreshScope.refreshAll();

        return value;
    }

}

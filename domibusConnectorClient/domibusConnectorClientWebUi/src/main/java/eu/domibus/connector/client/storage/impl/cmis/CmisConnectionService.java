package eu.domibus.connector.client.storage.impl.cmis;


import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CmisConnectionService {


    public Session createSession() {
// default factory implementation
        SessionFactory factory = SessionFactoryImpl.newInstance();


        Map<String, String> parameters = new HashMap<String, String>();

// user credentials
        parameters.put(SessionParameter.USER, "admin");
        parameters.put(SessionParameter.PASSWORD, "admin");

// connection settings
        parameters.put(SessionParameter.ATOMPUB_URL, "http://localhost:6280/service/cmis");
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameters.put(SessionParameter.REPOSITORY_ID, "5");

// create session
        Session session = factory.createSession(parameters);
        return session;
    }

}

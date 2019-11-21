package eu.domibus.connector.client.link.ws.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class PushWebserviceEnabledCondition implements Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushWebserviceEnabledCondition.class);

    public static final String PUSH_ENABLED_PROPERTY = "connector-client.connector-link.cxf.push-enabled";

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        String p = env.getProperty("PUSH_ENABLED_PROPERTY");
        boolean condition = p != null && "true".equalsIgnoreCase(p);

        LOGGER.debug("PushWebserviceEnabledCondition condition evaluates to: [{}]", condition);
        return condition;
    }


}

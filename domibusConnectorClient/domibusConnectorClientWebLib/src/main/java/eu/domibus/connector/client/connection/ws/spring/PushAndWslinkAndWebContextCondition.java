package eu.domibus.connector.client.connection.ws.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.context.WebApplicationContext;

/**
 * This condition is only true if
 *  the push profile AND the wslink profile are active
 *  AND
 *  the application is a webContext
 */
public class PushAndWslinkAndWebContextCondition implements Condition {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushAndWslinkAndWebContextCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        //check if the profiles push and wslink are active
        Environment env = context.getEnvironment();
        boolean isPushAndWebserviceConfigured = env.acceptsProfiles("push") && env.acceptsProfiles("wslink");
        LOGGER.debug("profile push and profile wslink are active [{}]", isPushAndWebserviceConfigured);

        //webcontext active?
        boolean isWebContext = WebApplicationContext.class.isAssignableFrom(context.getClass());
        LOGGER.debug("context is a webApplicationContext [{}]", isWebContext);

        return isPushAndWebserviceConfigured && isWebContext;
    }

}

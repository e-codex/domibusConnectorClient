package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.lib.spring.DomibusConnectorDuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = SubmitMessagesToConnectorJobConfigurationProperties.PREFIX)
public class SubmitMessagesToConnectorJobConfigurationProperties {

    public static final String PREFIX = "connector-client.timer.check.outgoing-messages";

    private DomibusConnectorDuration repeatInterval;

    private boolean enabled;

    public DomibusConnectorDuration getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(DomibusConnectorDuration repeatInterval) {
        this.repeatInterval = repeatInterval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}

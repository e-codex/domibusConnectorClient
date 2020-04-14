package eu.domibus.connector.client.scheduler.job;

import eu.domibus.connector.lib.spring.DomibusConnectorDuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = GetMessagesFromConnectorJobConfigurationProperties.PREFIX)
@Validated
@Valid
public class GetMessagesFromConnectorJobConfigurationProperties {

    public static final String PREFIX = "connector-client.scheduler.get-messages-from-connector-job";

    @NestedConfigurationProperty
    @NotNull
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

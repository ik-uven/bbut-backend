package org.ikuven.bbut.tracking.settings;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Data
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "backend-settings")
public class BackendSettingsProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackendSettingsProperties.class);

    private Laps laps;
    private Teams teams;

    @PostConstruct
    private void logSettings() {
        LOGGER.info("Loaded {}", this);
    }

    @Data
    public static class Laps {
        private long registrationGracePeriod;
    }

    @Data
    public static class Teams {
        private long minSize;
    }
}

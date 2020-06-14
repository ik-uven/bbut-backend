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
@ConfigurationProperties(prefix = "ui-settings")
public class UiSettingsProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(UiSettingsProperties.class);

    private ResultView resultView;

    @PostConstruct
    private void logSettings() {
        LOGGER.info("Loaded {}", this);
    }

    @Data
    public static class ResultView {
        private int numberOfColumns;
        private boolean showTeamsColumn;
    }
}

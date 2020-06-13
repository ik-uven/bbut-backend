package org.ikuven.bbut.tracking.settings;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "ui-settings")
public class UiSettingsProperties {

    private ResultView resultView;

    @Data
    public static class ResultView {
        private int numberOfColumns;
    }
}

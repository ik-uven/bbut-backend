package org.ikuven.bbut.tracking.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class UiSettingsDto {

    @JsonProperty("resultView")
    private ResultViewDto resultViewDto;

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class ResultViewDto {
        private int numberOfColumns;
    }
}

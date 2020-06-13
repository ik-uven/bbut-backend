package org.ikuven.bbut.tracking.web;

import org.ikuven.bbut.tracking.settings.UiSettingsProperties;
import org.ikuven.bbut.tracking.web.UiSettingsDto.ResultViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class UiSettingsController {

    private final UiSettingsProperties uiSettingsProperties;

    @Autowired
    public UiSettingsController(UiSettingsProperties uiSettingsProperties) {
        this.uiSettingsProperties = uiSettingsProperties;
    }

    @GetMapping(path = "/settings")
    ResponseEntity<UiSettingsDto> getUiSettingsProperties() {
        return ResponseEntity.ok(toUiSettingsDto(uiSettingsProperties));
    }

    private UiSettingsDto toUiSettingsDto(UiSettingsProperties uiSettingsProperties) {
        return UiSettingsDto.of(
                ResultViewDto.of(uiSettingsProperties.getResultView().getNumberOfColumns())
        );
    }
}

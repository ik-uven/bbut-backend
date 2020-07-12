package org.ikuven.bbut.racesimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RaceSimulatorApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RaceSimulatorApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}

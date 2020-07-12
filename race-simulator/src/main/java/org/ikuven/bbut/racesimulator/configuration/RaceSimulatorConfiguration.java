package org.ikuven.bbut.racesimulator.configuration;

import org.ikuven.bbut.racesimulator.RaceSimulator;
import org.ikuven.bbut.racesimulator.client.RaceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class RaceSimulatorConfiguration {

    @Bean
    RaceSimulator createSimulator(@Autowired RaceClient raceClient) {
        return new RaceSimulator(raceClient);
    }
}

package org.ikuven.bbut.tracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ResultTrackingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResultTrackingApplication.class, args);
    }

}

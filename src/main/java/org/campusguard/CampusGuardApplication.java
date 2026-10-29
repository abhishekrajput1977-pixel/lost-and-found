package org.campusguard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CampusGuardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusGuardApplication.class, args);
    }
}

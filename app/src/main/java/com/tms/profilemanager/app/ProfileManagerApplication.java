package com.tms.profilemanager.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProfileManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfileManagerApplication.class, args);
    }

}

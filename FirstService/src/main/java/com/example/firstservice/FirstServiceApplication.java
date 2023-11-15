package com.example.firstservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=9003"
@SpringBootApplication
public class FirstServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstServiceApplication.class, args);
    }

}

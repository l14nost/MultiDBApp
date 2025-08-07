package org.example.multidbapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MultiDbAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiDbAppApplication.class, args);
    }

}

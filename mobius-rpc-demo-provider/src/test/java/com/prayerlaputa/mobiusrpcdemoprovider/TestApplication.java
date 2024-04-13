package com.prayerlaputa.mobiusrpcdemoprovider;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author:
 * @description:
 * @date: 2024/4/13 20:12
 * @version: 1.0
 */
@SpringBootApplication
public class TestApplication {

    @Bean
    ApplicationRunner providerRun() {
        return x -> {
            System.out.println(" ahahahhaha ...");
        };
    }
}

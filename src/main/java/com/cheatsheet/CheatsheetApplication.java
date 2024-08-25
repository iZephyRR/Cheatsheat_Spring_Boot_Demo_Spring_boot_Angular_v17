package com.cheatsheet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableAsync(proxyTargetClass = true)
public class CheatsheetApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheatsheetApplication.class, args);
    }

}

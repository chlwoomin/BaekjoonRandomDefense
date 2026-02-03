package com.baekjoon.randomdefense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BaekjoonRandomDefenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaekjoonRandomDefenseApplication.class, args);
    }
}

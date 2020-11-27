package com.atguigu.gmall.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.atguigu.gmall.test.controller")
public class SeriveTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeriveTestApplication.class,args);
    }
}

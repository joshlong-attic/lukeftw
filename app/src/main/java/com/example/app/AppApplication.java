package com.example.app;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Bean
    ApplicationRunner myRunner(@Value("${server.port}") int port) {
        return args -> System.out.println("port is " + port);
    }
}

@RestController
class SlowEndpoint implements DisposableBean {

    @GetMapping("/slow")
    String slow() throws Exception {
        System.out.println("before");
        Thread.sleep(10_000);
        System.out.println("after");
        return "slow response";
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("preDestroy");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("destroy!");
    }
}
package com.exemplo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class MeuProjetoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeuProjetoApplication.class, args);
    }

    @GetMapping("/")
    public String hello() {
        return "\n Hello World! \n";
    }
}

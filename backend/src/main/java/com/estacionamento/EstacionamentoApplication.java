
package com.estacionamento; 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class EstacionamentoApplication {

    private EstacionamentoApplication() {
        
    }

    public static void main(String[] args) {
        SpringApplication.run(EstacionamentoApplication.class, args);
    }

}
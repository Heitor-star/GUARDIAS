package com.hegaf.guardians;

// Importa classe responsável por iniciar a aplicação Spring Boot
import org.springframework.boot.SpringApplication;

// Importa anotação principal que configura o Spring automaticamente
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 🔥 Anotação mais importante do Spring Boot
@SpringBootApplication
public class LoginApplication {

    // 🚀 Método principal (ponto de entrada da aplicação Java)
    public static void main(String[] args) {

        // 🔥 Inicializa toda a aplicação Spring Boot
        SpringApplication.run(LoginApplication.class, args);
    }
}
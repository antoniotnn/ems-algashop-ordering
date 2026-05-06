package com.algaworks.algashop.ordering.utils;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration
public class TestcontainerPostgreSQLConfig {

    private static PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer<>("postgres:17-alpine");

    static {
        /*
            O Docker v29 aumentou a API mínima aceita pelo daemon para v1.44.
            Quando o client Java (via docker-java/Testcontainers) acaba usando/caindo para uma API mais antiga (ex.: 1.32),
            o daemon responde com “client version … is too old… minimum supported … 1.44” e o Testcontainers acaba resumindo tudo como
            “Could not find a valid Docker environment”.
            (https://github.com/testcontainers/testcontainers-java/issues/11212).

            Abaixo, o setProperty contorna o problema.
         */
        System.setProperty("api.version", "1.44");
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer postgreSQLContainer() {
        return postgreSQLContainer;
    }
}

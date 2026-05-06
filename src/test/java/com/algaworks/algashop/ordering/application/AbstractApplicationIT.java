package com.algaworks.algashop.ordering.application;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class AbstractApplicationIT {

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

    @Container
    @ServiceConnection
    protected static PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("ordering_test");
}

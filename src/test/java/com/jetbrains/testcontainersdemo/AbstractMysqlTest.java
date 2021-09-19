package com.jetbrains.testcontainersdemo;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public class AbstractMysqlTest {

    protected static MySQLContainer container = (MySQLContainer) new MySQLContainer("mysql:latest")
            .withReuse(true);

    /*
    cat ~/.testcontainers.properties
    docker.client.strategy=org.testcontainers.dockerclient.UnixSocketClientProviderStrategy
    testcontainers.reuse.enable=true
    */

    @BeforeAll
    public static void setup() {
        container.start();
    }

    @DynamicPropertySource
    public static void overrideDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}

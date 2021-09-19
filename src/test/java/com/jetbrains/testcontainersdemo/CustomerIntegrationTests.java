package com.jetbrains.testcontainersdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public class CustomerIntegrationTests {

    @Autowired
    private CustomerDao customerDao;

    @Container
    private static MySQLContainer container = new MySQLContainer("mysql:latest");

    @DynamicPropertySource
    public static void overrideDatabaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void when_using_a_clean_db_this_should_be_empty() {
        List<Customer> customers = customerDao.findAll();
        assertThat(customers).hasSize(2);
    }

    @Test
    void when_using_a_clean_db_this_could_be_helpful() throws IOException, InterruptedException {
        container.withClasspathResourceMapping("application.properties", "/tmp/application.properties", BindMode.READ_ONLY);
        container.withFileSystemBind("application.properties", "/tmp/application.properties");
        container.execInContainer("ls", "-la");
        container.getLogs(OutputFrame.OutputType.STDOUT);
        //container.withLogConsumer(new Slf4jLogConsumer());
        Integer mappedPort = container.getMappedPort(3306);

        List<Customer> customers = customerDao.findAll();
        assertThat(customers).hasSize(2);
    }
}

package com.jetbrains.testcontainersdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.output.OutputFrame;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerIntegrationTests extends AbstractMysqlTest {

    @Autowired
    private CustomerDao customerDao;

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

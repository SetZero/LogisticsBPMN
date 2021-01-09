package rocks.magical.camunda.database;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RepositoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rocks.magical.camunda.database.utils.PackageUtil;

@Configuration
public class DBConfig {
    @Bean
    public PackageUtil packageUtil() {
        return new PackageUtil();
    }
}

package bg.angebotpilot.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class DeploymentConfigurationTest {

    private final YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    @Test
    void productionProfileRequiresExternalDatabaseAndOwnerSecrets() throws IOException {
        PropertySource<?> production = loader
                .load("production", new ClassPathResource("application-prod.yaml"))
                .get(0);

        assertThat(production.getProperty("spring.datasource.url")).isEqualTo("${JDBC_DATABASE_URL}");
        assertThat(production.getProperty("app.security.username")).isEqualTo("${APP_ADMIN_USERNAME}");
        assertThat(production.getProperty("app.security.password")).isEqualTo("${APP_ADMIN_PASSWORD}");
        assertThat(production.getProperty("server.servlet.session.cookie.secure")).isEqualTo(true);
        assertThat(production.getProperty("management.endpoints.web.exposure.include")).isEqualTo("health");
    }

    @Test
    void dockerComposeUsesPostgresAndEnvironmentSecrets() throws IOException {
        PropertySource<?> compose = loader
                .load("compose", new FileSystemResource("compose.yaml"))
                .get(0);

        assertThat(compose.getProperty("services.database.image")).isEqualTo("postgres:17-alpine");
        assertThat(compose.getProperty("services.app.environment.JDBC_DATABASE_URL"))
                .isEqualTo("jdbc:postgresql://database:5432/angebotpilot");
        assertThat(compose.getProperty("services.app.environment.APP_ADMIN_PASSWORD"))
                .isEqualTo("${APP_ADMIN_PASSWORD:?Set APP_ADMIN_PASSWORD in .env}");
    }
}

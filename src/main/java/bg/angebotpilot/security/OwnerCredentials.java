package bg.angebotpilot.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security")
public record OwnerCredentials(
        @NotBlank String username,
        @NotBlank @Size(min = 12) String password
) {
}

package org.example.datajpa.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
@ToString
public class KeycloakAdminProps {
    private String serverUrl;
    private String clientId;
    private String clientSecret;
    private String realm;
    private String targetRealm;
}

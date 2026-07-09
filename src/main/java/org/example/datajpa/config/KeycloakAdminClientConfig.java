package org.example.datajpa.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class KeycloakAdminClientConfig {
    private final KeycloakAdminProps props;

    @Bean
    public Keycloak keycloakAdminConfig(){
        return KeycloakBuilder.builder()
                .serverUrl(props.getServerUrl())
                .realm(props.getTargetRealm())
                .clientId(props.getClientId())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientSecret(props.getClientSecret())
                .build();
    }
}

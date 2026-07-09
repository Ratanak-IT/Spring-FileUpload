package org.example.datajpa.features.userprofile;

import lombok.RequiredArgsConstructor;
import org.example.datajpa.config.KeycloakAdminProps;
import org.example.datajpa.config.SecurityUtils;
import org.example.datajpa.features.userprofile.dto.response.UserProfileResponse;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final Keycloak keycloak;
    private final KeycloakAdminProps props;
    private final UserProfileMapper userProfileMapper;

    @Override
    public UserProfileResponse me() {
        String userId = SecurityUtils.extractUserId();
        UserRepresentation userRepresentation = keycloak.realm(props.getTargetRealm())
                .users()
                .get(userId)
                .toRepresentation();

        //profile from keycloak
        //profile from db
        return userProfileMapper.toUserProfileResponse(userRepresentation);
    }
}

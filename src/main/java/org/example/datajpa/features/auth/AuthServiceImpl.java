package org.example.datajpa.features.auth;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.datajpa.config.KeycloakAdminProps;
import org.example.datajpa.features.auth.dto.RegisterRequest;
import org.example.datajpa.features.auth.dto.RegisterResponse;
import org.example.datajpa.features.auth.dto.RoleEnum;
import org.example.datajpa.features.userprofile.UserProfile;
import org.example.datajpa.features.userprofile.UserProfileRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final Keycloak keycloak;
    private final KeycloakAdminProps props;
    private final AuthMapper authMapper;
    private final UserProfileRepository userProfileRepository;


    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        //check request validation
        if (!registerRequest.password().equals(registerRequest.comfirmPassword())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password and comfirm password not match");
        }
        //create keycloak user
        UsersResource usersResource = keycloak.realm(props.getTargetRealm()).users();

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(registerRequest.username());
        userRepresentation.setEmail(registerRequest.email());
        userRepresentation.setFirstName(registerRequest.firstName());
        userRepresentation.setLastName(registerRequest.lastName());
        Map<String, List<String>> phone = Map.of("phone", List.of(registerRequest.phone()));
        userRepresentation.setAttributes(phone);


        //set Credential
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(registerRequest.password());
        userRepresentation.setCredentials(List.of(credential));
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(false);


        try(Response response = usersResource.create(userRepresentation)){
            log.info("response status: {}", response.getStatus());
            if (response.getStatus() == HttpStatus.CREATED.value()){
                UserRepresentation createdUser =keycloak.realm(props.getTargetRealm())
                        .users().search(userRepresentation.getUsername()).getFirst();

               log.info("created user: {}", createdUser.getId());

                UserResource userResource = keycloak.realm(props.getTargetRealm()).users().get(createdUser.getId());
                userResource.sendVerifyEmail();
                RolesResource roleResource = keycloak.realm(props.getTargetRealm()).roles();
                RoleRepresentation roleUser = roleResource.get(RoleEnum.USER.name()).toRepresentation();
                RoleRepresentation roleStudent = roleResource.get(RoleEnum.STUDENT.name()).toRepresentation();
                log.info("role user: {}", roleUser);
                log.info("role student: {}", roleStudent);
                userResource.roles().realmLevel().add(List.of(roleUser,roleStudent));

                //save user profile to db
                UserProfile userProfile = new UserProfile();
                userProfile.setUserId(createdUser.getId());
                userProfileRepository.save(userProfile);
               return authMapper.toRegisterResponse(createdUser);

            }else if(response.getStatus() == HttpStatus.CONFLICT.value()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
            }else{
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user");
            }
        }
    }
}

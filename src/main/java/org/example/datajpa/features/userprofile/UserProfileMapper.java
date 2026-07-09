package org.example.datajpa.features.userprofile;

import org.example.datajpa.features.userprofile.dto.response.UserProfileResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class UserProfileMapper {

    public UserProfileResponse toUserProfileResponse(UserRepresentation userRepresentation){
        return UserProfileResponse.builder()
                .userId(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .email(userRepresentation.getEmail())
                .phone(userRepresentation.getAttributes().get("phone").getFirst())
                .build();
    }

}

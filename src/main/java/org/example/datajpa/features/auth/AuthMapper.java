package org.example.datajpa.features.auth;

import org.example.datajpa.features.auth.dto.RegisterResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class AuthMapper {
    public RegisterResponse toRegisterResponse(UserRepresentation userRepresentation){
        return RegisterResponse.builder()
                .userId(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .phone(userRepresentation.firstAttribute("phone"))
                .build();
    }
}

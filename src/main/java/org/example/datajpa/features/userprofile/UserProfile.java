package org.example.datajpa.features.userprofile;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profile")
@Setter
@Getter
@NoArgsConstructor
public class UserProfile {
    @Id
    private String userId; // from keycloak
    private String biography;
    private String gender;
    private String address;
    private String phone;
    private String profilePicture;

}

package mainpackage.secutiry.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileKeycloakDto {

    private String keycloakId;
    private String name;
    private String email;
    private String password;

}

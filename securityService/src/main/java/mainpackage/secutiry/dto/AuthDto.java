package mainpackage.secutiry.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {
    private String login;
    private String password;
}

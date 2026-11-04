package mainpackage.secutiry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//регистрация
@Data
public class ProfileCredentialsDto {
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotBlank
    private String password;
}

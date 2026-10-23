package mainpackage.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import mainpackage.domain.Task;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
    @NotBlank
    private String name;
    @Email
    private String email;
    private List<Task> tasks;

}

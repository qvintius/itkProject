package mainpackage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mainpackage.domain.Person;
import mainpackage.domain.StatusTask;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    private String name;

//    private Person person;


    private String description;

    private StatusTask status;

}

package mainpackage.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import mainpackage.domain.StatusTask;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {

    @NotBlank
    private String name;
    private String description;
    private StatusTask status;

}

package mainpackage.secutiry.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonSaveEventFlow {
    private Long id;
    private LocalDate date;
}

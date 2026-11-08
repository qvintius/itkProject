package mainpackage.secutiry.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonSaveEventStream {
    private Long id;
    private String name;
}

package mainpackage.eventAndflow;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSaveEventFlow {
    private Long id;
    private LocalDate date;

}

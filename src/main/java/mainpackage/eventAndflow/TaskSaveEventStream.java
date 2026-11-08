package mainpackage.eventAndflow;

import lombok.*;
import mainpackage.domain.StatusTask;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskSaveEventStream {
    private String id;
    private String name;
    private StatusTask status;
}

package mainpackage.eventAndflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mainpackage.domain.Person;
import mainpackage.domain.Task;
import mainpackage.secutiry.domain.Profile;
import mainpackage.secutiry.dto.PersonSaveEventFlow;
import mainpackage.secutiry.dto.PersonSaveEventStream;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class EventListener {

    public final KafkaTemplate<String, Object> kafkaTemplate;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;

    @Value("${topic.task-stream}")
    private String TASK_STREAM;
    @Value("${topic.task-flow}")
    private String TASK_FLOW;
    @Value("${topic.person-stream}")
    private String PERSON_STREAM;
    @Value("${topic.person-flow}")
    private String PERSON_FLOW;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void taskSaveStream(Task task) {
        TaskSaveEventStream eventStream = mapper.map(task, TaskSaveEventStream.class);
        TaskSaveEventFlow eventFlow = new TaskSaveEventFlow(task.getId(), LocalDate.now());
        kafkaTemplate.send(TASK_STREAM, eventStream);
        kafkaTemplate.send(TASK_FLOW, eventFlow);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void taskSaveStream(PersonSaveEventStream eventStream) {

        kafkaTemplate.send(PERSON_STREAM, eventStream);
    }

    @SneakyThrows
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void taskSaveFlow(PersonSaveEventFlow eventFlow) {
        String eventString = objectMapper.writeValueAsString(eventFlow);
        kafkaTemplate.send(PERSON_FLOW, eventFlow);
    }


}

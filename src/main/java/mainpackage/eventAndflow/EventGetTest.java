package mainpackage.eventAndflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainpackage.secutiry.dto.PersonSaveEventFlow;
import mainpackage.secutiry.dto.PersonSaveEventStream;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


//проверка получения событий сохранения пользователя и задачи
@Slf4j
@Component
@RequiredArgsConstructor
public class EventGetTest {

    private final ObjectMapper objectMapper;


    @KafkaListener(topics = "task-stream", groupId = "task")
    public void taskSaveStream(ConsumerRecord<String, ?> record) {
        TaskSaveEventStream event = objectMapper.convertValue(record.value(), TaskSaveEventStream.class);
        log.info("{}", event);
    }

    @KafkaListener(topics = "task-flow", groupId = "task")
    public void taskSaveFlow(ConsumerRecord<String, ?> record) {
        TaskSaveEventFlow event= objectMapper.convertValue(record.value(), TaskSaveEventFlow.class);
        log.info("{}", event);
    }

    @KafkaListener(topics = "person-stream", groupId = "person")
    public void personSaveStream(ConsumerRecord<String, ?> record) {
        PersonSaveEventStream event = objectMapper.convertValue(record.value(), PersonSaveEventStream.class);
        log.info("{}", event);
    }

    @KafkaListener(topics = "person-flow", groupId = "person")
    public void personSaveFlow(ConsumerRecord<String, String> record) {
        PersonSaveEventFlow event = objectMapper.convertValue(record.value(), PersonSaveEventFlow.class);
        log.info("{}", event);
    }


}

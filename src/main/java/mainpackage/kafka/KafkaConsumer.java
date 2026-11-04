package mainpackage.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import mainpackage.domain.Task;
import mainpackage.dto.IdsDto;
import mainpackage.dto.TaskDto;
import mainpackage.service.TaskService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final TaskService taskService;

    @Autowired
    public KafkaConsumer(ObjectMapper objectMapper, ModelMapper modelMapper, TaskService taskService) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.taskService = taskService;
    }

    //получение из кафки и передача в сервис
    @KafkaListener(topics = "tasks", groupId = "group1")
    public void receiveTask(ConsumerRecord<String, ?> record) {
        TaskDto taskDto = objectMapper.convertValue(record.value(), TaskDto.class);
        Task task = modelMapper.map(taskDto, Task.class);
        taskService.save(task);
    }

    @KafkaListener(topics = "appointExecutor", groupId = "group1")
    public void receiveIdsDto(ConsumerRecord<String, ?> record) {
        IdsDto idsDto = objectMapper.convertValue(record.value(), IdsDto.class);
        taskService.appointExecutor(idsDto);
    }

}

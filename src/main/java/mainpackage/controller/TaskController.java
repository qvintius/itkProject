package mainpackage.controller;

import jakarta.validation.Valid;
import mainpackage.domain.StatusTask;
import mainpackage.domain.Task;
import mainpackage.dto.Filter;
import mainpackage.dto.IdsDto;
import mainpackage.dto.TaskDto;
import mainpackage.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Value("${topic.tasks}")
    private String topicTasks;

    @Value("${topic.appointExecutor}")
    private String topicAppointExecutor;

    private final TaskService taskService;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @Autowired
    public TaskController(TaskService taskService, KafkaTemplate<String, Object> kafkaTemplate) {
        this.taskService = taskService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAll(@RequestBody(required = false) Filter filter) {
        Integer pageNumber = filter != null && filter.pageNumber() != null ? filter.pageNumber() : 0;
        Integer pageSize = filter != null && filter.pageSize() != null ? filter.pageSize() : 3;
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);

        List<Task> tasks = taskService.findAll(pageable);
        return ResponseEntity.ok().body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable("id") Long id) {
        Task task = taskService.findById(id);
        return ResponseEntity.ok().body(task);
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody @Valid TaskDto taskDto) {
        kafkaTemplate.send(topicTasks, taskDto);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> changeStatus(@PathVariable("id") Long id, @RequestParam StatusTask status) {
        taskService.changeStatus(id, status);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/{id}")
    public void appointExecutor(@PathVariable("id") Long idTask, @RequestParam("idPerson") Long idPerson) {
//        taskService.appointExecutor(idTask, idPerson);
        IdsDto idsDto = new IdsDto(idTask, idPerson);
        kafkaTemplate.send(topicAppointExecutor, idsDto);
    }


}

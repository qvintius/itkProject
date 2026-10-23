package mainpackage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import mainpackage.domain.Person;
import mainpackage.domain.StatusTask;
import mainpackage.domain.Task;
import mainpackage.dto.IdsDto;
import mainpackage.dto.TaskDto;
import mainpackage.repo.PersonRepo;
import mainpackage.repo.TaskRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class TaskService {

    private final TaskRepo taskRepo;
    private final PersonRepo personRepo;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${cache.prefix.tasks}")
    private String CACHE_PREFIX;
    @Value("${cache.prefix.people}")
    private String CACHE_PREFIX_PEOPLE;
    @Value("${cache.ttl}")
    private Long ttl;

    public TaskService(TaskRepo taskRepo, PersonRepo personRepo, ModelMapper mapper, ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.taskRepo = taskRepo;
        this.personRepo = personRepo;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public List<Task> findAll(Pageable pageable) {
         List<Task> tasks = taskRepo.findAllByFilter(pageable);
         return tasks;
    }

    public Task findById(Long id) {
        String cacheKey = CACHE_PREFIX + id;
        Object o = redisTemplate.opsForValue().get(cacheKey);
        Task taskFromCache = objectMapper.convertValue(o, Task.class);
        if (taskFromCache != null)
            return taskFromCache;
        Task task = taskRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Сущность не найдена"));
        redisTemplate.opsForValue().set(cacheKey, task, ttl, TimeUnit.MINUTES);
        return task;
    }


    public void save(Task task) {
        Task taskFromDb = taskRepo.save(task);
        redisTemplate.opsForValue().set(CACHE_PREFIX + taskFromDb.getId(), task, ttl, TimeUnit.MINUTES);
    }

    public void changeStatus(Long id, StatusTask status) {
        if (taskRepo.findById(id).isEmpty())
            throw new EntityNotFoundException("Сущность не найдена");
        String cacheKey = CACHE_PREFIX + id;
        redisTemplate.delete(cacheKey);
        taskRepo.changeStatus(id, status);
    }

    @SneakyThrows
    public void appointExecutor(IdsDto idsDto) {
        Task task = taskRepo.findById(idsDto.idTask()).orElseThrow(() -> new EntityNotFoundException("Cущность не найдена"));
        Person person = personRepo.findById(idsDto.idPerson()).orElseThrow(() -> new EntityNotFoundException("Cущность не найдена"));;
        redisTemplate.delete(List.of(CACHE_PREFIX + idsDto.idTask(), CACHE_PREFIX_PEOPLE + idsDto.idPerson()));
        task.setPerson(person);
        taskRepo.save(task);
    }

}

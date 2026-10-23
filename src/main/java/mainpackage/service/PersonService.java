package mainpackage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import mainpackage.domain.Person;
import mainpackage.dto.PersonDto;
import mainpackage.repo.PersonRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PersonService {

    private final PersonRepo personRepo;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${cache.prefix.people}")
    private String CACHE_PREFIX;
    @Value("${cache.ttl}")
    private Long ttl;

    @Autowired
    public PersonService(PersonRepo personRepo, ModelMapper mapper, ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        this.personRepo = personRepo;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
        this.redisTemplate = redisTemplate;
    }


    public List<Person> findAll() {
        String cacheKey = CACHE_PREFIX;
        Object o = redisTemplate.opsForValue().get(cacheKey);
        List<Person> peopleFromCache = objectMapper.convertValue(o, new TypeReference<List<Person>>() {});
        if (peopleFromCache != null && !peopleFromCache.isEmpty())
            return peopleFromCache;
        List<Person> people = personRepo.findAll();
        List<PersonDto> personDtos = new ArrayList<>();
//        for (Person p: people)
//            personDtos.add(mapper.map(p, PersonDto.class));
        redisTemplate.opsForValue().set(cacheKey, people, ttl, TimeUnit.MINUTES);
        return people;
    }

    public Person findById(Long id) {
        String cacheKey = CACHE_PREFIX + id;
        Object o = redisTemplate.opsForValue().get(cacheKey);
        Person personFromCache = objectMapper.convertValue(o, Person.class);
        if (personFromCache != null)
            return personFromCache;

        Person person = personRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Сущность не найдена"));
        redisTemplate.opsForValue().set(cacheKey, person, ttl, TimeUnit.MINUTES);
//        PersonDto personDto = mapper.map(person, PersonDto.class);
        return person;
    }


    public void save(PersonDto personDto) {
        if (personRepo.findByEmail(personDto.getEmail()).isPresent())
            throw new EntityExistsException("Сущность уже существует");
        Person person = mapper.map(personDto, Person.class);
        personRepo.save(person);
        redisTemplate.opsForValue().set(CACHE_PREFIX + person.getId(), person, ttl, TimeUnit.MINUTES);
    }


}

package mainpackage.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import mainpackage.domain.Person;
import mainpackage.repo.PersonRepo;
import mainpackage.secutiry.domain.Profile;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PersonService {

    private final PersonRepo personRepo;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;


    @Autowired
    public PersonService(PersonRepo personRepo, ModelMapper mapper, ObjectMapper objectMapper) {
        this.personRepo = personRepo;
        this.mapper = mapper;
        this.objectMapper = objectMapper;
    }


    public List<Person> findAll() {
        List<Person> people = personRepo.findAll();
        return people;
    }

    public Person findById(Long id) {
        Person person = personRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Сущность не найдена"));
        return person;
    }


//    public void save(PersonDto personDto) {
//        if (personRepo.findByEmail(personDto.getEmail()).isPresent())
//            throw new EntityExistsException("Сущность уже существует");
//        Profile profile = Profile.builder().email(personDto.getEmail()).password(personDto.getPassword())
//                .encodedPassword(personDto.getPassword()).build();
//        Person person = mapper.map(personDto, Person.class);
//        personRepo.save(person);
//    }

}

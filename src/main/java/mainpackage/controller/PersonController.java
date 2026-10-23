package mainpackage.controller;

import jakarta.validation.Valid;
import mainpackage.domain.Person;
import mainpackage.dto.PersonDto;
import mainpackage.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAll() {
        List<Person> people = personService.findAll();
        return ResponseEntity.ok(people);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getById(@PathVariable Long id) {
        return ResponseEntity.ok(personService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody @Valid PersonDto personDto) {
        personService.save(personDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }





}

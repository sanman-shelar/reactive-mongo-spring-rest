package com.reactive.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j
@RestController
@RequestMapping("/person")
public class PersonController {

	@Autowired
	PersonRepository personRepository;

	@GetMapping
	private Flux<Person> getAllPersons() {
		log.info("/person GET");
		return personRepository.findAll();
	}

	@PostMapping
	private Mono<ResponseEntity<Person>> addPerson(@RequestBody Person person) {
		return personRepository.save(person).map(createdPerson -> new ResponseEntity<>(createdPerson, HttpStatus.CREATED));
	}

	@PutMapping("/{id}")
	private Mono<Person> updatePerson(@RequestBody Person person, @PathVariable String id) {		
		return personRepository.findById(id).flatMap(existingPerson -> updatePerson(person, existingPerson)); 
	}
	
	@DeleteMapping("/{id}")
	private Mono<ResponseEntity<Void>> deletePerson(@PathVariable String id){
		return personRepository.findById(id).flatMap(person -> 
			personRepository.delete(person)
			.then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	private Mono<Person> updatePerson(Person person, Person existingPerson) {
		existingPerson.setName(person.getName());
		return personRepository.save(existingPerson);
	}
}

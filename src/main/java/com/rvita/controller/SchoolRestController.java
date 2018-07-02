package com.rvita.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.rvita.model.School;
import com.rvita.model.Student;
import com.rvita.repository.SchoolRepository;

@RestController
@RequestMapping("/school")
public class SchoolRestController {
	@Autowired // This means to get the bean called schoolRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	private SchoolRepository schoolRepository;

	@PostMapping(path = "/add") // Map ONLY GET Requests
	public @ResponseBody School addNewSchool(@RequestParam String name) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		School school = new School();
		school.setName(name);
		schoolRepository.save(school);
		return school;
	}

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<School> getAllSchools() {
		// This returns a JSON or XML with the schools
		return schoolRepository.findAll();
	}

	@GetMapping(path = "/{id}")
	public School retrieveSchool(@PathVariable long id) {
		Optional<School> school = schoolRepository.findById(id);
		return school.get();
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<Object> updateSchool(@RequestBody School school, @PathVariable long id) {

		Optional<School> schoolOptional = schoolRepository.findById(id);

		if (!schoolOptional.isPresent())
			return ResponseEntity.notFound().build();

		school.setId(id);

		schoolRepository.save(school);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "{id}")
	public void deleteSchool(@PathVariable long id) {
		schoolRepository.deleteById(id);
	}
}
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

import com.rvita.model.Student;
import com.rvita.repository.SchoolRepository;
import com.rvita.repository.StudentRepository;

@RestController
@RequestMapping("/student")
public class StudentRestController {
	@Autowired // This means to get the bean called studentRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	private StudentRepository studentRepository;

	@PostMapping(path = "/add") // Map ONLY GET Requests
	public @ResponseBody Student addNewStudent(@RequestParam String firstName, @RequestParam String lastName,
			@RequestParam Long du) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		Student student = new Student();
		student.setDu(du);
		student.setFirstName(firstName);
		student.setLastName(lastName);
		studentRepository.save(student);
		return student;
	}

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<Student> getAllStudents() {
		// This returns a JSON or XML with the students
		return studentRepository.findAll();
	}

	@GetMapping(path = "/{id}")
	public Student retrieveStudent(@PathVariable long id) {
		Optional<Student> student = studentRepository.findById(id);
		return student.get();
	}

	@PutMapping(path = "/{id}")
	public ResponseEntity<Object> updateStudent(@RequestBody Student student, @PathVariable long id) {

		Optional<Student> studentOptional = studentRepository.findById(id);

		if (!studentOptional.isPresent())
			return ResponseEntity.notFound().build();

		student.setId(id);

		studentRepository.save(student);

		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "{id}")
	public void deleteStudent(@PathVariable long id) {
		studentRepository.deleteById(id);
	}
}

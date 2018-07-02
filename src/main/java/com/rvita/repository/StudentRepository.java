package com.rvita.repository;

import org.springframework.data.repository.CrudRepository;

import com.rvita.model.Student;

//This will be AUTO IMPLEMENTED by Spring into a Bean called studentRepository
//CRUD refers Create, Read, Update, Delete

public interface StudentRepository extends CrudRepository<Student, Long> {

}

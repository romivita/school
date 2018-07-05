package com.rvita.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rvita.model.School;
import com.rvita.model.Student;

//This will be AUTO IMPLEMENTED by Spring into a Bean called schoolRepository
//CRUD refers Create, Read, Update, Delete

public interface SchoolRepository extends JpaRepository<School, Long> {

	Iterable<School> findAllByOrderByIdAsc();

}

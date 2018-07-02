package com.rvita.repository;

import org.springframework.data.repository.CrudRepository;

import com.rvita.model.School;

//This will be AUTO IMPLEMENTED by Spring into a Bean called schoolRepository
//CRUD refers Create, Read, Update, Delete

public interface SchoolRepository extends CrudRepository<School, Long> {

}

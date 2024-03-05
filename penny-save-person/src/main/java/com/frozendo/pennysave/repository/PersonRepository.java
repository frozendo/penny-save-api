package com.frozendo.pennysave.repository;

import com.frozendo.pennysave.domain.entity.Person;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Long> {

    Optional<Person> findByEmail(String email);

}

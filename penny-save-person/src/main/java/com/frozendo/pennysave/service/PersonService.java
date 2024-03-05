package com.frozendo.pennysave.service;

import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.PersonMessageEnum;
import com.frozendo.pennysave.exceptions.BusinessException;
import com.frozendo.pennysave.repository.PersonRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person create(Person person) {
        validatePerson(person);
        return personRepository.save(person);
    }

    private void validatePerson(Person person) {
        var personExist = personRepository.findByEmail(person.getEmail());
        if (personExist.isPresent()) {
            throw new BusinessException(PersonMessageEnum.EMAIL_DUPLICATED);
        }
    }
}

package com.frozendo.pennysave.service;

import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.PersonMessageEnum;
import com.frozendo.pennysave.exceptions.BusinessException;
import com.frozendo.pennysave.repository.PersonRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_DIRECT_EXCHANGE;
import static com.frozendo.pennysave.domain.enums.PersonEventEnum.PERSON_CREATE_KEY;

@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final RabbitTemplate rabbitTemplate;

    public PersonService(PersonRepository personRepository,
                         RabbitTemplate rabbitTemplate) {
        this.personRepository = personRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public Person create(Person person) {
        validatePerson(person);
        var savedEntity = personRepository.save(person);
        triggerPersonEvent(savedEntity, PERSON_CREATE_KEY.getProperty());
        return savedEntity;
    }

    private void validatePerson(Person person) {
        var personExist = personRepository.findByEmail(person.getEmail());
        if (personExist.isPresent()) {
            throw new BusinessException(PersonMessageEnum.EMAIL_DUPLICATED);
        }
    }

    private void triggerPersonEvent(Person person, String routingKey) {
        var createPersonEvent = new CreatePersonEvent(person);
        rabbitTemplate.convertAndSend(PERSON_DIRECT_EXCHANGE.getProperty(), routingKey, createPersonEvent);
    }
}

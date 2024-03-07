package com.frozendo.pennysave.unit;

import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.PersonMessageEnum;
import com.frozendo.pennysave.exceptions.BusinessException;
import com.frozendo.pennysave.exceptions.EntityNotFoundException;
import com.frozendo.pennysave.repository.PersonRepository;
import com.frozendo.pennysave.service.PersonService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unitTest")
class PersonServiceUnitTest {

    private static final String PERSON_EMAIL = "test@email.com";
    private static final String PERSON_NAME = "Test Person";
    private static final String PERSON_PASSWORD = "123@qwe#RS";

    private final PersonRepository personRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PersonService personService;

    public PersonServiceUnitTest() {
        this.rabbitTemplate = mock(RabbitTemplate.class);
        this.personRepository = mock(PersonRepository.class);
        this.personService = new PersonService(personRepository, rabbitTemplate);
    }

    @Test
    void getPersonByExternalId() {
        var mockPerson = new Person(PERSON_EMAIL, PERSON_NAME, LocalDate.now(), PERSON_PASSWORD);

        when(personRepository.findByExternalId(mockPerson.getExternalId())).thenReturn(Optional.of(mockPerson));

        var person = personService.getByExternalId(mockPerson.getExternalId());

        assertThat(person).isNotNull();

        verify(personRepository, times(1)).findByExternalId(mockPerson.getExternalId());
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void getPersonByExternalIdWhenPersonNotExist() {
        var externalId = "abc123";
        var expectedMessage = "Entity id %s not found!".formatted(externalId);

        when(personRepository.findByExternalId(externalId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(
                () -> personService.getByExternalId(externalId)
        ).withMessage(expectedMessage);

        verify(personRepository, times(1)).findByExternalId(externalId);
        verifyNoMoreInteractions(personRepository);
    }

    @Test
    void createPersonSuccessful() {
        var newPerson = new Person(PERSON_EMAIL, PERSON_NAME, LocalDate.now(), PERSON_PASSWORD);

        when(personRepository.save(any(Person.class))).thenReturn(newPerson);

        var savedEntity = personService.create(newPerson);
        assertThat(savedEntity).isNotNull();

        verify(personRepository, times(1)).findByEmail(PERSON_EMAIL);
        verify(personRepository, times(1)).save(newPerson);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(CreatePersonEvent.class));

        verifyNoMoreInteractions(personRepository);
        verifyNoMoreInteractions(rabbitTemplate);
    }

    @Test
    void createPersonWhenEmailIsDuplicated() {
        var newPerson = new Person(PERSON_EMAIL, PERSON_NAME, LocalDate.now(), PERSON_PASSWORD);

        when(personRepository.findByEmail(PERSON_EMAIL)).thenReturn(Optional.of(newPerson));

        var exception = assertThrows(BusinessException.class, () -> personService.create(newPerson));

        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isNotNull();
        assertThat(exception.getCode()).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(PersonMessageEnum.EMAIL_DUPLICATED.getMessage());
        assertThat(exception.getCode()).isEqualTo(PersonMessageEnum.EMAIL_DUPLICATED.getCode());

        verify(personRepository, times(1)).findByEmail(PERSON_EMAIL);
        verifyNoMoreInteractions(personRepository);

        verifyNoInteractions(rabbitTemplate);
    }

}

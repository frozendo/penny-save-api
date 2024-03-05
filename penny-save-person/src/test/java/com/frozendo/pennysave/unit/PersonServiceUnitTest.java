package com.frozendo.pennysave.unit;

import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.PersonMessageEnum;
import com.frozendo.pennysave.exceptions.BusinessException;
import com.frozendo.pennysave.repository.PersonRepository;
import com.frozendo.pennysave.service.PersonService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("unitTest")
class PersonServiceUnitTest {

    private static final String PERSON_EMAIL = "test@email.com";
    private static final String PERSON_NAME = "Test Person";
    private static final String PERSON_PASSWORD = "123@qwe#RS";

    private final PersonRepository personRepository;
    private final PersonService personService;

    public PersonServiceUnitTest() {
        this.personRepository = mock(PersonRepository.class);
        this.personService = new PersonService(personRepository);
    }

    @Test
    void createPersonSuccessful() {
        var newPerson = new Person(PERSON_EMAIL, PERSON_NAME, LocalDate.now(), PERSON_PASSWORD);

        when(personRepository.save(any(Person.class))).thenReturn(newPerson);

        var savedEntity = personService.create(newPerson);
        assertThat(savedEntity).isNotNull();

        verify(personRepository, times(1)).findByEmail(PERSON_EMAIL);
        verify(personRepository, times(1)).save(newPerson);
        verifyNoMoreInteractions(personRepository);
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
    }

}

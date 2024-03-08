package com.frozendo.pennysave.unit;

import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.entity.EmailConfirmation;
import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.domain.enums.PersonActionEnum;
import com.frozendo.pennysave.repository.EmailConfirmationRepository;
import com.frozendo.pennysave.service.EmailConfirmationService;
import com.frozendo.pennysave.service.EmailSenderService;
import com.frozendo.pennysave.service.PersonService;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

class EmailConfirmationServiceUnitTest {

    private static final String PERSON_EMAIL = "test@email.com";
    private static final String PERSON_NAME = "Test Person";
    private static final String PASSWORD = "123@qwe#J";
    private static final String PERSON_EXTERNAL_ID = "abc123";
    public static final long PERSON_ID = 1L;

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final PersonService personService;
    private final EmailSenderService emailSenderService;
    private final EmailConfirmationService emailConfirmationService;

    public EmailConfirmationServiceUnitTest() {
        this.emailSenderService = mock(EmailSenderService.class);
        this.emailConfirmationRepository = mock(EmailConfirmationRepository.class);
        this.personService = mock(PersonService.class);
        this.emailConfirmationService =
                new EmailConfirmationService(emailConfirmationRepository, personService, emailSenderService);
    }

    @Test
    void dealWithPersonEventWhenPersonHasNotConfirmationPending() {
        var person = new Person(PERSON_EMAIL, PERSON_NAME, LocalDate.now(), PASSWORD);
        var createPersonEvent = new CreatePersonEvent(
                PERSON_ID, PERSON_EXTERNAL_ID, PERSON_EMAIL, PERSON_NAME, PersonActionEnum.CREATED);

        when(personService.getByExternalId(PERSON_EXTERNAL_ID)).thenReturn(person);
        when(emailConfirmationRepository
                .findConfirmationPending(PERSON_ID)).thenReturn(Optional.empty());

        var result = emailConfirmationService.notificationForPersonCreateEvent(createPersonEvent);

        assertThat(result).isTrue();

        verify(personService, times(1)).getByExternalId(PERSON_EXTERNAL_ID);
        verify(emailConfirmationRepository, times(1)).findConfirmationPending(PERSON_ID);
        verify(emailConfirmationRepository, times(1)).save(any(EmailConfirmation.class));
        verify(emailSenderService, times(1)).sendEmail(person);

        verifyNoMoreInteractions(personService);
        verifyNoMoreInteractions(emailConfirmationRepository);
        verifyNoMoreInteractions(emailSenderService);
    }

    @Test
    void dealWithPersonEventWhenPersonHasConfirmationPending() {
        var person = new Person(PERSON_EMAIL, PERSON_NAME, LocalDate.now(), PASSWORD);
        var createPersonEvent = new CreatePersonEvent(
                PERSON_ID, PERSON_EXTERNAL_ID, PERSON_EMAIL, PERSON_NAME, PersonActionEnum.CREATED);
        var emailConfirmation = new EmailConfirmation("def098", person, PersonActionEnum.CREATED);

        when(personService.getByExternalId(PERSON_EXTERNAL_ID)).thenReturn(person);
        when(emailConfirmationRepository
                .findConfirmationPending(PERSON_ID)).thenReturn(Optional.of(emailConfirmation));

        var result = emailConfirmationService.notificationForPersonCreateEvent(createPersonEvent);

        assertThat(result).isFalse();

        verify(emailConfirmationRepository, times(1)).findConfirmationPending(PERSON_ID);

        verifyNoMoreInteractions(emailConfirmationRepository);

        verifyNoInteractions(personService);
        verifyNoInteractions(emailSenderService);
    }

}

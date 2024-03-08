package com.frozendo.pennysave.service;

import com.frozendo.pennysave.domain.dto.events.CreatePersonEvent;
import com.frozendo.pennysave.domain.entity.EmailConfirmation;
import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.repository.EmailConfirmationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmailConfirmationService {

    private final EmailConfirmationRepository emailConfirmationRepository;
    private final PersonService personService;
    private final EmailSenderService emailSenderService;

    public EmailConfirmationService(EmailConfirmationRepository emailConfirmationRepository,
                                    PersonService personService,
                                    EmailSenderService emailSenderService) {
        this.emailConfirmationRepository = emailConfirmationRepository;
        this.personService = personService;
        this.emailSenderService = emailSenderService;
    }

    public boolean notificationForPersonCreateEvent(CreatePersonEvent createPersonEvent) {
        var confirmationForEvent = emailConfirmationRepository.findConfirmationPending(createPersonEvent.id());

        if (confirmationForEvent.isEmpty()) {
            var person = personService.getByExternalId(createPersonEvent.externalId());
            createEmailConfirmation(createPersonEvent, person);
            sendNotification(person);
            return true;
        }
        return false;
    }

    private void createEmailConfirmation(CreatePersonEvent createPersonEvent, Person person) {
        var newEmailConfirmation = new EmailConfirmation(
                generateEmailConfirmationToken(), person, createPersonEvent.action());
        emailConfirmationRepository.save(newEmailConfirmation);
    }

    private void sendNotification(Person person) {
        emailSenderService.sendEmail(person);
    }

    private String generateEmailConfirmationToken() {
        return UUID.randomUUID()
                .toString()
                .trim()
                .replace("-", "");
    }
}

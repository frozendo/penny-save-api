package com.frozendo.pennysave.service;

import com.frozendo.pennysave.config.properties.PennySaveEmailProperties;
import com.frozendo.pennysave.domain.entity.Person;
import com.frozendo.pennysave.exceptions.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
public class EmailSenderService {

    private static final String PERSON_NAME_VARIABLE = "person_name";
    private static final String CONFIRMATION_LINK_VARIABLE = "confirmation_link";
    private static final String CONFIRM_EMAIL_TEMPLATE = "confirm_email";

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PennySaveEmailProperties pennySaveEmailProperties;

    public EmailSenderService(TemplateEngine templateEngine,
                              JavaMailSender mailSender,
                              PennySaveEmailProperties pennySaveEmailProperties) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.pennySaveEmailProperties = pennySaveEmailProperties;
    }

    public void sendEmail(String email, String name) {
        try {
            MimeMessage message = createMimeMessage(email, name);
            mailSender.send(message);
        } catch (Exception exception) {
            throw new EmailException(exception);
        }
    }

    private MimeMessage createMimeMessage(String email, String name) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(pennySaveEmailProperties.from(), pennySaveEmailProperties.name());
            helper.setTo(email);
            helper.setSubject(pennySaveEmailProperties.subject());

            Context context = new Context();
            context.setVariable(PERSON_NAME_VARIABLE, name);
            context.setVariable(CONFIRMATION_LINK_VARIABLE, pennySaveEmailProperties.link());
            String emailTemplate = templateEngine.process(CONFIRM_EMAIL_TEMPLATE, context);

            helper.setText(emailTemplate, true);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new EmailException(e);
        }
        return message;
    }

}

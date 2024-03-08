package com.frozendo.pennysave.service;

import com.frozendo.pennysave.domain.entity.Person;
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

//    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailSenderService(TemplateEngine templateEngine) {
//        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(Person person) {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message);
//
//        try {
//            helper.setFrom("flavio.ap.rozendo@gmail.com", "Flavio Rozendo"); //todo
//            helper.setTo(person.getEmail());
//            helper.setSubject("Penny Save - Confirme Sua Conta");
//
//            Context context = new Context();
//            context.setVariable(PERSON_NAME_VARIABLE, person.getName());
//            context.setVariable(CONFIRMATION_LINK_VARIABLE, "http://localhost:9000/penny-save"); //todo
//            String emailTemplate = templateEngine.process(CONFIRM_EMAIL_TEMPLATE, context);
//
//            helper.setText(emailTemplate, true);
//
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }
//
//        mailSender.send(message);
    }

}

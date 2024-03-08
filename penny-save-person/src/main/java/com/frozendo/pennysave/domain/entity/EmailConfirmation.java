package com.frozendo.pennysave.domain.entity;

import com.frozendo.pennysave.converters.YesNoConverter;
import com.frozendo.pennysave.domain.converters.PersonActionConverter;
import com.frozendo.pennysave.domain.enums.PersonActionEnum;
import com.frozendo.pennysave.enums.YesNoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "email_confirmation")
public class EmailConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_email_confirmation")
    private Long id;

    @Column(name = "cd_token", nullable = false, length = 32)
    private String token;

    @Column(name = "dt_created", nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = "dt_limit_confirmation", nullable = false)
    private LocalDateTime dateLimitConfirmation;

    @Column(name = "dt_confirmation")
    private LocalDateTime dateConfirmation;

    @Column(name = "in_email_confirmed", nullable = false)
    @Convert(converter = YesNoConverter.class)
    private YesNoEnum emailConfirmed;

    @Column(name = "in_person_action", nullable = false)
    @Convert(converter = PersonActionConverter.class)
    private PersonActionEnum action;

    @JoinColumn(name = "id_person")
    @ManyToOne(fetch = FetchType.EAGER)
    private Person person;

    public EmailConfirmation() {
    }

    public EmailConfirmation(String token, Person person, PersonActionEnum action) {
        this.token = token;
        this.person = person;
        this.action = action;

        this.dateCreated = LocalDateTime.now();
        this.dateLimitConfirmation = LocalDateTime.now();
        this.emailConfirmed = YesNoEnum.NO;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getDateLimitConfirmation() {
        return dateLimitConfirmation;
    }

    public LocalDateTime getDateConfirmation() {
        return dateConfirmation;
    }

    public YesNoEnum getEmailConfirmed() {
        return emailConfirmed;
    }

    public PersonActionEnum getAction() {
        return action;
    }

    public Person getPerson() {
        return person;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EmailConfirmation that = (EmailConfirmation) object;
        return Objects.equals(token, that.token) && Objects.equals(dateCreated, that.dateCreated) && Objects.equals(dateLimitConfirmation, that.dateLimitConfirmation) && Objects.equals(dateConfirmation, that.dateConfirmation) && emailConfirmed == that.emailConfirmed && action == that.action && person.equals(that.person);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, dateCreated, dateLimitConfirmation, dateConfirmation, emailConfirmed, action) + person.hashCode();
    }

    @Override
    public String toString() {
        return "EmailConfirmation{" +
                "token='" + token + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLimitConfirmation=" + dateLimitConfirmation +
                ", dateConfirmation=" + dateConfirmation +
                ", emailConfirmed=" + emailConfirmed +
                ", action=" + action +
                ", person=" + person +
                '}';
    }
}

package com.frozendo.pennysave.domain.entity;

import com.frozendo.pennysave.converters.YesNoConverter;
import com.frozendo.pennysave.domain.converters.StatusPersonConverter;
import com.frozendo.pennysave.domain.enums.StatusPersonEnum;
import com.frozendo.pennysave.enums.YesNoEnum;
import com.frozendo.pennysave.helper.GenerateExternalId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_person")
    private Long id;

    @Column(name = "cd_external", nullable = false, length = 32)
    private String externalId;

    @Column(name = "ds_email", nullable = false, length = 50)
    private String email;

    @Column(name = "nm_person", nullable = false, length = 80)
    private String name;

    @Column(name = "dt_birth", nullable = false)
    private LocalDate birthDate;

    @Column(name = "in_status", nullable = false)
    @Convert(converter = StatusPersonConverter.class)
    private StatusPersonEnum status;

    @Column(name = "ds_password", nullable = false, length = 72)
    private String password;

    @Column(name = "in_email_confirmed", nullable = false)
    @Convert(converter = YesNoConverter.class)
    private YesNoEnum emailConfirmed;

    @Column(name = "dt_created", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "dt_updated")
    private LocalDateTime updatedAt;

    public Person() {
    }

    public Person(String email, String name, LocalDate birthDate, String password) {
        this.email = email;
        this.name = name;
        this.birthDate = birthDate;
        this.password = password;

        this.externalId = GenerateExternalId.generate();
        this.status = StatusPersonEnum.PENDING;
        this.emailConfirmed = YesNoEnum.NO;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getExternalId() {
        return externalId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public StatusPersonEnum getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public YesNoEnum getEmailConfirmed() {
        return emailConfirmed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Person person = (Person) object;
        return Objects.equals(externalId, person.externalId) && Objects.equals(email, person.email) && Objects.equals(name, person.name) && Objects.equals(birthDate, person.birthDate) && status == person.status && Objects.equals(password, person.password) && emailConfirmed == person.emailConfirmed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, email, name, birthDate, status, password, emailConfirmed);
    }

    @Override
    public String toString() {
        return "Person{" +
                "externalId='" + externalId + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", status=" + status +
                ", password='" + password + '\'' +
                ", emailConfirmed=" + emailConfirmed +
                '}';
    }
}

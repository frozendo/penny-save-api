package com.frozendo.pennysave.domain.dto.request;

import com.frozendo.pennysave.domain.entity.Person;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PersonCreateRequest(
        @Size(max = 50, message = "person_email_many_characters")
        @Pattern(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "person_email_invalid")
        String email,
        @NotBlank(message = "person_name_mandatory")
        @Size(max = 80, message = "person_name_many_characters")
        String name,
        @NotNull(message = "person_birth_date_mandatory")
        LocalDate birthDate,
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[\\W])(?!.*\\s).{8,20}$",
                message = "password_invalid")
        String password
) {

    public Person convertToEntity() {
        return new Person(
                this.email,
                this.name,
                this.birthDate,
                this.password
        );
    }

}

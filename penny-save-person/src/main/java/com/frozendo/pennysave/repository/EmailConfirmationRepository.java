package com.frozendo.pennysave.repository;

import com.frozendo.pennysave.domain.entity.EmailConfirmation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailConfirmationRepository extends CrudRepository<EmailConfirmation, Long> {

    String CONFIRMED_PENDING_QUERY = """
            SELECT e
            FROM EmailConfirmation e
            JOIN e.person p
            where p.id = :personId
            and e.emailConfirmed = YesNoEnum.NO
            """;

    Optional<EmailConfirmation> findByToken(String token);

    @Query(CONFIRMED_PENDING_QUERY)
    Optional<EmailConfirmation> findConfirmationPending(Long personId);

}

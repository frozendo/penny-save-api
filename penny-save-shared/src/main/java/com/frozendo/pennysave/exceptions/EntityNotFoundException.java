package com.frozendo.pennysave.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long identifier) {
        super("Entity id %d not found!".formatted(identifier));
    }

    public EntityNotFoundException(String identifier) {
        super("Entity id %s not found!".formatted(identifier));
    }

}

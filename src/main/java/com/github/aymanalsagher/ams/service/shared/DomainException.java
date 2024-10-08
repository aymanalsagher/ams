package com.github.aymanalsagher.ams.service.shared;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {

    private final DomainError domainError;

    public DomainException(DomainError domainError) {
        this.domainError = domainError;
    }

    public DomainException(String debugMessage, DomainError domainError) {
        super(debugMessage);
        this.domainError = domainError;
    }

    public DomainException(String debugMessage, Throwable ex, DomainError domainError) {
        super(debugMessage, ex);
        this.domainError = domainError;
    }

    @Override
    public String toString() {
        return "DomainException {errors = " + domainError + '}';
    }
}

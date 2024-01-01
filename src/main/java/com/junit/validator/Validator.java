package com.junit.validator;

public interface Validator<T> {

    ValidationResult validate(T object);
}

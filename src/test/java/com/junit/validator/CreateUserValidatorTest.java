package com.junit.validator;

import com.junit.dto.CreateUserDto;
import com.junit.entity.Gender;
import com.junit.entity.Role;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreateUserValidatorTest {

    private final CreateUserValidator validator = CreateUserValidator.getInstance();


    @Test
    void validUserDto(){
        CreateUserDto validUserDto = CreateUserDto.builder()
                .name("John")
                .birthday("2023-12-24")
                .email("john@gmail.com")
                .password("password")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actualResult = validator.validate(validUserDto);

        assertFalse(actualResult.hasErrors());
    }


    @Test
    void invalidBirthDate(){
        CreateUserDto validUserDto = CreateUserDto.builder()
                .name("John")
                .birthday("2023-12-24 22:52")
                .email("john@gmail.com")
                .password("password")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actualResult = validator.validate(validUserDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.birthday");
    }

    @Test
    void invalidRole(){
        CreateUserDto validUserDto = CreateUserDto.builder()
                .name("John")
                .birthday("2023-12-24")
                .email("john@gmail.com")
                .password("password")
                .role("fake")
                .gender(Gender.MALE.name())
                .build();

        ValidationResult actualResult = validator.validate(validUserDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.role");
    }

    @Test
    void invalidGender(){
        CreateUserDto validUserDto = CreateUserDto.builder()
                .name("John")
                .birthday("2023-12-24")
                .email("john@gmail.com")
                .password("password")
                .role(Role.USER.name())
                .gender("fake")
                .build();

        ValidationResult actualResult = validator.validate(validUserDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo("invalid.gender");
    }

    @Test
    void allArgumentsInvalid(){
        CreateUserDto validUserDto = CreateUserDto.builder()
                .name("John")
                .birthday("12-12-2000")
                .email("john@gmail.com")
                .password("password")
                .role("fake_role")
                .gender("fake_gender")
                .build();

        ValidationResult actualResult = validator.validate(validUserDto);

        assertThat(actualResult.getErrors()).hasSize(3);
        List<String> errorCodes = actualResult.getErrors().stream().map(Error::getCode).toList();
        assertThat(errorCodes).contains("invalid.birthday","invalid.gender","invalid.role");
    }
}
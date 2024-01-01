package com.junit.mapper;

import com.junit.dto.CreateUserDto;
import com.junit.entity.Gender;
import com.junit.entity.Role;
import com.junit.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreateUserMapperTest {

    private final CreateUserMapper mapper = CreateUserMapper.getInstance();

    @Test
    void mappingSuccess() {
        CreateUserDto dto = CreateUserDto.builder()
                .name("John")
                .birthday("2023-12-24")
                .email("john@gmail.com")
                .password("password")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();

        User actualResult = mapper.map(dto);

        User exceptedResult = User.builder()
                .name("John")
                .birthday(LocalDate.of(2023,12,24))
                .email("john@gmail.com")
                .password("password")
                .role(Role.USER)
                .gender(Gender.MALE)
                .build();


        assertThat(actualResult).isEqualTo(exceptedResult);
    }
}
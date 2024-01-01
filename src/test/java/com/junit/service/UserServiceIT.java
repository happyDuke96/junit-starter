package com.junit.service;

import com.junit.IntegrationTestBase;
import com.junit.dao.UserDao;
import com.junit.dto.CreateUserDto;
import com.junit.dto.UserDto;
import com.junit.entity.Gender;
import com.junit.entity.Role;
import com.junit.entity.User;
import com.junit.mapper.CreateUserMapper;
import com.junit.mapper.UserMapper;
import com.junit.validator.CreateUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserServiceIT extends IntegrationTestBase {

    private UserService userService;
    private UserDao userDao;

    @BeforeEach
    void init(){
        userDao = UserDao.getInstance();
        userService = new UserService(
                CreateUserValidator.getInstance(),
                userDao,
                CreateUserMapper.getInstance(),
                UserMapper.getInstance());
    }


    @Test
    void login(){
        User user = userDao.save(getUser("john@gmail.com"));

        Optional<UserDto> actualResult = userService.login(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void create(){
        CreateUserDto createUserDto = getCreateUserDto();

        UserDto actualResult = userService.create(createUserDto);

        assertNotNull(actualResult.getId());

    }



    private static CreateUserDto getCreateUserDto() {
        return CreateUserDto.builder()
                .name("John")
                .birthday("2023-12-24")
                .email("john@gmail.com")
                .password("password")
                .role(Role.USER.name())
                .gender(Gender.MALE.name())
                .build();
    }


    private static User getUser(String email) {
        return User.builder()
                .id(99)
                .name("John")
                .email(email)
                .password("123")
                .gender(Gender.MALE)
                .role(Role.USER)
                .birthday(LocalDate.of(2023, 12, 24))
                .build();
    }

}

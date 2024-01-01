package com.junit.service;

import com.junit.dao.UserDao;
import com.junit.dto.CreateUserDto;
import com.junit.dto.UserDto;
import com.junit.entity.Gender;
import com.junit.entity.Role;
import com.junit.entity.User;
import com.junit.exception.ValidationException;
import com.junit.mapper.CreateUserMapper;
import com.junit.mapper.UserMapper;
import com.junit.validator.CreateUserValidator;
import com.junit.validator.Error;
import com.junit.validator.ValidationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private CreateUserValidator createUserValidator;
    @Mock
    private UserDao userDao;
    @Mock
    private CreateUserMapper createUserMapper;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void loginSuccess() {
        User user = getUser();
        UserDto userDto = getUserDto();
        doReturn(Optional.of(user)).when(userDao).findByEmailAndPassword(user.getEmail(), user.getPassword());
        doReturn(userDto).when(userMapper).map(user);

        Optional<UserDto> actualResult = userService.login(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(userDto);

    }


    @Test
    void loginFailure() {

        // stub from userDao
        doReturn(Optional.empty()).when(userDao).findByEmailAndPassword(any(),any());
        Optional<UserDto> actualResult = userService.login("dummy", "123");

        assertThat(actualResult).isEmpty();

    }

    @Test
    void create(){
        // given
        CreateUserDto createUserDto = getCreateUserDto();
        User user = getUser();
        UserDto userDto = getUserDto();
        doReturn(new ValidationResult()).when(createUserValidator).validate(createUserDto);
        doReturn(user).when(createUserMapper).map(createUserDto);
        doReturn(userDto).when(userMapper).map(user);

        // when
        UserDto actualResult = userService.create(createUserDto);

        // then
        assertThat(actualResult).isEqualTo(userDto);
        // проверяет вызван ли метод save
        verify(userDao).save(user);
    }

    @Test
    void shouldThrowExceptionIfDtoInvalid(){
        CreateUserDto createUserDto = getCreateUserDto();
        ValidationResult validationResult = new ValidationResult();
        validationResult.add(Error.of("invalid.role","Role is invalid"));
        doReturn(validationResult).when(createUserValidator).validate(createUserDto);

        assertThrows(ValidationException.class, () -> userService.create(createUserDto));
        //  проверка на не вызваные
        verifyNoInteractions(userDao,createUserMapper,userMapper);
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


    private static User getUser() {
        return User.builder()
                .id(99)
                .name("John")
                .email("john@gmail.com")
                .password("123")
                .gender(Gender.MALE)
                .role(Role.USER)
                .birthday(LocalDate.of(2023, 12, 24))
                .build();
    }

    private static UserDto getUserDto() {
        return UserDto.builder()
                .id(99)
                .name("John")
                .email("john@gmail.com")
                .gender(Gender.MALE)
                .role(Role.USER)
                .birthday(LocalDate.of(2023, 12, 24))
                .build();
    }

}
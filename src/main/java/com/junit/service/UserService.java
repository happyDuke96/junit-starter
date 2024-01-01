package com.junit.service;

import com.junit.dao.UserDao;
import com.junit.dto.CreateUserDto;
import com.junit.dto.UserDto;
import com.junit.exception.ValidationException;
import com.junit.mapper.CreateUserMapper;
import com.junit.mapper.UserMapper;
import com.junit.validator.CreateUserValidator;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;


@NoArgsConstructor(access = PRIVATE)
public class UserService {

    private static final UserService INSTANCE = new UserService();

    private CreateUserValidator createUserValidator = CreateUserValidator.getInstance();
    private UserDao userDao = UserDao.getInstance();
    private CreateUserMapper createUserMapper = CreateUserMapper.getInstance();
    private UserMapper userMapper = UserMapper.getInstance();

    public UserService(CreateUserValidator createUserValidator, UserDao userDao,
                       CreateUserMapper createUserMapper,
                       UserMapper userMapper){
        this.createUserValidator = createUserValidator;
        this.userDao = userDao;
        this.createUserMapper = createUserMapper;
        this.userMapper = userMapper;
    }

    public static UserService getInstance() {
        return INSTANCE;
    }

    public Optional<UserDto> login(String email, String password) {
        return userDao.findByEmailAndPassword(email, password)
                .map(userMapper::map);
    }

    @SneakyThrows
    public UserDto create(CreateUserDto userDto) {
        var validationResult = createUserValidator.validate(userDto);
        if (validationResult.hasErrors()) {
            throw new ValidationException(validationResult.getErrors());
        }
        var userEntity = createUserMapper.map(userDto);
        userDao.save(userEntity);

        return userMapper.map(userEntity);
    }
}

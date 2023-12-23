package com.junit.service;

import com.junit.dao.UserDao;
import com.junit.dto.User;

import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class UserService {

    private final List<User> users = new ArrayList<>();
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAll() {
        return users;
    }

    public boolean add(User... user) {
        return users.addAll(Arrays.asList(user));
    }

    public boolean delete(Integer userid){
        return userDao.delete(userid);
    }


    public Optional<User> login(String username, String password) {
        if (username == null || password == null){
            throw new IllegalArgumentException("username or password can't be null");
        }

        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedMapValue() {
        return users.stream()
                .collect(toMap(User::getId, identity()));
    }
}

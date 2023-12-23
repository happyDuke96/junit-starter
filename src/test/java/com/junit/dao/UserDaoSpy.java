package com.junit.dao;

import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

public class UserDaoSpy extends UserDao {
    // примерно такой Answer создается
    private Map<Integer,Boolean> answers = new HashMap<>();
    Answer1<Integer,Boolean> answer;

    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId,false);
    }
}

package com.junit.dao;

import org.mockito.stubbing.Answer1;

import java.util.HashMap;
import java.util.Map;

/** Mockito создает прокси объект with extends и возвращает значение Answer(stub),исходя из переданного аргумента
 * */
public class UserDaoMock extends UserDao {

    // примерно такой Answer создается,в случае Spy,если не будет answer то возвращается реальный
    // объект(вызывается метод реального объекта)
    private Map<Integer,Boolean> answers = new HashMap<>();
    private final UserDao userDao;
    Answer1<Integer,Boolean> answer;

    public UserDaoMock(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean delete(Integer userId) {
        return answers.getOrDefault(userId,userDao.delete(userId));
    }
}

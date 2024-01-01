package com.junit.dao;

import com.junit.IntegrationTestBase;
import com.junit.entity.Gender;
import com.junit.entity.Role;
import com.junit.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserDaoIT extends IntegrationTestBase {

    private final UserDao userDao = UserDao.getInstance();

    @Test
    void findAll() {
        User user1 = userDao.save(getUser("test1@gmail.com"));
        User user2 = userDao.save(getUser("test2@gmail.com"));
        User user3 = userDao.save(getUser("test3@gmail.com"));

        List<User> actualResult = userDao.findAll();

        assertThat(actualResult).hasSize(3);

        List<Integer> userIds = actualResult.stream()
                .map(User::getId)
                .toList();
        assertThat(userIds).contains(user1.getId(),user2.getId(),user3.getId());

    }

    @Test
    void findById() {
        User user = userDao.save(getUser("test@gmail.com"));

        Optional<User> actualResult = userDao.findById(user.getId());

        assertThat(actualResult).isPresent();

    }

    @Test
    void save() {
        User user = getUser("test@gmail.com");

        User actualResult = userDao.save(user);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByEmailAndPassword() {
        User user = userDao.save(getUser("test@gmail.com"));

        Optional<User> actualResult = userDao.findByEmailAndPassword(user.getEmail(), user.getPassword());

        assertThat(actualResult).isPresent();
    }

    @Test
    void shouldNotFindByEmailAndPasswordIfUserDoesntExists() {
        userDao.save(getUser("test@gmail.com"));

        Optional<User> actualResult = userDao.findByEmailAndPassword("dummy","123");

        assertThat(actualResult).isEmpty();
    }
    @Test
    void deleteExistingUser() {
        User user = userDao.save(getUser("test@gmail.com"));

        boolean actualResult = userDao.delete(user.getId());

        assertTrue(actualResult);
    }

    @Test
    void deleteNotExistingUser() {
        User user = userDao.save(getUser("test@gmail.com"));

        boolean actualResult = userDao.delete(1000500);

        assertFalse(actualResult);
    }

    @Test
    void update() {
        // given
        User user = userDao.save(getUser("test@gmail.com"));
        user.setName("Conor");
        user.setPassword("new_password");

        // when
        userDao.update(user);

        // then
        User actualResult = userDao.findById(user.getId()).get();
        assertThat(actualResult).isEqualTo(user);
    }


    private static User getUser(String email) {
        return User.builder()
                .name("John")
                .email(email)
                .password("123")
                .gender(Gender.MALE)
                .role(Role.USER)
                .birthday(LocalDate.of(2023, 12, 24))
                .build();
    }
}
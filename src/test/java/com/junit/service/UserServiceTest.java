package com.junit.service;

import com.junit.dto.User;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Lifecycle Test - @BeforeAll -- @BeforeEach -- Test -- @AfterEach -- @AfterAll
 *
 * @BeforeAll - вызывается глобально для всех тестов в этом классе
 * @BeforeEach - вызывается отдельно для каждого класса
 * @AfterEach - вызывается отдельно для каждого класса
 * @AfterAll - вызывается глобально для всех тестов в этом классе
 */


/*@TestInstance(TestInstance.Lifecycle.PER_METHOD)  - это означает по умолчанию для каждое метода(тест) создается новый класс,
 соответственно @BeforeAll и @AfterAll не будет работать ,потому что он должен быть для всех тестов один чтобы работал
 нам нужен @BeforeAll и @AfterAll сделать статичный или  @TestInstance(TestInstance.Lifecycle.PER_CLASS) */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    private UserService userService;
    private static final User JOHN = User.of(1, "John", "123");
    private static final User SARAH = User.of(2, "Sarah", "111");


    @BeforeAll
    void init() {
        System.out.println("Before All: " + this);
    }

    @BeforeEach
    void prepare() {
        this.userService = new UserService();
        System.out.println("Before Each " + this);
    }

    @Test
    void usersEmptyIfUsersNotAdded() {
        var users = userService.getAll();

//        Согласно источнику, сообщение печатается только в том случае, если утверждение не выполнено.
        assertTrue(users.isEmpty(), () -> "Users is empty");
//        assertFalse(users.isEmpty(),() -> "Users should be empty");
        System.out.println("usersEmptyIfUsersNotAdded Test " + this);
    }

    @Test
    void usersSizeIfUsersAdded() {
        userService.add(JOHN);
        userService.add(SARAH);

        var users = userService.getAll();

        Assertions.assertEquals(2, users.size());
        System.out.println("usersSizeIfUsersAdded Test " + this);
    }

    @Test
    void loginSuccessIfUserExists() {
        userService.add(JOHN);

        Optional<User> maybeUser = userService.login(JOHN.getUsername(), JOHN.getPassword());
        assertTrue(maybeUser.isPresent());

        maybeUser.ifPresent(user -> assertEquals(JOHN,user));

    }


    @Test
    void logicFailureIfPasswordNotCorrect(){
        userService.add(SARAH);

        Optional<User> maybeUser = userService.login(SARAH.getUsername(), "test_dummy");

        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void logicFailureIfUserDoesNotExist(){
        userService.add(SARAH);

        Optional<User> maybeUser = userService.login("dummy", SARAH.getPassword());
        assertTrue(maybeUser.isEmpty());
    }

    @AfterEach
    void closeConnection() {
        System.out.println("After Each " + this);
    }

    @AfterAll
    void clear() {
        System.out.println("After All: " + this);
    }

}

package com.junit.service;

import com.junit.dto.User;
import com.junit.paramresolver.UserServiceParameterResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
/* @Tag - с помощью этой аннотации,во время запуска,можем исключить или наоборот запускает тестов с помеченным аннотации*/
@Tag("all")
        /*По умолчанию запускается с помощью какой-то алгоритма,но можно изменить последовательность запуска тестов есть 4 варианты,
         * MethodOrderer.MethodName, MethodOrderer.OrderAnnotation, MethodOrderer.DisplayName , MethodOrderer.Random
         * но лучше их не использовать,в пользу chain gang anti - pattern
         * Пара тестов, которые должны выполняться в определенном порядке, т.е. один тест меняет глобальное состояние системы
         * (глобальные переменные, данные в базе данных) и от этого зависит следующий тест(ы). */
//@TestMethodOrder(MethodOrderer.Random.class)
    /**Dependency Injection - механизм предоставления каких либо параметра либо класса,в отличие spring
     * в JUnit5 эти параметры или классы(включая различные метаданные) предоставляется с помощью ParameterResolver
     * создаем custom Parameter Resolve чтобы этот parameterResolver работал нам надо объявлять его внутри аннотации @ExtendWith
     * */
@ExtendWith({
        UserServiceParameterResolver.class
})
class UserServiceTest {

    private UserService userService;
    private static final User JOHN = User.of(1, "John", "123");
    private static final User SARAH = User.of(2, "Sarah", "111");


    UserServiceTest(TestInfo testInfo){
        System.out.println(testInfo.getTestMethod());
        System.out.println(testInfo.getTestClass());
        System.out.println(testInfo.getDisplayName());
        System.out.println(testInfo.getTags());
    }

    @BeforeAll
    void init() {
        System.out.println("Before All: " + this);
    }

    @BeforeEach
    void prepare(UserService userService) {
        this.userService = userService;
        System.out.println("USERSERVICE : Before Each" + userService.toString());
        System.out.println("Before Each " + this);
    }

    @Test
    void usersEmptyIfUsersNotAdded() {
        System.out.println("USERSERVICE : usersEmptyIfUsersNotAdded" + userService.toString());
        var users = userService.getAll();

        //hamcrest
//        MatcherAssert.assertThat(users,IsEmptyCollection.empty());
//        Согласно источнику, сообщение печатается только в том случае, если утверждение не выполнено.
        assertTrue(users.isEmpty(), () -> "Users is empty");
//        assertFalse(users.isEmpty(),() -> "Users should be empty");
        System.out.println("usersEmptyIfUsersNotAdded Test " + this);
    }

    @Test
    void usersSizeIfUsersAdded() {
        System.out.println("USERSERVICE : usersSizeIfUsersAdded" + userService.toString());
        userService.add(JOHN, SARAH);

        var users = userService.getAll();

        org.assertj.core.api.Assertions.assertThat(users).hasSize(2);
//        Assertions.assertEquals(2, users.size());
        System.out.println("usersSizeIfUsersAdded Test " + this);
    }

    @Test
    void userConvertedToMapById() {
        userService.add(JOHN, SARAH);
        Map<Integer, User> users = userService.getAllConvertedMapValue();

        //hamcrest
//        MatcherAssert.assertThat(users, IsMapContaining.hasKey(JOHN.getId()));

//        assertj
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(users).containsKeys(JOHN.getId(), SARAH.getId()),
                () -> org.assertj.core.api.Assertions.assertThat(users).containsValues(JOHN, SARAH)
        );

    }

    @AfterEach
    void closeConnection() {
        System.out.println("After Each " + this);
    }

    @AfterAll
    void clear() {
        System.out.println("After All: " + this);
    }

    @Nested
    @DisplayName("login functionality test")
    class TestLogin {

        @Test
        @Tag("login")
        @Order(1)
        void loginSuccessIfUserExists() {
            userService.add(JOHN);
//    assertj
            Optional<User> maybeUser = userService.login(JOHN.getUsername(), JOHN.getPassword());
            org.assertj.core.api.Assertions.assertThat(maybeUser).isPresent();
//        assertTrue(maybeUser.isPresent());

            maybeUser.ifPresent(user -> org.assertj.core.api.Assertions.assertThat(user).isEqualTo(JOHN));
//        maybeUser.ifPresent(user -> assertEquals(JOHN,user));

        }

        @Test
        @Tag("login")
        @Order(2)
        void logicFailureIfUserDoesNotExist() {
            userService.add(SARAH);

            Optional<User> maybeUser = userService.login("dummy", SARAH.getPassword());
            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void logicFailureIfPasswordNotCorrect() {
            userService.add(SARAH);

            Optional<User> maybeUser = userService.login(SARAH.getUsername(), "test_dummy");

            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void throwExceptionIfUserNameOrPasswordNull() {
            // bad practice
            try {
                userService.login(null, "test_123");
                Assertions.fail("login should throw exception on null username");
            } catch (IllegalArgumentException e) {
                assertTrue(true);
            }
        }

        @Test
//    @org.junit.Test(expected = IllegalArgumentException.class)     /*для нижеуказанного функционала,в старых версиях junit4 надо было использовать в аннотации */
        void throwExceptionIfUserNameOrPasswordNull2() {
//        best practice
//        assertThrows(IllegalArgumentException.class,() -> userService.login(null,"test_123"));
            assertAll(
                    () -> {
                        var exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "test_123"));
                        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).isEqualTo("username or password can't be null");
                    },
                    () -> assertThrows(IllegalArgumentException.class, () -> userService.login("test_123", null))
            );
        }
    }

}

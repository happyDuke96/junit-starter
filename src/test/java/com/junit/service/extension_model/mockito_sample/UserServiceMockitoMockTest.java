package com.junit.service.extension_model.mockito_sample;

import com.junit.dao.UserDao;
import com.junit.dto.User;
import com.junit.extension.*;
import com.junit.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Lifecycle Test - @BeforeAll -- @BeforeEach -- Test -- @AfterEach -- @AfterAll
 *
 * @BeforeAll - вызывается глобально для всех тестов в этом классе
 * @BeforeEach - вызывается отдельно для каждого класса
 * @AfterEach - вызывается отдельно для каждого класса
 * @AfterAll - вызывается глобально для всех тестов в этом классе
 */

/** Mockito - фреймворк помогает создать фейковый(mock) объекты допустим хотим тестировать UserService,
 * для этого нужен UserRepository но нам не интересно как работает UserRepository в этом случае можем токовый объект с помощью Mockito
 * Test Doubles(дублёры) - это специальные определение которые пришел на замен настоящего объектa
 * Dummy - который не используется во время тестирование,нужны для заполнения параметров метода
 * например в логин методе нам нужен два параметра USERNAME&PASSWORD и нам неважно какие эти параметры важно их запускать
 * Fake - объекты с работающей похожим функционалом но не подходит для production,для тестировании DAO слой например JOHN  и SARAH
 * Stub - объекты использует mocks и spies для ответа(Answer) на вызова метода во время теста
 * Mock - запрограммированный объект возвращаемый ожидаемый результат(stubs) на вызова определенного метода
 * Spy - proxy для реальных объектов который ведет себя точно также как настоящий,или запрограммированный как mock
 * */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({
        GlobalExtension.class,
        UserServiceParameterResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
})
class UserServiceMockitoMockTest {

    private UserService userService;
    private UserDao userDao;
    private static final User JOHN = User.of(1, "John", "123");
    private static final User SARAH = User.of(2, "Sarah", "111");


    @BeforeAll
    void init() {
        System.out.println("Before All: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before Each " + this);
        this.userDao = Mockito.mock(UserDao.class);
        this.userService = new UserService(userDao);
    }


    @Test
    void shouldDeleteExistedUser() {
        userService.add(JOHN);
        /* возвращается true если передается JOHN.getId() */
//        Mockito.doReturn(true).when(userDao).delete(JOHN.getId());

        /*всегда возвращается true,потому что Mockito.any() - это DUMMY */
//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());

        /* этот подход более читабельный,но в некоторых случаев(для STUB) не всегда работает,
           первый результат возвращает true после для всех вызовов возвращается false */
        Mockito.when(userDao.delete(JOHN.getId()))
                .thenReturn(true)
                .thenReturn(false);

        var deleteResult = userService.delete(JOHN.getId());
        System.out.println(deleteResult);
        System.out.println(userService.delete(JOHN.getId()));
        System.out.println(userService.delete(JOHN.getId()));
        assertThat(deleteResult).isTrue();
    }

    @Test
    void shouldDeleteExistedUserSpySample() {
        userService.add(JOHN);
        Mockito.when(userDao.delete(JOHN.getId()))
                .thenReturn(true)
                .thenReturn(false);

        var deleteResult = userService.delete(JOHN.getId());
        System.out.println(userService.delete(JOHN.getId()));
        System.out.println(userService.delete(JOHN.getId()));
        assertThat(deleteResult).isTrue();
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

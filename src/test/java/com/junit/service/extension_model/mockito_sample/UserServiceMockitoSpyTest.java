package com.junit.service.extension_model.mockito_sample;

import com.junit.dao.UserDao;
import com.junit.dto.User;
import com.junit.extension.ConditionalExtension;
import com.junit.extension.GlobalExtension;
import com.junit.extension.PostProcessingExtension;
import com.junit.extension.UserServiceParameterResolver;
import com.junit.service.UserService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Mockito - фреймворк помогает создать фейковый(mock) объекты допустим хотим тестировать UserService,
 * для этого нужен UserRepository но нам не интересно как работает UserRepository в этом случае можем токовый объект с помощью Mockito
 * Test Doubles(дублёры) - это специальные определение которые пришел на замен настоящего объектa
 * Dummy - который не используется во время тестирование,нужны для заполнения параметров метода
 * например в логин методе нам нужен два параметра USERNAME&PASSWORD и нам неважно какие эти параметры важно их запускать
 * Fake - объекты с работающей похожим функционалом но не подходит для production,для тестировании DAO слой например JOHN  и SARAH
 * Stub - объекты использует mocks и spies для ответа(Answer) на вызова метода во время теста
 * Mock - запрограммированный объект возвращаемый ожидаемый результат(stubs) на вызова определенного метода
 * Spy - proxy для реальных объектов который ведет себя точно также как настоящий,или запрограммированный как mock
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({
        GlobalExtension.class,
        UserServiceParameterResolver.class,
        PostProcessingExtension.class,
        ConditionalExtension.class,
        MockitoExtension.class
})
class UserServiceMockitoSpyTest {

    @InjectMocks
    private UserService userService; // объект вызывающий mock объект,доступно @ExtendWith -> MockitoExtension.class
//    @Mock(strictness = Mock.Strictness.LENIENT)
    @Spy
    private UserDao userDao; // Mockito.mock() alternative,доступно @ExtendWith -> MockitoExtension.class
    private static final User JOHN = User.of(1, "John", "123");
    private static final User SARAH = User.of(2, "Sarah", "111");


    @BeforeAll
    void init() {
        System.out.println("Before All: " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before Each " + this);
//        this.userDao = Mockito.spy(new UserDao());
//        Mockito.lenient().when(userDao.delete(JOHN.getId())).thenReturn(true);
        Mockito.doReturn(true).when(userDao).delete(JOHN.getId());
//        Mockito.doThrow(RuntimeException.class).when(userDao).delete(JOHN.getId());

//        this.userService = new UserService(userDao);
    }

    /*MockitoExtension по умолчанию инициализирует Stub в @BeforeEach фазе,
      у него будет доступ к нему благодаря интерфейсов и ругает если будет тесты не использует эти stub
      ниже метод не использует объявленный stub в @BeforeEach,но его поведение можно изменить,
       решение если юзаем @Spy что внутри теста инициализировать,Если Mock сделать линентом */
    @Test
    void throwIfDatabaseNotAvailable() {
        Mockito.doThrow(RuntimeException.class).when(userDao).delete(JOHN.getId());

        assertThrows(RuntimeException.class,() -> userService.delete(JOHN.getId()));
    }

    @Test
    void shouldDeleteExistedUserSpySample() {
        userService.add(JOHN);

        /*в таком случае будет ошибка потому что наверху создали UserDao как spy а не mock,
        поэтому у нас не будет запрограммированный объект следовательно вызывается реальный объект */
//        Mockito.when(userDao.delete(JOHN.getId()))
//                .thenReturn(true)
//                .thenReturn(false);

        // вызывается внутри when(),т.е создается обертка потом вызывается метод его
//        Mockito.doReturn(true).when(userDao).delete(JOHN.getId());

        var deleteResult = userService.delete(JOHN.getId());
        System.out.println(userService.delete(JOHN.getId()));
        System.out.println(userService.delete(JOHN.getId()));

        // проверяет сколько раз вызван метод минимум на 2
//        Mockito.verify(userDao,Mockito.atLeast(2)).delete(JOHN.getId());

        // проверяет сколько раз вызван метод ровна на 2
//        Mockito.verify(userDao,Mockito.times(2)).delete(JOHN.getId());

        //  если то знаем реальный метод и реальный объект не вызовется можем суда несколько mock передать
//        Mockito.verifyNoInteractions();

        // можем отлавливать переданный значение передав какой тип
        var argumentCaptor = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(userDao, Mockito.times(3)).delete(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).isEqualTo(25);

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

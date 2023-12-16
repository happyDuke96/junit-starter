package com.junit.service.extension_model.mockito_sample;

import com.junit.TestBase;
import com.junit.dto.User;
import com.junit.service.UserService;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lifecycle Test - @BeforeAll -- @BeforeEach -- Test -- @AfterEach -- @AfterAll
 *
 * @BeforeAll - вызывается глобально для всех тестов в этом классе
 * @BeforeEach - вызывается отдельно для каждого класса
 * @AfterEach - вызывается отдельно для каждого класса
 * @AfterAll - вызывается глобально для всех тестов в этом классе
 */
/** Extension Model - это модель пришел взамен @RunWith из старый версии JUnit,который можно изменить/добавить жизненный цикл тестов
 * проблема @RunWith в его трудностей,если мы хотели изменить например часть какой то фазы жизненный цикл свой тест,
 * нам надо было остальных фазы тоже переписать,потом появился @Rule но он смог только изменить определенный фазы,
 * например от @BeforeEach до @AfterEach. Extension Model есть ряд возможностей
 * 1. Test life cycle callbacks - это возможность дает выполнять каких то функциональность в любом этапе жизненный цикла  тестов,
 * даже перед или после @BeforeAll и @AfterAll
 * 2. Test instance post-processing - это возможность дает добавить либо изменить с созданным тестовом классом UserServiceTest
 * этот возможность активно использует Spring в нашем создается один раз для весь жизненный цикл теста потому что Lifecycle.PER_CLASS
 * 3. Conditional test execution - на основе каких либо проверок исключать либо игнорировать тесты
 * 4. Parameter resolution -можно внедрять с помощью интерфейса ParameterResolver реализуя два методов,который принимает на вход
 * ExtensionContext - он же объект жизненный цикла тестов который хранит у себя полный информации о жизненным цикл теста,
 * {getTestMethod,getParent,getUniqueId,getDisplayName,getTags,getExecutionException,getStore}
 * 5. Exception handling - уловить исключение и дальше дополнительно что то делать.
 * @Warn ВАЖНО: для того чтобы внедрять свой или готовый функциональность к жизненную  циклу тестов (добавлять/изменять поведение)
 * классы/интерфейсы должны имплементится от интерфейса Extension который является маркерным и подключить с помощью @ExtendWith
 * и тогда смотрятся как модель жизненного цикла тестов
 * */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@ExtendWith({
//        UserServiceParameterResolver.class
//})
class UserServiceTestExtensionSample extends TestBase {

    private UserService userService;
    private static final User JOHN = User.of(1, "John", "123");
    private static final User SARAH = User.of(2, "Sarah", "111");


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
    void userConvertedToMapById() throws IOException {
        if (true){
            throw new RuntimeException();
        }
        userService.add(JOHN, SARAH);
        Map<Integer, User> users = userService.getAllConvertedMapValue();
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
}

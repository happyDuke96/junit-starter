package com.junit.service;

import com.junit.dto.User;
import com.junit.paramresolver.UserServiceParameterResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

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

        /** @ParameterizedTest - благодаря Dependency Injection(Junit5) можем параметризовать тесты,дать значение для входных параметров,с помощью ArgumentsProvider
         * @see @ArgumentsSource - базовый  интерфейс  -> ArgumentsProvider
         * @NullSource -> NullArgumentsProvider, пареметризует только один параметр с значением null
         * @EmptySource -> EmptyArgumentsProvider, пареметризует только один параметр с пустой строкой
         * @NullAndEmptySource -> объединяет два argument provider @NullSource,@EmptySource
         * @MethodSource -> MethodArgumentsProvider,самый распространенный способ грубо говоря вставляет значение возвращаемого из указанного метода
         *
         */
        @ParameterizedTest
//        @NullSource
//        @EmptySource
//        @NullAndEmptySource
        @MethodSource
        void loginWithParametrizedTest(String username) {
            userService.add(SARAH);

            Optional<User> maybeUser = userService.login(username, null);

            assertTrue(maybeUser.isEmpty());
        }

        @ParameterizedTest(name = "{arguments} nameForEachParamTest1") // by default {index}
        // в этом случае не можем не можем создать статический метод внутри вложенного класса,по этому создаем метод  для аргументов снаружи класса и добавим путь к методу
        @MethodSource("com.junit.service.UserServiceTest#argumentsForParametrizedLoginTest")
        void loginWithParametrizedTest2(String username,String password,Optional<User> user) {
            userService.add(JOHN,SARAH);

            Optional<User> maybeUser = userService.login(username, password);

            org.assertj.core.api.Assertions.assertThat(maybeUser).isEqualTo(user);
        }

        @ParameterizedTest(name = "{arguments} nameForEachParamTest2") // by default {index}
//        @CsvSource({
//                "John","123",
//                "Sarah","111"
//        })  можно без файлов проста в виде строки

        @CsvFileSource(resources = "/login-arguments.csv",delimiter = ',',numLinesToSkip = 1)
        // numLinesToSkip - пропускает первую линию,headers = USERNAME,PASSWORD
        // delimiter - по умолчанию запятая,можно изменить исходя из csv файл
        // resources - путь к csv файл аргументов
        void loginWithParametrizedTest3(String username,String password) {
            userService.add(JOHN,SARAH);

            Optional<User> maybeUser = userService.login(username, password);

            org.assertj.core.api.Assertions.assertThat(maybeUser).isEqualTo(null);
        }
    }

    static Stream<Arguments> argumentsForParametrizedLoginTest(){
        return Stream.of(
                Arguments.of("John","123",Optional.of(JOHN)), // valid argument case
                Arguments.of("Sarah","111",Optional.of(SARAH)), // valid argument case
                Arguments.of("John",null,Optional.empty()), // password not valid argument case
                Arguments.of(null,"111",Optional.empty()) // username not valid argument case
        );
    }


    /** Flaky - означает что тест не стабильный,когда запускаем тесты они могут быть не правильно реализованы например
     * когда не очищаем базы данных после теста и это могут влияют на других тестов,по умолчанию тесты не должны завязывать друг от друга
     * если найдем такой тест который влияет на выполнение других тестов,можем игнорить с помощью аннотаций @Disabled
     * TimeOut - обычно они используется для интеграционных и accepted тестов,потому что унит тесты быстрее запускается и не нуждается для таймаутов
     * @org.junit.jupiter.api.TimeOut - лучше использовать его над классом, потому что внутри метода ассерты больше всего используется  и работает для всех классов
     * */
    @Nested
    @DisplayName("Flaky TimeOut Sample")
//    @Timeout(value = 200,unit = TimeUnit.MILLISECONDS)
    public class FlakyAndTimeOutTestSample {


        @Test
        @Disabled("игнорировали в пользу flaky")
        void failureIfPasswordNotCorrect(){
            userService.add(SARAH);

            Optional<User> maybeUser = userService.login("Sarah", null);

            assertTrue(maybeUser.isEmpty());
        }

        // чтобы  уменьшить количество flaky тестов можем запускать несколько раз указывая количество запусков и названию
        @Test
        @RepeatedTest(value = 4,name = RepeatedTest.LONG_DISPLAY_NAME)
        void testForFindFlakyTest(RepetitionInfo repetitionInfo){
            userService.add(SARAH);

            System.out.println(repetitionInfo.getCurrentRepetition());
            System.out.println(repetitionInfo.getTotalRepetitions());
            Optional<User> maybeUser = userService.login("Sarah", SARAH.getPassword());

            assertFalse(maybeUser.isEmpty());
        }

        @Test
        void testLoginFunctionalityPerformance(){
            var result = assertTimeout(Duration.ofMillis(200L), () -> {
                Thread.sleep(300L); // напоминание здесь не нужно перехватить exception,Executable сам пробрасывает @see method execute()
                return userService.login("Sarah", SARAH.getPassword());
            });
        }

        @Test
        void testLoginFunctionalityPerformance2(){
            System.out.println(Thread.currentThread().getName());
            /* в отличие assertTimeout,assertTimeoutPreemptively запускает Executable в отдельном потоке,допустим мы используем spring и хотим проверять
              базу данных то в этом случае скорее надо юзать assertTimeout потому что в спринге транзакции завязаны в ThreadLocal объект */
            var result = assertTimeoutPreemptively(Duration.ofMillis(200L), () -> {
                System.out.println(Thread.currentThread().getName());
                Thread.sleep(300L); // напоминание здесь не нужно перехватить exception,Executable сам пробрасывает @see method execute()
                return userService.login("Sarah", SARAH.getPassword());
            });
        }
    }

}

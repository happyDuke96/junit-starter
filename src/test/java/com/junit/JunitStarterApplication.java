package com.junit;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.io.PrintWriter;

public class JunitStarterApplication {


    public static void main(String[] args) {
        var launcher = LauncherFactory.create();
//        launcher.registerLauncherDiscoveryListeners();
        var summaryGeneratingListener = new SummaryGeneratingListener();
//        launcher.registerTestExecutionListeners();
        launcher.registerTestExecutionListeners(summaryGeneratingListener);

        var discoveryRequest = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(DiscoverySelectors.selectPackage("com.junit.service"))
//                .listeners()
                .build();

        launcher.execute(discoveryRequest,summaryGeneratingListener);

        try(var writer = new PrintWriter(System.out)) {
            summaryGeneratingListener.getSummary().printTo(writer);
        }
    }


    /** Тестирование ПО - это процесс испытания программы,
     * целью которого является соответствие между ожидаемого и актуального с помощью наборов тестов
     *  Test --> Input --> Application
     *  Application --> Actual output --> Test.
     *  Уровни тестирование
     *  1.Unit testing
     *  2.Integration testing
     *  3.Acceptance testing
     *  */


    /** Unit testing - тестирование маленького компонента(функции),
     * т.е этот unit должен правильно отробатывать изоляции от других компонентов
     * Test --> input --> func, func --> output --> Test
     * */


    /** Integration testing - тестирование нескольких компонентов приложения,
     * т.е как несколько unit тесты работает в месте как один большой unit
     * Test --> input --> func1 --> func2 --> func3, func3 --> func2 --> func1 --> output --> Test
     * */

    /** Acceptance testing - тестирование всего приложения
     * */


    /** JUnit5 - разбить на нескольких основных под проектов(в отличие jUnit 4).
     * JUnit Platform - для работы с jvm  и работать через консоли
     * JUnit Jupiter - набор классов(AssertJ..),работает как API
     * JUnit Vintage - как адаптер,работать с предыдущей версией JUnit4
     * */


}

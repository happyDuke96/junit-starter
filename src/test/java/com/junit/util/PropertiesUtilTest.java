package com.junit.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PropertiesUtilTest {


    @ParameterizedTest
    @MethodSource("getPropertiesArguments")
    void checkGetProperties(String propKey,String expectedPropValue){
        String actualResult = PropertiesUtil.get(propKey);

        assertThat(actualResult).isEqualTo(expectedPropValue);
    }

    static Stream<Arguments> getPropertiesArguments(){
        return Stream.of(
                Arguments.of("db.url","jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"),
                Arguments.of("db.user","test_user"),
                Arguments.of("db.password","")
        );
    }

}
package com.junit.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class LocalDateFormatterTest {


    @Test
    void format() {
        // given
        String date = "2023-12-24";

        // when
        LocalDate localDate = LocalDateFormatter.format(date);

        // then
        assertThat(localDate).isEqualTo(LocalDate.of(2023, 12, 24));
    }

    @Test
    void shouldThrowExceptionIfDateInvalid() {
        String date = "2023-12-24 18:09";

        assertThrows(DateTimeParseException.class, () -> LocalDateFormatter.format(date));

    }


    @DisplayName("Date Valid Param Test")
    @ParameterizedTest(name = "{arguments} validTest")
    @MethodSource("getValidationArguments")
    void isValid(String date,boolean expectedResult){
        boolean actualResult = LocalDateFormatter.isValid(date);

        assertEquals(expectedResult,actualResult);

    }

    static Stream<Arguments> getValidationArguments(){
        return Stream.of(
                Arguments.of("2023-12-24",true), // valid argument case
                Arguments.of("24-12-2023",false),// notValid argument case
                Arguments.of("2023-12-24 18:09",false),// notValid argument case
                Arguments.of(null,false) // notValid argument case
        );
    }

}
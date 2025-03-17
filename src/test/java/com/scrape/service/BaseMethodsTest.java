package com.scrape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseMethodsTest {

    private BaseMethods baseMethods;

    @BeforeEach
    void setUp() {
        baseMethods = new BaseMethods();
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/base-methods/BaseMethodsData.csv", numLinesToSkip = 1)
    void convertTimestampToSeconds(String timestamp, int expectedTimestampInSeconds) {
        int actualTimestampInSeconds = baseMethods.convertTimestampToSeconds(timestamp);

        assertEquals(expectedTimestampInSeconds, actualTimestampInSeconds);
    }
}
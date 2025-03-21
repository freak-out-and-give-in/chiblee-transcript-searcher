package com.scrape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseTest {

    @Mock
    private Base base;

    @BeforeEach
    void setUp() {
        base = new Base();
    }

    @AfterEach
    void tearDown() {
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/base-methods/BaseMethodsData.csv", numLinesToSkip = 1)
    void convertTimestampToSeconds(String timestamp, int expectedTimestampInSeconds) {
        int actualTimestampInSeconds = base.convertTimestampToSeconds(timestamp);

        assertEquals(expectedTimestampInSeconds, actualTimestampInSeconds);
    }
}
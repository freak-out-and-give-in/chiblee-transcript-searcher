package com.scrape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class TranscriptTxtParsingServiceTest {

    @InjectMocks
    private TranscriptTxtParsingService transcriptTxtParsingService;

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-txt-parsing-service/TranscriptTxtParsingServiceData.csv", numLinesToSkip = 1)
    void getTranscriptPathWithFileName(String fileName, String expectedTranscriptPath) {
        String actualTranscriptPath = transcriptTxtParsingService.getTranscriptPathWithFileName(fileName);

        assertEquals(expectedTranscriptPath + fileName, actualTranscriptPath);
    }

    @Nested
    class GetStopWords {

        private List<String> stopWords;

        @BeforeEach
        void setUp() {
            stopWords = transcriptTxtParsingService.getStopWords();
        }

        @AfterEach
        void tearDown() {
            stopWords = null;
        }

        @Test
        void areAnyStopWordsEmpty() {
            // Checks if every stop word is not empty
            assertTrue(stopWords.stream().noneMatch(String::isEmpty));
        }

        @Test
        void areThereManyStopWords() {
            // Checks that the amount of stop words is roughly what is expected
            assertTrue(stopWords.size() > 100);
        }

    }

    @Nested
    class GetIndividualTranscriptFiles {

        private List<File> transcriptFiles;

        @BeforeEach
        void setUp() {
            transcriptFiles = transcriptTxtParsingService.getIndividualTranscriptFiles();
        }

        @AfterEach
        void tearDown() {
            transcriptFiles = null;
        }

        @Test
        void areAnyTranscriptFileNamesEmpty() {
            // Checks if every file name is not empty
            assertTrue(transcriptFiles.stream().noneMatch(file -> file.getName().isEmpty()));
        }

        // This might fail either because:
        // The transcripts aren't downloading properly
        // Or we are in the middle of deleting/updating the transcripts - in which case do not worry
        // that this test has failed
        @Test
        void areThereManyTranscriptFiles() {
            // Checks if there are at least 650 transcript files
            assertTrue(transcriptFiles.size() > 650);
        }

    }

    @Nested
    class GetTranscriptFromEachFile {

        private HashMap<String, LinkedHashMap<String, String>> transcriptMap;

        @BeforeEach
        void setUp() {
            transcriptMap = transcriptTxtParsingService.getTranscriptFromEachFile();
        }

        @AfterEach
        void tearDown() {
            transcriptMap = null;
        }

        @Test
        void areTranscriptTxtTitlesNotEmpty() {
            // Checks if every title is not empty
            assertTrue(transcriptMap.keySet().stream()
                    .noneMatch(titleAndId -> titleAndId.substring(0, titleAndId.length() - 14)
                            .isEmpty()));
        }

        @Test
        void areTranscriptTxtIdsValid() {
            // Checks if every id is valid
            assertTrue(transcriptMap.keySet().stream()
                    .allMatch(titleAndId -> titleAndId.substring(titleAndId.length() - 13)
                            .matches("(\\[[^\"&?/\\s]{11}])")));
        }

        @Test
        void areTranscriptTxtTimestampsValid() {
            // Checks if every timestamp is valid
            assertTrue(transcriptMap.values().stream()
                    .allMatch(linkedMap -> linkedMap.keySet().stream()
                            .allMatch(timestamp -> timestamp.matches("[\\d{2}:]{8}.\\d{3}"))));
        }

        @Test
        void areTranscriptTxtLinesNotEmpty() {
            // Checks if every line of text is not empty
            assertTrue(transcriptMap.values().stream()
                    .allMatch(linkedMap -> linkedMap.values().stream()
                            .noneMatch(String::isEmpty)));
        }

    }
}
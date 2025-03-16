package com.scrape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getTranscriptPathWithFileName() {
        String fileName = "TodayIsTheGreatest";
        String transcriptsPath = transcriptTxtParsingService.getTranscriptPathWithFileName(fileName);

        assertEquals("C:\\Users\\James\\OneDrive\\Documents\\folder\\chiblee videos\\transcripts-dlp\\" + fileName, transcriptsPath);
    }

    @Test
    void getStopWords() {
        List<String> stopWords = transcriptTxtParsingService.getStopWords();

        // Checks if every stop word is not empty
        assertTrue(stopWords.stream().noneMatch(String::isEmpty));
        assertTrue(stopWords.size() > 100);
    }

    @Test
    void getIndividualTranscriptFiles() {
        List<File> transcriptFiles = transcriptTxtParsingService.getIndividualTranscriptFiles();

        // Checks if every file name is not empty
        assertTrue(transcriptFiles.stream().noneMatch(file -> file.getName().isEmpty()));
        assertTrue(transcriptFiles.size() > 650);
    }

    @Test
    void getTranscriptFromEachFile() {
        HashMap<String, LinkedHashMap<String, String>> map = transcriptTxtParsingService.getTranscriptFromEachFile();

        // Checks if every title is not empty
        assertTrue(map.keySet().stream()
                .noneMatch(titleAndId -> titleAndId.substring(0, titleAndId.length() - 14)
                        .isEmpty()));

        // Checks if every id is valid
        assertTrue(map.keySet().stream()
                .allMatch(titleAndId -> titleAndId.substring(titleAndId.length() - 13)
                        .matches("(\\[[^\"&?/\\s]{11}])")));

        // Checks if every timestamp is valid
        assertTrue(map.values().stream()
                .allMatch(linkedMap -> linkedMap.keySet().stream()
                        .allMatch(timestamp -> timestamp.matches("[\\d{2}:]{8}.\\d{3}"))));

        // Checks if every line of text is not empty
        assertTrue(map.values().stream()
                .allMatch(linkedMap -> linkedMap.values().stream()
                        .noneMatch(String::isEmpty)));
    }
}
package com.scrape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranscriptWritingServiceTest {

    @InjectMocks
    private TranscriptWritingService transcriptWritingService;

    @Mock
    private TranscriptService transcriptService;

    @Mock
    private TranscriptTxtParsingService transcriptTxtParsingService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void writeTranscriptsToDatabase() {
        HashMap<String, LinkedHashMap<String, String>> map = new HashMap<>();
        LinkedHashMap<String, String> linkedMap = new LinkedHashMap<>();
        map.put("this is the title of video [abcdefghijk]", linkedMap);

        when(transcriptTxtParsingService.getTranscriptFromEachFile()).thenReturn(map);

        transcriptWritingService.writeTranscriptsToDatabase();

        verify(transcriptService, times(1)).deleteAll();
        verify(transcriptService, times(1)).save(any());
    }
}
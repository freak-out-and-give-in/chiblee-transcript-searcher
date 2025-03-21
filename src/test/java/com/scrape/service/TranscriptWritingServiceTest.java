package com.scrape.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
    void addTranscriptsToDatabase(String title, String id) {
        HashMap<String, LinkedHashMap<String, String>> map = new HashMap<>();
        LinkedHashMap<String, String> linkedMap = new LinkedHashMap<>();
        map.put(title + " [" + id + "]", linkedMap);

        when(transcriptTxtParsingService.getTranscriptFromEachFile()).thenReturn(map);

        transcriptWritingService.addTranscriptsToDatabase();

        verify(transcriptService, times(1)).deleteAll();
        verify(transcriptService, times(1)).addOrUpdate(any());
    }
}
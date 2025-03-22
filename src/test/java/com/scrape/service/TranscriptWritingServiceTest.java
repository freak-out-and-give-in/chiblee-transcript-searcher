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
    @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_TranscriptValid.csv", numLinesToSkip = 1)
    void addTranscriptsToDatabase(String title, String id) {
        // titleAndId, <timestamps,text>
        HashMap<String, LinkedHashMap<String, String>> transcripts = new HashMap<>();
        LinkedHashMap<String, String> timestampsAndTextMap = new LinkedHashMap<>();

        transcripts.put(title + " [" + id + "]", timestampsAndTextMap);

        when(transcriptTxtParsingService.getTranscriptFromEachFile()).thenReturn(transcripts);

        transcriptWritingService.addTranscriptsToDatabase();

        verify(transcriptService, times(1)).addOrUpdate(any());
    }
}
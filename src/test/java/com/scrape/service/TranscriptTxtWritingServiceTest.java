package com.scrape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptTxtWritingServiceTest {

    @InjectMocks
    private TranscriptTxtWritingService transcriptTxtWritingService;

    @Mock
    private TranscriptTxtParsingService transcriptTxtParsingService;

    private HashMap<String, File> fileNameWithFile;

    @BeforeEach
    void setUp() {
        fileNameWithFile = new HashMap<>();

        fileNameWithFile.put("file name1", new File("/path/file1"));
        fileNameWithFile.put("file name2", new File("/path/file2"));
        fileNameWithFile.put("file name3", new File("/path/file3"));
    }

    @AfterEach
    void tearDown() {
        fileNameWithFile = null;
    }

    @Test
    void downloadTranscripts() {
        /*
        when(transcriptTxtParsingService.getIndividualTranscriptFiles()).thenReturn(new ArrayList<>(fileNameWithFile.values()));
        when(transcriptTxtParsingService.getTranscriptPathWithFileName("file name1")).thenReturn(fileNameWithFile.get("file name1").getAbsolutePath());
        when(transcriptTxtParsingService.getTranscriptPathWithFileName("file name2")).thenReturn(fileNameWithFile.get("file name2").getAbsolutePath());
        when(transcriptTxtParsingService.getTranscriptPathWithFileName("file name3")).thenReturn(fileNameWithFile.get("file name3").getAbsolutePath());

        transcriptTxtWritingService.downloadTranscripts();
         */
    }
}
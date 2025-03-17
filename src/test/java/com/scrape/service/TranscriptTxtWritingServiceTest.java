package com.scrape.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
class TranscriptTxtWritingServiceTest {

    @InjectMocks
    private TranscriptTxtWritingService transcriptTxtWritingService;

    @Mock
    private TranscriptTxtParsingService transcriptTxtParsingService;

    private HashMap<String, File> fileNameWithFile;

    @AfterEach
    void tearDown() {
        fileNameWithFile = null;
    }

    private void setUpCreateFileNameWithFileHashMap(String fileName1, String pathName1, String fileName2,
                                               String pathName2, String fileName3, String pathName3) {
        fileNameWithFile = new HashMap<>();

        fileNameWithFile.put(fileName1, new File(pathName1));
        fileNameWithFile.put(fileName2, new File(pathName2));
        fileNameWithFile.put(fileName3, new File(pathName3));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-txt-writing-service/TranscriptTxtWritingServiceData.csv", numLinesToSkip = 1)
    void downloadTranscripts(String fileName1, String pathName1, String fileName2, String pathName2, String fileName3, String pathName3) {
        setUpCreateFileNameWithFileHashMap(fileName1, pathName1, fileName2, pathName2, fileName3, pathName3);

        /*
        when(transcriptTxtParsingService.getIndividualTranscriptFiles()).thenReturn(new ArrayList<>(fileNameWithFile.values()));
        when(transcriptTxtParsingService.getTranscriptPathWithFileName("file name1")).thenReturn(fileNameWithFile.get("file name1").getAbsolutePath());
        when(transcriptTxtParsingService.getTranscriptPathWithFileName("file name2")).thenReturn(fileNameWithFile.get("file name2").getAbsolutePath());
        when(transcriptTxtParsingService.getTranscriptPathWithFileName("file name3")).thenReturn(fileNameWithFile.get("file name3").getAbsolutePath());

        transcriptTxtWritingService.downloadTranscripts();
         */
    }
}
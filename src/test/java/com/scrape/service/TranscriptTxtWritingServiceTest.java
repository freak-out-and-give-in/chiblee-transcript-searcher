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

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TranscriptTxtWritingServiceTest {

    @InjectMocks
    private TranscriptTxtWritingService transcriptTxtWritingService;

    @Mock
    private TranscriptTxtParsingService transcriptTxtParsingService;

    private Base base;

    private HashMap<String, File> fileNameWithFile;

    @BeforeEach
    void setUp() {
        base = new Base();
    }

    @AfterEach
    void tearDown() {
        base = null;
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

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-txt-writing-service/TranscriptTxtWritingServiceData.csv", numLinesToSkip = 1)
    void deleteAllTranscripts(String fileName1, String pathName1, String fileName2, String pathName2, String fileName3, String pathName3) {
        setUpCreateFileNameWithFileHashMap(fileName1, pathName1, fileName2, pathName2, fileName3, pathName3);
        // creates a folder and puts in 2 text files with text in them.
        // use the deleteAllTranscripts() method on the folder,
        // and verify that the resulting amount of txt files is 0
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-txt-writing-service/TranscriptTxtWritingServiceData.csv", numLinesToSkip = 1)
    void clearArchiveFiles(String fileName1, String pathName1, String fileName2, String pathName2, String fileName3, String pathName3) {
        setUpCreateFileNameWithFileHashMap(fileName1, pathName1, fileName2, pathName2, fileName3, pathName3);
        // creates a txt file with information inside it.
        // use clearArchiveFiles() method on the file,
        // and verify that there is then no text inside
    }

    @Test
    void areThereTheSameAmountOfTxtTranscriptsAsIdsInArchive() {
        int amountOfTxtTranscripts = calculateAmountOfTxtTranscripts();
        int amountOfArchiveIds = calculateAmountOfArchiveIds();

        assertEquals(amountOfTxtTranscripts, amountOfArchiveIds);
    }

    private int calculateAmountOfTxtTranscripts() {
        File transcriptFolder = new File(base.getTranscriptsPath());
        return Objects.requireNonNull(transcriptFolder.list()).length;
    }

    private int calculateAmountOfArchiveIds() {
        int lines = 0;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(base.getArchiveFile()));
            while (reader.readLine() != null) {
                lines++;
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }
}
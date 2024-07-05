package com.scrape.service;

import old.transcript.TranscriptGet;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class TranscriptGetTest {

    private TranscriptGet transcriptGet;

    @BeforeEach
    void setUp() {
        transcriptGet = new TranscriptGet();
    }

    @AfterEach
    void tearDown() {
    }

    @DisplayName("Return the correct text and timestamps if they exist, otherwise return the line. From a line containing ")
    @Nested
    class FindTextAndTimestamps {

        @DisplayName("text")
        @Test
        void givenOneLineWithText_whenUsingThisMethod_thenReturnCorrectText() {
            String line = "is this a Norman Rockwell painting oh no";

            List<String> result = transcriptGet.findTextBetweenArrowsAndTimestamp(line);

            assertEquals(line, result.getFirst());
        }

        @DisplayName("arrows, timestamps and align-text")
        @Test
        void givenOneLineWithArrowsTimestampsAndAlignText_whenUsingThisMethod_thenReturnCorrectTextAndTimestamps() {
            String line = "00:00:05.279 --> 00:00:07.970 align:start position:0%";

            List<String> result = transcriptGet.findTextBetweenArrowsAndTimestamp(line);

            assertEquals(line, result.getFirst());
        }

        @DisplayName("text, expletives, arrows and timestamps")
        @Test
        void givenOneLineWithTextArrowsAndTimestamps_whenUsingThisMethod_thenReturnCorrectTextAndTimestamps() {
            String line = "there<00:12:30.600><c> you</c><00:12:30.779><c> go</c><00:12:30.839><c> yeah</c><00:12:31.019><c> " +
                    "wait</c><00:12:31.440><c> wait</c><00:12:31.440><c> [&nbsp;__&nbsp;]</c><00:12:32.600><c> you</c>";

            List<String> result = transcriptGet.findTextBetweenArrowsAndTimestamp(line);
            System.out.println(result);

            assertEquals("there you go yeah wait wait [&nbsp;__&nbsp;] you", result.get(0));
            assertEquals("00:12:30.600", result.get(1));
        }

    }

    @DisplayName("All transcript files should end in .vtt")
    @Test
    void givenNothing_whenGettingTranscriptFiles_thenTheyShouldAllEndInDotVtt() {
        List<File> files = transcriptGet.getFiles();

        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.contains(".vtt")) {
                fail("The file with the name: " + fileName + " does not contain the .vtt extension");
            }
        }
    }

    @DisplayName("Remove all newlines")
    @Test
    void getTranscriptWithoutLineBreaks() {
        HashMap<String, List<String>> transcripts = transcriptGet.getTranscriptWithoutLineBreaks();
        for (List<String> transcript : transcripts.values()) {
            if (transcript.contains("\n")) {
                fail();
            }
        }
    }
}
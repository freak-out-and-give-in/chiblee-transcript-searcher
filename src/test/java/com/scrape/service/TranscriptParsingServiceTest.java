package com.scrape.service;

import com.scrape.dto.TranscriptDto;
import com.scrape.exception.InvalidIdException;
import com.scrape.exception.InvalidTitleException;
import com.scrape.exception.InvalidWordCountException;
import com.scrape.model.Transcript;
import com.scrape.repository.TranscriptRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptParsingServiceTest {

    @InjectMocks
    private TranscriptParsingService transcriptParsingService;

    @Mock
    private TranscriptService transcriptService;

    @Nested
    class PhraseContext {

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_PhraseContextValid.csv",
                numLinesToSkip = 1)
        void givenPhraseContext_whenValidInput_thenReceiveContext(String firstId, String secondId, String thirdId,
                                                                  int timestamp1, int timestamp2, int timestamp3,
                                                                  int surroundingContextTimestamp1,
                                                                  String surroundingContextText1,
                                                                  int surroundingContextTimestamp2,
                                                                  String surroundingContextText2,
                                                                  int surroundingContextTimestamp3,
                                                                  String surroundingContextText3, int wordCount) {
            LinkedHashMap<String, List<Integer>> idAndTimestamps = new LinkedHashMap<>();

            idAndTimestamps.put(firstId, List.of(timestamp1, timestamp2, timestamp3));
            idAndTimestamps.put(secondId, List.of(timestamp1, timestamp2, timestamp3));
            idAndTimestamps.put(thirdId, List.of(timestamp1, timestamp2, timestamp3));

            // This is for simulating the surrounding context
            LinkedHashMap<Integer, String> timestampsAndText = new LinkedHashMap<>();
            timestampsAndText.put(surroundingContextTimestamp1, surroundingContextText1);
            timestampsAndText.put(surroundingContextTimestamp2, surroundingContextText2);
            timestampsAndText.put(surroundingContextTimestamp3, surroundingContextText3);

            when(transcriptService.makeMapOfTimestampsAndText(any())).thenReturn(timestampsAndText);

            List<String> textContext = transcriptParsingService.getPhraseContext(idAndTimestamps, wordCount);
            assertThat(textContext.toString()).contains(
                    surroundingContextText1 + " " + surroundingContextText2 + " " + surroundingContextText3);
        }

        @Test
        void givenPhraseContext_whenEnteringWordCountEqualTo0_thenThrowException() {
            LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<>();

            assertThrows(InvalidWordCountException.class, () ->
                    transcriptParsingService.getPhraseContext(map, 0));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_PhraseContextInvalid.csv",
                numLinesToSkip = 1)
        void givenPhraseContext_whenEnteringWordCountLessThan0_thenThrowException(int lessThan0) {
            LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<>();

            assertThrows(InvalidWordCountException.class, () ->
                    transcriptParsingService.getPhraseContext(map, lessThan0));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_PhraseContextInvalid.csv",
                numLinesToSkip = 1)
        void givenPhraseContext_whenEnteringWordCountGreaterThan100_thenThrowException(int lessThan0,
                                                                                       int greaterThan100) {
            LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<>();

            assertThrows(InvalidWordCountException.class, () ->
                    transcriptParsingService.getPhraseContext(map, greaterThan100));
        }
    }
}
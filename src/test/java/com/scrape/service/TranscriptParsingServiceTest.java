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

    @Mock
    private TranscriptRepository transcriptRepository;

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

    @Nested
    class TranscriptByTitle {

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_TranscriptValid.csv",
                numLinesToSkip = 1)
        void givenTranscriptTitle_whenEnteringUniqueTitle_thenReturnDto(String title, String videoId, int timestamp1,
                                                                        String text1, int timestamp2, String text2) {
            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            map.put(timestamp1, text1);
            map.put(timestamp2, text2);
            TranscriptDto transcriptDto = new TranscriptDto(videoId, map);

            when(transcriptService.getByTitle(title)).thenReturn(List.of(new Transcript()));
            when(transcriptService.getVideoIdByTitle(title)).thenReturn(videoId);
            when(transcriptService.makeMapOfTimestampsAndText(videoId)).thenReturn(map);

            TranscriptDto resultTranscriptDto = transcriptParsingService.getTranscriptIdTimestampsAndTextByTitle(title);

            assertThat(resultTranscriptDto.getId()).isEqualTo(transcriptDto.getId());
            assertThat(resultTranscriptDto.getTimestampsAndText()).isEqualTo(transcriptDto.getTimestampsAndText());

        }

        @Test
        void givenTranscriptTitle_whenEnteringEmptyTitle_thenThrowException() {
            assertThrows(InvalidTitleException.class, () -> transcriptParsingService.getTranscriptIdTimestampsAndTextByTitle(""));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_TranscriptInvalid.csv",
                numLinesToSkip = 1)
        void givenTranscriptTitle_whenEnteringTooLongTitle_thenThrowException(String title) {
            assertThrows(InvalidTitleException.class, () -> transcriptParsingService
                    .getTranscriptIdTimestampsAndTextByTitle(title));
        }
    }

    @Nested
    class TranscriptByVideoId {

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_TranscriptValid.csv",
                numLinesToSkip = 1)
        void givenGetTranscriptByVideoId_whenEnteringVideoId_thenReturnDto(String title, String videoId) {
            TranscriptDto transcriptDto = new TranscriptDto(videoId, new LinkedHashMap<>());

            when(transcriptService.makeMapOfTimestampsAndText(videoId)).thenReturn(transcriptDto.getTimestampsAndText());

            TranscriptDto reusltTranscriptDto = transcriptParsingService.getTranscriptIdTimestampsAndTextByVideoId(videoId);

            assertThat(reusltTranscriptDto.getId()).isEqualTo(transcriptDto.getId());
            assertThat(reusltTranscriptDto.getTimestampsAndText()).isEqualTo(transcriptDto.getTimestampsAndText());
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_TranscriptInvalid.csv",
                numLinesToSkip = 1)
        void givenGetTranscriptByVideoId_whenEnteringInvalidVideoId_thenThrowException(String title, String videoId){
            assertThrows(InvalidIdException.class, () -> transcriptParsingService.getTranscriptIdTimestampsAndTextByVideoId(videoId));
        }
    }
}
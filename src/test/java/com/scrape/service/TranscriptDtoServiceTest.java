package com.scrape.service;

import com.scrape.dto.TranscriptDto;
import com.scrape.exception.InvalidIdException;
import com.scrape.exception.InvalidTitleException;
import com.scrape.model.Transcript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranscriptDtoServiceTest {

    @InjectMocks
    private TranscriptDtoService transcriptDtoService;

    @Mock
    private TranscriptService transcriptService;

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

            TranscriptDto resultTranscriptDto = transcriptDtoService.getTranscriptIdTimestampsAndTextByTitle(title);

            assertThat(resultTranscriptDto.getId()).isEqualTo(transcriptDto.getId());
            assertThat(resultTranscriptDto.getTimestampsAndText()).isEqualTo(transcriptDto.getTimestampsAndText());

        }

        @Test
        void givenTranscriptTitle_whenEnteringEmptyTitle_thenThrowException() {
            assertThrows(InvalidTitleException.class, () -> transcriptDtoService.getTranscriptIdTimestampsAndTextByTitle(""));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_TranscriptInvalid.csv",
                numLinesToSkip = 1)
        void givenTranscriptTitle_whenEnteringTooLongTitle_thenThrowException(String longTitle) {
            assertThrows(InvalidTitleException.class, () -> transcriptDtoService
                    .getTranscriptIdTimestampsAndTextByTitle(longTitle));
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

            TranscriptDto reusltTranscriptDto = transcriptDtoService.getTranscriptIdTimestampsAndTextByVideoId(videoId);

            assertThat(reusltTranscriptDto.getId()).isEqualTo(transcriptDto.getId());
            assertThat(reusltTranscriptDto.getTimestampsAndText()).isEqualTo(transcriptDto.getTimestampsAndText());
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-parsing-service/TranscriptParsingServiceData_TranscriptInvalid.csv",
                numLinesToSkip = 1)
        void givenGetTranscriptByVideoId_whenEnteringInvalidVideoId_thenThrowException(String title, String videoId){
            assertThrows(InvalidIdException.class, () -> transcriptDtoService.getTranscriptIdTimestampsAndTextByVideoId(videoId));
        }
    }
}
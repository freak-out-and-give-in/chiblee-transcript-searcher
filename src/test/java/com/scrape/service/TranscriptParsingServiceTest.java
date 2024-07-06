package com.scrape.service;

import com.scrape.dto.TranscriptDto;
import com.scrape.exception.InvalidIdException;
import com.scrape.exception.InvalidTitleException;
import com.scrape.exception.InvalidWordCountException;
import com.scrape.exception.PrivateException;
import com.scrape.model.Transcript;
import com.scrape.repository.TranscriptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
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

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    class PhraseContext {

        @Test
        void givenPhraseContext_whenValidInput_thenReceiveContext() {
            LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<>();
            String firstId = "2354335";
            String secondId = "1235423";
            String thirdId = "2346787";

            map.put(firstId, List.of(1, 10, 20));
            map.put(secondId, List.of(1, 10, 20));
            map.put(thirdId, List.of(1, 10, 20));

            LinkedHashMap<Integer, String> timestampsText = new LinkedHashMap<>();
            timestampsText.put(12, "through the veins oh how the");
            timestampsText.put(16, "back up to their brains to form");
            timestampsText.put(21, "expressions on their tstudpdif aces");

            when(transcriptService.makeMapOfTimestampsAndText(any())).thenReturn(timestampsText);

            List<String> textContext = transcriptParsingService.getPhraseContext(map, 30);

            assertThat(textContext.toString()).contains("through the veins oh how the back up to their brains to form expressions on their tstudpdif aces");
        }

        @Test
        void givenPhraseContext_whenEnteringWordCountLessThan0_thenThrowException() {
            LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<>();

            assertThrows(InvalidWordCountException.class, () -> transcriptParsingService.getPhraseContext(map, 0));
        }

        @Test
        void givenPhraseContext_whenEnteringWordCountGreaterThan100_thenThrowException() {
            LinkedHashMap<String, List<Integer>> map = new LinkedHashMap<>();

            assertThrows(InvalidWordCountException.class, () -> transcriptParsingService.getPhraseContext(map, 120));
        }
    }

    @Nested
    class TranscriptByTitle {

        @Test
        void givenTranscriptTitle_whenEnteringUniqueTitle_thenReturnDto() {
            String title = " new video yall";
            String videoId = "23423";

            LinkedHashMap<Integer, String> map = new LinkedHashMap<>();
            map.put(3543, "we wont make it home");
            map.put(112, "desolation yes hesitation no");
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

        @Test
        void givenTranscriptTitle_whenEnteringTooLongTitle_thenThrowException() {
            assertThrows(InvalidTitleException.class, () -> transcriptParsingService.getTranscriptIdTimestampsAndTextByTitle(
                    "age of innocence. desolation no. age of innocence. desolation no. age of innocence. desolation no. age of innocence. desolation no. age of " +
                            "innocence. desolation no. age of innocence. desolation no. age of innocence. desolation no. age of innocence. desolation no. "
            ));
        }
    }

    @Nested
    class TranscriptByVideoId {

        @Test
        void givenGetTranscriptByVideoId_whenEnteringVideoId_thenReturnDto() {
            String videoId = "abcdefghijk";
            TranscriptDto transcriptDto = new TranscriptDto(videoId, new LinkedHashMap<>());

            when(transcriptService.makeMapOfTimestampsAndText(videoId)).thenReturn(transcriptDto.getTimestampsAndText());

            TranscriptDto reusltTranscriptDto = transcriptParsingService.getTranscriptIdTimestampsAndTextByVideoId(videoId);

            assertThat(reusltTranscriptDto.getId()).isEqualTo(transcriptDto.getId());
            assertThat(reusltTranscriptDto.getTimestampsAndText()).isEqualTo(transcriptDto.getTimestampsAndText());
        }

        @Test
        void givenGetTranscriptByVideoId_whenEnteringInvalidVideoId_thenThrowException(){
            assertThrows(InvalidIdException.class, () -> transcriptParsingService.getTranscriptIdTimestampsAndTextByVideoId("abc"));
        }
    }
}
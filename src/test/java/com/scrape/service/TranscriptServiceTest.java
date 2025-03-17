package com.scrape.service;

import com.scrape.exception.PrivateException;
import com.scrape.model.Transcript;
import com.scrape.repository.TranscriptRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranscriptServiceTest {

    @InjectMocks
    private TranscriptService transcriptService;

    @Mock
    private TranscriptRepository transcriptRepository;

    private String createTimestampsAndText(String timestamp1, String text1, String timestamp2, String text2) {
        return timestamp1 + text1 + timestamp2 + text2;
    }

    @Nested
    class Save {

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
        void givenTranscript_whenSavingWithValidDetails_thenSave(String videoId, String title, String timestamp1,
                                                                 String text1, String timestamp2, String text2) {
            String timestampsAndText = createTimestampsAndText(timestamp1, text1, timestamp2, text2);
            Transcript transcript = new Transcript(videoId, title, timestampsAndText);

            transcriptService.save(transcript);

            ArgumentCaptor<Transcript> captor = ArgumentCaptor.forClass(Transcript.class);
            verify(transcriptRepository).save(captor.capture());

            assertThat(captor.getValue().getId()).isEqualTo(transcript.getId());
            assertThat(captor.getValue().getVideoId()).isEqualTo(transcript.getVideoId());
            assertThat(captor.getValue().getTitle()).isEqualTo(transcript.getTitle());
            assertThat(captor.getValue().getTimestampsAndText()).isEqualTo(transcript.getTimestampsAndText());
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
        void givenTranscript_whenSavingWithVideoIdEmpty_thenThrowPrivateException(String videoId, String title,
                                                                                  String timestamp1, String text1,
                                                                                  String timestamp2, String text2) {
            String timestampsAndText = createTimestampsAndText(timestamp1, text1, timestamp2, text2);
            Transcript transcript = new Transcript("", title, timestampsAndText);

            assertThrows(PrivateException.class, () -> transcriptService.save(transcript));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
        void givenTranscript_whenSavingWithTitleEmpty_thenThrowPrivateException(String videoId, String title,
                                                                                String timestamp1, String text1,
                                                                                String timestamp2, String text2) {
            String timestampsAndText = createTimestampsAndText(timestamp1, text1, timestamp2, text2);
            Transcript transcript = new Transcript(videoId, "", timestampsAndText);

            assertThrows(PrivateException.class, () -> transcriptService.save(transcript));
        }

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
        void givenTranscript_whenSavingWithTimestampsAndTextEmpty_thenThrowPrivateException(String videoId,
                                                                                            String title) {
            Transcript transcript = new Transcript(videoId, title, "");

            assertThrows(PrivateException.class, () -> transcriptService.save(transcript));
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
    void getByVideoId(String videoId, String title, String timestamp1, String text1, String timestamp2, String text2) {
        String timestampsAndText = createTimestampsAndText(timestamp1, text1, timestamp2, text2);
        Transcript transcript = new Transcript(videoId, title, timestampsAndText);

        when(transcriptRepository.getTranscriptByVideoId(transcript.getVideoId())).thenReturn(transcript);
        Transcript resultTranscript = transcriptService.getByVideoId(transcript.getVideoId());

        assertThat(resultTranscript).isEqualTo(transcript);
        verify(transcriptRepository, times(1)).getTranscriptByVideoId(transcript.getVideoId());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
    void getByTitle(String videoId, String title, String timestamp1, String text1, String timestamp2, String text2) {
        String timestampsAndText = createTimestampsAndText(timestamp1, text1, timestamp2, text2);
        Transcript transcript = new Transcript(videoId, title, timestampsAndText);

        when(transcriptRepository.getTranscriptsByTitle(transcript.getTitle())).thenReturn(List.of(transcript));
        List<Transcript> resultTranscripts = transcriptService.getByTitle(transcript.getTitle());

        assertThat(resultTranscripts.size()).isEqualTo(1);
        assertThat(resultTranscripts.getFirst()).isEqualTo(transcript);
        verify(transcriptRepository, times(1)).getTranscriptsByTitle(transcript.getTitle());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
    void getVideoIdByTitle(String videoId, String title, String timestamp1, String text1, String timestamp2,
                           String text2) {
        String timestampsAndText = createTimestampsAndText(timestamp1, text1, timestamp2, text2);
        Transcript transcript = new Transcript(videoId, title, timestampsAndText);

        when(transcriptRepository.getTranscriptsByTitle(transcript.getTitle())).thenReturn(List.of(transcript));
        String resultVideoId = transcriptService.getVideoIdByTitle(transcript.getTitle());

        assertThat(resultVideoId).isEqualTo(transcript.getVideoId());
        verify(transcriptRepository, times(1)).getTranscriptsByTitle(transcript.getTitle());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/transcript-service/TranscriptServiceData.csv", numLinesToSkip = 1)
    void makeMapOfTimestampsAndText(String videoId, String title, String timestamp1, String text1, String timestamp2,
                                    String text2, String timestampInSeconds1, String timestampInSeconds2) {
        String timestampsAndText = createTimestampsAndText(timestamp1, text1, timestamp2, text2);
        Transcript transcript = new Transcript(videoId, title, timestampsAndText);

        when(transcriptRepository.getTranscriptByVideoId(transcript.getVideoId())).thenReturn(transcript);
        transcriptRepository.save(transcript);

        LinkedHashMap<Integer, String> resultMapOfTimestampsAndText =
                transcriptService.makeMapOfTimestampsAndText(transcript.getVideoId());

        // Removing the ending of #hg from the text
        text1 = text1.substring(0, text1.length() - 3);
        text2 = text2.substring(0, text2.length() - 3);

        assertThat(resultMapOfTimestampsAndText + "").isEqualTo(
                "{" + timestampInSeconds1 + "=" + text1 + ", " + timestampInSeconds2 + "=" + text2 + "}");
    }

    @Test
    void deleteAll() {
        transcriptService.deleteAll();

        verify(transcriptRepository, times(1)).deleteAll();
        verify(transcriptRepository, times(1)).flush();
    }
}
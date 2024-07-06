package com.scrape.service;

import com.scrape.exception.PrivateException;
import com.scrape.model.InvertedIndex;
import com.scrape.model.Transcript;
import com.scrape.repository.TranscriptRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private Transcript transcript;

    @BeforeEach
    void setUp() {
        transcript = new Transcript("23674", "plays knightfall", "00:12:34andtodaywewentforawalk#hg01:14:34truetranssoulrebel#hg");
    }

    @AfterEach
    void tearDown() {
    }

    @Nested
    class Save {

        @Test
        void givenTranscript_whenSavingWithValidDetails_thenSave() {
            transcript = new Transcript("videoid", "plays knightfall", "00:12:34andtodaywewentforawalk#hg01:14:34truetranssoulrebel#hg");

            transcriptService.save(transcript);

            ArgumentCaptor<Transcript> captor = ArgumentCaptor.forClass(Transcript.class);
            verify(transcriptRepository).save(captor.capture());

            assertThat(captor.getValue().getId()).isEqualTo(transcript.getId());
            assertThat(captor.getValue().getVideoId()).isEqualTo(transcript.getVideoId());
            assertThat(captor.getValue().getTitle()).isEqualTo(transcript.getTitle());
            assertThat(captor.getValue().getTimestampsAndText()).isEqualTo(transcript.getTimestampsAndText());
        }

        @Test
        void givenTranscript_whenSavingWithVideoIdEmpty_thenThrowPrivateException() {
            transcript = new Transcript("", "plays knightfall", "00:12:34andtodaywewentforawalk#hg01:14:34truetranssoulrebel#hg");

            assertThrows(PrivateException.class, () -> transcriptService.save(transcript));
        }

        @Test
        void givenTranscript_whenSavingWithTitleEmpty_thenThrowPrivateException() {
            transcript = new Transcript("763551", "", "00:12:34andtodaywewentforawalk#hg01:14:34truetranssoulrebel#hg");

            assertThrows(PrivateException.class, () -> transcriptService.save(transcript));
        }

        @Test
        void givenTranscript_whenSavingWithTimestampsAndTextEmpty_thenThrowPrivateException() {
            transcript = new Transcript("763551", "plays a game only once!!", "");

            assertThrows(PrivateException.class, () -> transcriptService.save(transcript));
        }
    }

    @Test
    void getByVideoId() {
        when(transcriptRepository.getTranscriptByVideoId(transcript.getVideoId())).thenReturn(transcript);
        Transcript resultTranscript = transcriptService.getByVideoId(transcript.getVideoId());

        assertThat(resultTranscript).isEqualTo(transcript);
        verify(transcriptRepository, times(1)).getTranscriptByVideoId(transcript.getVideoId());
    }

    @Test
    void getByTitle() {
        when(transcriptRepository.getTranscriptsByTitle(transcript.getTitle())).thenReturn(List.of(transcript));
        List<Transcript> resultTranscripts = transcriptService.getByTitle(transcript.getTitle());

        assertThat(resultTranscripts.size()).isEqualTo(1);
        assertThat(resultTranscripts.getFirst()).isEqualTo(transcript);
        verify(transcriptRepository, times(1)).getTranscriptsByTitle(transcript.getTitle());
    }

    @Test
    void getVideoIdByTitle() {
        when(transcriptRepository.getTranscriptsByTitle(transcript.getTitle())).thenReturn(List.of(transcript));
        String resultVideoId = transcriptService.getVideoIdByTitle(transcript.getTitle());

        assertThat(resultVideoId).isEqualTo(transcript.getVideoId());
        verify(transcriptRepository, times(1)).getTranscriptsByTitle(transcript.getTitle());
    }

    @Test
    void makeMapOfTimestampsAndText() {
        when(transcriptRepository.getTranscriptByVideoId(transcript.getVideoId())).thenReturn(transcript);
        transcriptRepository.save(transcript);

        LinkedHashMap<Integer, String> resultMapOfTimestampsAndText = transcriptService.makeMapOfTimestampsAndText(transcript.getVideoId());

        assertThat(resultMapOfTimestampsAndText + "").isEqualTo("{754=odaywewentforawalk, 4474=transsoulrebel}");
    }

    @Test
    void deleteAll() {
        transcriptService.deleteAll();

        verify(transcriptRepository, times(1)).deleteAll();
        verify(transcriptRepository, times(1)).flush();
    }
}
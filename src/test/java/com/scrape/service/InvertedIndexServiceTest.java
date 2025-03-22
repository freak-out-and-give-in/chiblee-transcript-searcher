package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import com.scrape.model.InvertedIndex;
import com.scrape.repository.InvertedIndexRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvertedIndexServiceTest {

    @InjectMocks
    private InvertedIndexService invertedIndexService;

    @Mock
    private InvertedIndexRepository invertedIndexRepository;

    private InvertedIndex invertedIndex;

    @BeforeEach
    void setUp() {
        invertedIndex = new InvertedIndex("term", "{id: timestamp}");
    }

    @Test
    void getInvertedIndex() {
        when(invertedIndexRepository.findByTerm("term")).thenReturn(invertedIndex);
        InvertedIndex resultInvertedIndex = invertedIndexService.getInvertedIndex("term");

        assertThat(invertedIndex).isEqualTo(resultInvertedIndex);
        verify(invertedIndexRepository, times(1)).findByTerm("term");
    }

    @Test
    void save() {
        invertedIndexService.save(invertedIndex);

        ArgumentCaptor<InvertedIndex> captor = ArgumentCaptor.forClass(InvertedIndex.class);
        verify(invertedIndexRepository).save(captor.capture());

        assertThat(captor.getValue().getId()).isEqualTo(invertedIndex.getId());
        assertThat(captor.getValue().getTerm()).isEqualTo(invertedIndex.getTerm());
        assertThat(captor.getValue().getVideoIdWithTimestamps()).isEqualTo(invertedIndex.getVideoIdWithTimestamps());
    }

    @Test
    void deleteAll() {
        invertedIndexService.deleteAll();

        verify(invertedIndexRepository, times(1)).deleteAll();
        verify(invertedIndexRepository, times(1)).flush();
    }

}
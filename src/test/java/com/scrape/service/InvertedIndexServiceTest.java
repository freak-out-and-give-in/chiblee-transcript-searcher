package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import com.scrape.model.InvertedIndex;
import com.scrape.repository.InvertedIndexRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvertedIndexServiceTest {

    @InjectMocks
    private InvertedIndexService invertedIndexService;

    @InjectMocks
    private InvertedIndexDtoService invertedIndexDtoService;

    @Mock
    private InvertedIndexRepository invertedIndexRepository;

    private InvertedIndex invertedIndex;

    @BeforeEach
    void setUp() {
        invertedIndex = new InvertedIndex("term", "{id: timestamp}");
    }

    @AfterEach
    void tearDown() {
        invertedIndex = null;
    }

    @Test
    void getInvertedIndexDtosTimestamps() {
        HashMap<String, List<String>> map = new HashMap<>();
        map.put("id1", List.of("{[12: 453, 4531]", "[5555: 67, 78, 9]"));
        map.put("id2", List.of("{[72: 4353, 4531]", "[53: 67, 798, 8]"));
        InvertedIndexDto invertedIndexDto1 = new InvertedIndexDto("bread", map);

        HashMap<String, List<String>> map2 = new HashMap<>();
        map2.put("id3", List.of("{[11: 325, 234]", "[5555: 67]"));
        map2.put("id2", List.of("{[72: 77]", "[53: 67, 798, 8, 67, 99]"));
        InvertedIndexDto invertedIndexDto2 = new InvertedIndexDto("sin", map2);

        List<List<String>> resultTimestamps = invertedIndexDtoService.getInvertedIndexDtosTimestamps(List.of(invertedIndexDto1, invertedIndexDto2), "id2");
        List<List<String>> expectedTimestamps = new ArrayList<>();
        expectedTimestamps.add(List.of("{[72: 4353, 4531]", "[53: 67, 798, 8]"));
        expectedTimestamps.add(List.of("{[72: 77]", "[53: 67, 798, 8, 67, 99]"));

        assertThat(resultTimestamps).isEqualTo(expectedTimestamps);
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
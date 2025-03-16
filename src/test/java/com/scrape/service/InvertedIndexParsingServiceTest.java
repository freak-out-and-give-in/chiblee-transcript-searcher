package com.scrape.service;

import com.scrape.exception.InvalidPhraseException;
import com.scrape.exception.InvalidWordCountException;
import com.scrape.model.InvertedIndex;
import com.scrape.repository.InvertedIndexRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvertedIndexParsingServiceTest {

    @InjectMocks
    private InvertedIndexParsingService invertedIndexParsingService;

    @Mock
    private InvertedIndexService invertedIndexService;

    @Mock
    private InvertedIndexRepository invertedIndexRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findThisPhrase() {
        when(invertedIndexService.getInvertedIndex("im")).thenReturn(new InvertedIndex("im",
                "{mmURWCO_H2I=[01:58:25.599], guOwab__yHU=[00:00:30.759], 2z2WNcl1XlA=[00:11:14.680, 00:27:56.240, 00:37:52.240]}"));
        when(invertedIndexService.getInvertedIndex("the")).thenReturn(new InvertedIndex("the",
                "{mmURWCO_H2I=[01:58:26.000], guOwab__yHU=[00:00:31.284], 2z2WNcl1XlA=[00:11:15.125, 00:27:55.654, 00:37:53.678]}"));
        when(invertedIndexService.getInvertedIndex("joker")).thenReturn(new InvertedIndex("joker",
                "{mmURWCO_H2I=[01:58:24.111], guOwab__yHU=[00:00:32.000], 2z2WNcl1XlA=[00:11:13.333, 00:27:54.784, 00:37:51.532]}"));
        when(invertedIndexService.getInvertedIndex("baby")).thenReturn(new InvertedIndex("baby",
                "{mmURWCO_H2I=[01:58:27.753], guOwab__yHU=[00:00:33.098], 2z2WNcl1XlA=[00:11:16.004, 00:27:53.265, 00:37:50.545, 00:13:32.000]}"));

        List<List<String>> allTimestampsFromSpecificId = new ArrayList<>();
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList("01:58:25.599", "00:00:30.759", "00:11:14.680", "00:27:56.240", "00:37:52.240")));
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList("01:58:26.000", "00:00:31.284", "00:11:15.125", "00:27:55.654", "00:37:53.678")));
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList("01:58:24.111", "00:00:32.000", "00:11:13.333", "00:27:54.784", "00:37:51.532")));
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList("01:58:27.753", "00:00:33.098", "00:11:16.004", "00:27:53.265", "00:37:50.545")));
        when(invertedIndexService.getInvertedIndexDtosTimestamps(any(), any())).thenReturn(allTimestampsFromSpecificId);

        LinkedHashMap<String, List<Integer>> mapOfIdWithTimestampsInSeconds = invertedIndexParsingService.findThisPhrase("im the joker baby");

        assertEquals("{mmURWCO_H2I=[7104], guOwab__yHU=[30], 2z2WNcl1XlA=[673, 1673, 2270]}", mapOfIdWithTimestampsInSeconds.toString());
    }

    @Test
    void givenFindPhrase_whenEnteringEmptyPhrase_thenThrowInvalidPhraseException() {
        assertThrows(InvalidPhraseException.class, () -> invertedIndexParsingService.findThisPhrase(""));
    }

    @Test
    void givenFindPhrase_whenEnteringSingleCharacterPhrase_thenThrowInvalidPhraseException() {
        assertThrows(InvalidPhraseException.class, () -> invertedIndexParsingService.findThisPhrase("b"));
    }

    @Test
    void givenFindPhrase_whenEntering51CharacterPhrase_thenThrowInvalidPhraseException() {
        assertThrows(InvalidPhraseException.class, () -> invertedIndexParsingService.findThisPhrase("hurry up hurry u p imve got the blues hurry up " +
                "hurry u p imve got the blues"));
    }
}
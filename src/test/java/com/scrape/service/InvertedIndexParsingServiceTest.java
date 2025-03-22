package com.scrape.service;

import com.scrape.exception.InvalidPhraseException;
import com.scrape.model.InvertedIndex;
import com.scrape.repository.InvertedIndexRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
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
    private InvertedIndexDtoService invertedIndexDtoService;

    @Mock
    private InvertedIndexRepository invertedIndexRepository;

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-parsing-service/InvertedIndexParsingServiceData_Valid.csv",
            numLinesToSkip = 1)
    void findThisPhrase(String id1, String id2, String id3, String word1, String word2, String word3, String word4,
                        String timestampA1, String timestampA2, String timestampA3, String timestampA4,
                        String timestampA5, String timestampB1, String timestampB2, String timestampB3,
                        String timestampB4, String timestampB5, String timestampC1, String timestampC2,
                        String timestampC3, String timestampC4, String timestampC5, String timestampD1,
                        String timestampD2, String timestampD3, String timestampD4, String timestampD5,
                        String timestampD6, int timestampInSecondsC1, int timestampInSecondsA2,
                        int timestampInSecondsC3, int timestampInSecondsD4, int timestampInSecondsD5) {

        when(invertedIndexService.getInvertedIndex(word1)).thenReturn(new InvertedIndex(word1,
                "{%s=[%s], %s=[%s], %s=[%s, %s, %s]}".formatted(id1, timestampA1, id2, timestampA2, id3, timestampA3,
                        timestampA4, timestampA5)));
        when(invertedIndexService.getInvertedIndex(word2)).thenReturn(new InvertedIndex(word2,
                "{%s=[%s], %s=[%s], %s=[%s, %s, %s]}".formatted(id1, timestampB1, id2, timestampB2, id3,timestampB3,
                        timestampB4, timestampB5)));
        when(invertedIndexService.getInvertedIndex(word3)).thenReturn(new InvertedIndex(word3,
                "{%s=[%s], %s=[%s], %s=[%s, %s, %s]}".formatted(id1, timestampC1, id2, timestampC2, id3, timestampC3,
                        timestampC4, timestampC5)));
        when(invertedIndexService.getInvertedIndex(word4)).thenReturn(new InvertedIndex(word4,
                "{%s=[%s], %s=[%s], %s=[%s, %s, %s, %s]}".formatted(id1, timestampD1, id2, timestampD2, id3,
                        timestampD3, timestampD4, timestampD5, timestampD6)));

        List<List<String>> allTimestampsFromSpecificId = new ArrayList<>();
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList(timestampA1, timestampA2, timestampA3,
                timestampA4, timestampA5)));
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList(timestampB1, timestampB2, timestampB3,
                timestampB4, timestampB5)));
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList(timestampC1, timestampC2, timestampC3,
                timestampC4, timestampC5)));
        allTimestampsFromSpecificId.add(new ArrayList<>(Arrays.asList(timestampD1, timestampD2, timestampD3,
                timestampD4, timestampD5)));
        when(invertedIndexDtoService.calculateTimestamps(any(), any())).thenReturn(allTimestampsFromSpecificId);

        LinkedHashMap<String, List<Integer>> mapOfIdWithTimestampsInSeconds = invertedIndexParsingService
                .findThisPhrase("%s %s %s %s".formatted(word1, word2, word3, word4));

        assertEquals("{%s=[%s], %s=[%s], %s=[%s, %s, %s]}"
                .formatted(id1, timestampInSecondsC1, id2, timestampInSecondsA2, id3, timestampInSecondsC3,
                        timestampInSecondsD4, timestampInSecondsD5), mapOfIdWithTimestampsInSeconds.toString());
    }

    @Test
    void givenFindPhrase_whenEnteringEmptyPhrase_thenThrowInvalidPhraseException() {
        assertThrows(InvalidPhraseException.class, () -> invertedIndexParsingService.findThisPhrase(""));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-parsing-service/InvertedIndexParsingServiceData_Invalid.csv",
            numLinesToSkip = 1)
    void givenFindPhrase_whenEnteringSingleCharacterPhrase_thenThrowInvalidPhraseException(String singleCharacter) {
        assertThrows(InvalidPhraseException.class, () -> invertedIndexParsingService.findThisPhrase(singleCharacter));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-parsing-service/InvertedIndexParsingServiceData_Invalid.csv",
            numLinesToSkip = 1)
    void givenFindPhrase_whenEntering51CharacterPhrase_thenThrowInvalidPhraseException(String singleCharacter,
                                                                                       String phrase) {
        assertThrows(InvalidPhraseException.class, () -> invertedIndexParsingService.findThisPhrase(phrase));
    }
}
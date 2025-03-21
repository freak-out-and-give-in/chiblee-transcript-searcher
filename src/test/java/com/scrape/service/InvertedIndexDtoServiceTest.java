package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InvertedIndexDtoServiceTest {

    @InjectMocks
    private InvertedIndexService invertedIndexService;

    @Mock
    private InvertedIndexDtoService invertedIndexDtoService;

    @BeforeEach
    void setUp() {
        invertedIndexDtoService = new InvertedIndexDtoService(invertedIndexService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void convertInvertedIndexToDto() {
    }

    @Test
    void convertToInvertedIndexDtos() {
    }

    @Test
    void getInvertedIndexDtoWithCommonIds() {
    }

    @Test
    void getInvertedIndexDtosTimestamps() {
    }

    @Test
    void getCommonIdsWithTimestamps() {
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexDtoServiceData.csv", numLinesToSkip = 1)
    void mergeCommonAndUncommonIds(String word1, String word2, String idA, String timestampA1, String idB,
                                   String timestampB1, String idC, String timestampC1, String timestampC2,
                                   String idD, String timestampD1, String idE, String timestampE1) {
        Set<String> inputListOfIds = new HashSet<>(Arrays.asList(idA, idB, idC, idD, idE));

        HashMap<String, List<String>> currentIdsAndTimestamps = new HashMap<>();
        currentIdsAndTimestamps.put(idA, List.of(timestampA1));
        currentIdsAndTimestamps.put(idB, List.of(timestampB1));
        currentIdsAndTimestamps.put(idC, List.of(timestampC1, timestampC2));
        currentIdsAndTimestamps.put(idD, List.of(timestampD1));

        HashMap<String, List<String>> newIdsAndTimestamps = new HashMap<>();
        newIdsAndTimestamps.put(idA, List.of(timestampA1));
        newIdsAndTimestamps.put(idB, List.of(timestampB1));
        newIdsAndTimestamps.put(idC, List.of(timestampC1, timestampC2));
        newIdsAndTimestamps.put(idD, List.of(timestampD1));
        newIdsAndTimestamps.put(idE, List.of(timestampE1));

        InvertedIndexDto currentInvertedIndexDto = new InvertedIndexDto(word1, currentIdsAndTimestamps);
        InvertedIndexDto newInvertedIndexDto = new InvertedIndexDto(word2, newIdsAndTimestamps);

        InvertedIndexDto resultDto = invertedIndexDtoService.mergeCommonAndUncommonIds(currentInvertedIndexDto, newInvertedIndexDto);
        Set<String> resultListOfIds = resultDto.getMapOfIdWithTimestamps().keySet();

        assertEquals(inputListOfIds, resultListOfIds);
    }
}
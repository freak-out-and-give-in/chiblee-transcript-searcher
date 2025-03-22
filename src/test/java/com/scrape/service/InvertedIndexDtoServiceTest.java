package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import com.scrape.exception.InvalidPhraseException;
import com.scrape.model.InvertedIndex;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvertedIndexDtoServiceTest {

    @Mock
    private InvertedIndexService invertedIndexService;

    @InjectMocks
    private InvertedIndexDtoService invertedIndexDtoService;

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexData.csv", numLinesToSkip = 1)
    void convertInvertedIndexToDto(String term, String videoIdWithTimestamps) {
        InvertedIndex invertedIndex = new InvertedIndex(term, videoIdWithTimestamps);
        InvertedIndexDto resultDto = invertedIndexDtoService.convertInvertedIndexToDto(invertedIndex);

        // Checks if the DTOs values and the before values are the same
        assertEquals(term, resultDto.getTerm());
        assertEquals(videoIdWithTimestamps, resultDto.getMapOfIdWithTimestamps().toString());
    }

    @Nested
    class ConvertListOfWordsToInvertedIndexDtos {

        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexData.csv", numLinesToSkip = 1)
        void convertListOfWordsToDtos(String term1, String videoIdWithTimestamps1, String term2,
                                      String videoIdWithTimestamps2) {
            InvertedIndex invertedIndex1 = new InvertedIndex(term1, videoIdWithTimestamps1);
            InvertedIndex invertedIndex2 = new InvertedIndex(term2, videoIdWithTimestamps2);

            when(invertedIndexService.getInvertedIndex(term1)).thenReturn(invertedIndex1);
            when(invertedIndexService.getInvertedIndex(term2)).thenReturn(invertedIndex2);

            List<InvertedIndexDto> listOfInvertedIndexDtos =
                    invertedIndexDtoService.findDtosOfPhrase(term1 + " " + term2);
            InvertedIndexDto invertedIndexDto1 = listOfInvertedIndexDtos.get(0);
            InvertedIndexDto invertedIndexDto2 = listOfInvertedIndexDtos.get(1);

            // Check that the DTOs data matches the inverted indexes
            assertEquals(invertedIndexDto1.getTerm(), invertedIndex1.getTerm());
            assertEquals(invertedIndexDto2.getTerm(), invertedIndex2.getTerm());
            assertEquals(invertedIndexDto1.getMapOfIdWithTimestamps().toString(), invertedIndex1.getVideoIdWithTimestamps());
            assertEquals(invertedIndexDto2.getMapOfIdWithTimestamps().toString(), invertedIndex2.getVideoIdWithTimestamps());
        }

        // If a word passed through does not have an inverted index associated with it
        @ParameterizedTest
        @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexData.csv", numLinesToSkip = 1)
        void convertListOfWordsToDtos_inputWithWordMissingFromDatabase(String term1, String videoIdWithTimestamps1,
                                                                       String term2, String videoIdWithTimestamps2,
                                                                       String term3) {
            InvertedIndex invertedIndex1 = new InvertedIndex(term1, videoIdWithTimestamps1);
            InvertedIndex invertedIndex2 = new InvertedIndex(term2, videoIdWithTimestamps2);

            when(invertedIndexService.getInvertedIndex(term1)).thenReturn(invertedIndex1);
            when(invertedIndexService.getInvertedIndex(term2)).thenReturn(invertedIndex2);

            List<InvertedIndexDto> listOfInvertedIndexDtos =
                    invertedIndexDtoService.findDtosOfPhrase(term1 + " " + term2 + " " + term3);
            InvertedIndexDto invertedIndexDto1 = listOfInvertedIndexDtos.get(0);
            InvertedIndexDto invertedIndexDto2 = listOfInvertedIndexDtos.get(1);

            // Check that the DTOs data matches the inverted indexes
            assertEquals(invertedIndexDto1.getTerm(), invertedIndex1.getTerm());
            assertEquals(invertedIndexDto2.getTerm(), invertedIndex2.getTerm());
            assertEquals(invertedIndexDto1.getMapOfIdWithTimestamps().toString(), invertedIndex1.getVideoIdWithTimestamps());
            assertEquals(invertedIndexDto2.getMapOfIdWithTimestamps().toString(), invertedIndex2.getVideoIdWithTimestamps());
        }

        // If none of the input words are stored in the database
        @Test
        void convertListOfWordsToDtos_invalidPhraseException() {
            assertThrows(InvalidPhraseException.class, () -> invertedIndexDtoService.findDtosOfPhrase("bunny rabbit"));
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexData.csv", numLinesToSkip = 1)
    void calculateInvertedIndexDtoWithTheseIds(String term1, String videoIdWithTimestamps1, String term2,
                                               String videoIdWithTimestamps2, String term3, String idA1, String idA2,
                                               String idA3) {
        InvertedIndexDto invertedIndexDto = invertedIndexDtoService
                .convertInvertedIndexToDto(new InvertedIndex(term1, videoIdWithTimestamps1));
        List<String> listOfSharedIds = new ArrayList<>(List.of(idA1, idA3));

        InvertedIndexDto resultDto = invertedIndexDtoService
                .calculateInvertedIndexDtoWithTheseIds(invertedIndexDto, listOfSharedIds);

        // Check if the resulting DTO has the same term, and the correct ids
        assertEquals(resultDto.getTerm(), invertedIndexDto.getTerm());
        assertEquals(resultDto.getMapOfIdWithTimestamps().keySet().stream().toList(), listOfSharedIds);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexTimestampsData.csv", numLinesToSkip = 1)
    void calculateTimestamps(String term1, String videoIdWithTimestamps1, String term2, String videoIdWithTimestamps2,
                             String idA2, String timestampA1, String timestampA2, String timestampA3, String timestampB1) {
        InvertedIndexDto invertedIndexDto1 = invertedIndexDtoService
                .convertInvertedIndexToDto(new InvertedIndex(term1, videoIdWithTimestamps1));
        InvertedIndexDto invertedIndexDto2 = invertedIndexDtoService
                .convertInvertedIndexToDto(new InvertedIndex(term2, videoIdWithTimestamps2));
        List<InvertedIndexDto> listOfInvertedIndexDtos = List.of(invertedIndexDto1, invertedIndexDto2);

        List<List<String>> resultTimestamps= invertedIndexDtoService.calculateTimestamps(listOfInvertedIndexDtos, idA2);
        List<List<String>> expectedTimestamps = List.of(List.of(timestampA1, timestampA2, timestampA3), List.of(timestampB1));

        assertEquals(expectedTimestamps, resultTimestamps);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexTimestampsData.csv", numLinesToSkip = 1)
    void calculateSharedIdsWithTimestamps(String term1, String videoIdWithTimestamps1, String term2,
                                          String videoIdWithTimestamps2, String idA2, String timestampA1,
                                          String timestampA2, String timestampA3, String timestampB1) {
        InvertedIndexDto invertedIndexDto1 = invertedIndexDtoService
                .convertInvertedIndexToDto(new InvertedIndex(term1, videoIdWithTimestamps1));
        InvertedIndexDto invertedIndexDto2 = invertedIndexDtoService
                .convertInvertedIndexToDto(new InvertedIndex(term2, videoIdWithTimestamps2));
        List<InvertedIndexDto> listOfInvertedIndexDtos = List.of(invertedIndexDto1, invertedIndexDto2);

        List<InvertedIndexDto> resultDtos = invertedIndexDtoService.calculateSharedIdsWithTimestamps(listOfInvertedIndexDtos);

        // Test that the DTOs only contain the same ids
        Set<String> resultDtoKeys1 = resultDtos.get(0).getMapOfIdWithTimestamps().keySet();
        Set<String> resultDtoKeys2= resultDtos.get(1).getMapOfIdWithTimestamps().keySet();

        assertEquals(resultDtoKeys1, resultDtoKeys2);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/csv/service/inverted-index-dto-service/InvertedIndexDtoServiceData.csv", numLinesToSkip = 1)
    void mergeSharedAndNotSharedIds(String word1, String word2, String idA, String timestampA1, String idB,
                                    String timestampB1, String idC, String timestampC1, String timestampC2, String idD,
                                    String timestampD1, String idE, String timestampE1) {
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

        InvertedIndex currentInvertedIndex = new InvertedIndex(word1, currentIdsAndTimestamps.toString());
        InvertedIndex newInvertedIndex = new InvertedIndex(word2, newIdsAndTimestamps.toString());

        InvertedIndexDto resultDto = invertedIndexDtoService.mergeSharedAndNotSharedIds(currentInvertedIndex, newInvertedIndex);
        Set<String> resultListOfIds = resultDto.getMapOfIdWithTimestamps().keySet();

        assertEquals(inputListOfIds, resultListOfIds);
    }
}
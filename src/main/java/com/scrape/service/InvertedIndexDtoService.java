package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import com.scrape.exception.InvalidPhraseException;
import com.scrape.model.InvertedIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvertedIndexDtoService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexService invertedIndexService;

    @Autowired
    public InvertedIndexDtoService(InvertedIndexService invertedIndexService) {
        this.invertedIndexService = invertedIndexService;
    }

    public InvertedIndexDto convertInvertedIndexToDto(InvertedIndex invertedIndex) {
        LinkedHashMap<String, List<String>> mapOfIdWithTimestamps = new LinkedHashMap<>();
        String videoIdWithTimestampsString = invertedIndex.getVideoIdWithTimestamps();

        // Removes start & end squiggly brackets
        videoIdWithTimestampsString = videoIdWithTimestampsString.substring(1, videoIdWithTimestampsString.length() - 1);
        List<String> listOfInvertedIndexIdsAndTimestamps = List.of(videoIdWithTimestampsString.split("], "));

        for (String idAndTimestamps : listOfInvertedIndexIdsAndTimestamps) {
            // Adding on the ending square bracket that's removed by split
            // It's a conditional because the square bracket after the last id & last timestamp is not removed
            if (!idAndTimestamps.endsWith("]")) {
                idAndTimestamps += "]";
            }

            String id = idAndTimestamps.substring(0, 11);
            String timestamps = idAndTimestamps.substring(13, idAndTimestamps.length() - 1);

            List<String> listOfTimestamps = List.of(timestamps.split(", "));
            mapOfIdWithTimestamps.put(id, listOfTimestamps);
        }

        return new InvertedIndexDto(invertedIndex.getTerm(), mapOfIdWithTimestamps);
    }

    public List<InvertedIndexDto> findDtosOfPhrase(String phrase) {
        log.debug("Finding the DTOs of these words: {}", phrase);

        String[] phraseWords = phrase.toLowerCase().trim().split(" ");

        // term, (id(0), timestamps(1+))
        List<InvertedIndexDto> listOfInvertedIndexDtos = new ArrayList<>();

        for (String word : phraseWords) {
            InvertedIndex invertedIndex = invertedIndexService.getInvertedIndex(word);

            // Ignore if the word cannot be found (might be a StopWord)
            if (invertedIndex == null) {
                continue;
            }

            // If the word does exist in our database, then convert it to a DTO and add it to the list
            InvertedIndexDto invertedIndexDto = convertInvertedIndexToDto(invertedIndex);
            listOfInvertedIndexDtos.add(invertedIndexDto);
        }

        if (listOfInvertedIndexDtos.isEmpty()) {
            throw new InvalidPhraseException("The phrase '" + phrase + "' does not contain any words that are in our database");
        }

        return listOfInvertedIndexDtos;
    }

    public InvertedIndexDto calculateInvertedIndexDtoWithTheseIds(InvertedIndexDto invertedIndexDto, List<String> ids) {
        LinkedHashMap<String, List<String>> mapOfIdWithTimestamps = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> map : invertedIndexDto.getMapOfIdWithTimestamps().entrySet()) {
            String id = map.getKey();

            // If the id is in the list
            if (ids.contains(id)) {
                List<String> timestamps = map.getValue();

                mapOfIdWithTimestamps.put(id, timestamps);
            }
        }

        return new InvertedIndexDto(invertedIndexDto.getTerm(), mapOfIdWithTimestamps);
    }

    // Calculates all the timestamps for the id
    public List<List<String>> calculateTimestamps(List<InvertedIndexDto> listOfInvertedIndexDtos, String id) {
        List<List<String>> allTimestamps = new ArrayList<>();

        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            // I don't think a conditional is needed as every inverted index contains the id
            List<String> listOfIterationTimestamps = new ArrayList<>(invertedIndexDto.getMapOfIdWithTimestamps().get(id));
            allTimestamps.add(listOfIterationTimestamps);
        }

        return allTimestamps;
    }

    public List<InvertedIndexDto> calculateSharedIdsWithTimestamps(List<InvertedIndexDto> listOfInvertedIndexDtos) {
        // Calculates the shared ids between the inverted indexes
        List<String> sharedIds = calculateSharedIds(listOfInvertedIndexDtos);
        List<InvertedIndexDto> newListOfInvertedIndexDtos = new ArrayList<>();

        // Each object looped over represents a word
        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            // This operation is so only the shared ids are present in each inverted index
            InvertedIndexDto iteratedInvertedIndexDto = calculateInvertedIndexDtoWithTheseIds(invertedIndexDto, sharedIds);
            newListOfInvertedIndexDtos.add(iteratedInvertedIndexDto);
        }

        return newListOfInvertedIndexDtos;
    }

    private List<String> calculateSharedIds(List<InvertedIndexDto> listOfInvertedIndexDtos) {
        List<List<String>> listOfListOfIds = new ArrayList<>();

        // Fills the list-of ids with all ids
        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            List<String> listOfIds = new ArrayList<>(invertedIndexDto.getMapOfIdWithTimestamps().keySet());
            listOfListOfIds.add(listOfIds);
        }

        // Makes a list with only the ids that appear in every word
        List<String> listOfSharedIds = listOfListOfIds.getFirst();
        for (int i = 1; i < listOfListOfIds.size(); i++) {
            listOfSharedIds.retainAll(listOfListOfIds.get(i));
        }

        return listOfSharedIds;
    }

    public InvertedIndexDto mergeSharedAndNotSharedIds(InvertedIndex currentInvertedIndex,
                                                       InvertedIndex newInvertedIndex) {
        // Convert the inverted indexes to DTOs
        InvertedIndexDto currentInvertedIndexDto = convertInvertedIndexToDto(currentInvertedIndex);
        InvertedIndexDto newInvertedIndexDto = convertInvertedIndexToDto(newInvertedIndex);

        // Leaves the newInvertedIndexDto with only the unique ids between the new and current dto
        newInvertedIndexDto.getMapOfIdWithTimestamps().keySet()
                .removeAll(currentInvertedIndexDto.getMapOfIdWithTimestamps().keySet());

        // Add these new ids to the current dto
        for (String id : newInvertedIndexDto.getMapOfIdWithTimestamps().keySet()) {
            List<String> timestamps = newInvertedIndexDto.getMapOfIdWithTimestamps().get(id);
            currentInvertedIndexDto.getMapOfIdWithTimestamps().put(id, timestamps);
        }

        return currentInvertedIndexDto;
    }

}

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

    // Converts a HashMap.toString() to a list
    // e.g. {s0sKAjaPHu8=[03:52:31.040], p4oGmEGaAew=[03:00:11.680, 03:14:02.399, 03:14:05.200], ebM8fywbYbQ=[00:22:06.059]}
    public InvertedIndexDto convertInvertedIndexToDto(InvertedIndex invertedIndex) {
        LinkedHashMap<String, List<String>> mapOfIdWithTimestamps = new LinkedHashMap<>();
        String videoIdWithTimestampsString = invertedIndex.getVideoIdWithTimestamps();

        // Removes start & end squiggly brackets
        videoIdWithTimestampsString = videoIdWithTimestampsString.substring(1, videoIdWithTimestampsString.length() - 1);
        List<String> listOfInvertedIndexIdsAndTimestamps = List.of(videoIdWithTimestampsString.split("], "));

        for (String idAndTimestamps : listOfInvertedIndexIdsAndTimestamps) {
            // Adding on the ending square bracket that's removed by split
            idAndTimestamps += "]";
            String id = idAndTimestamps.substring(0, 11);
            String timestamps = idAndTimestamps.substring(13, idAndTimestamps.length() - 1);

            List<String> listOfTimestamps = List.of(timestamps.split(", "));
            mapOfIdWithTimestamps.put(id, listOfTimestamps);
        }

        return new InvertedIndexDto(invertedIndex.getTerm(), mapOfIdWithTimestamps);
    }

    public List<InvertedIndexDto> convertToInvertedIndexDtos(List<String> listOfWordsNotRemoved, String[] arrayOfWords,
                                                             String phrase) {
        // term, (id(0), timestamps(1+))
        List<InvertedIndexDto> listOfInvertedIndexDtos = new ArrayList<>();

        for (String tempWord : arrayOfWords) {
            InvertedIndex invertedIndex = invertedIndexService.getInvertedIndex(tempWord);

            // Word cannot be found (might be a StopWord)
            if (invertedIndex == null) {
                listOfWordsNotRemoved.remove(tempWord);
                continue;
            }

            InvertedIndexDto invertedIndexDto = convertInvertedIndexToDto(invertedIndex);
            listOfInvertedIndexDtos.add(invertedIndexDto);
        }

        if (listOfInvertedIndexDtos.isEmpty()) {
            throw new InvalidPhraseException("The phrase " + phrase + " does not contain any words that are in our database");
        }

        return listOfInvertedIndexDtos;
    }

    public InvertedIndexDto getInvertedIndexDtoWithCommonIds(InvertedIndexDto invertedIndexDto, List<String> commonIds) {
        LinkedHashMap<String, List<String>> mapOfIdWithTimestamps = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> map : invertedIndexDto.getMapOfIdWithTimestamps().entrySet()) {
            String id = map.getKey();

            // If the id is a common id
            if (commonIds.contains(id)) {
                List<String> timestamps = map.getValue();

                mapOfIdWithTimestamps.put(id, timestamps);
            }
        }

        return new InvertedIndexDto(invertedIndexDto.getTerm(), mapOfIdWithTimestamps);
    }

    public List<List<String>> getInvertedIndexDtosTimestamps(List<InvertedIndexDto> listOfInvertedIndexDtos, String id) {
        List<List<String>> allTimestamps = new ArrayList<>();

        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            List<String> timestamps = new ArrayList<>(invertedIndexDto.getMapOfIdWithTimestamps().get(id));
            allTimestamps.add(timestamps);
        }

        return allTimestamps;
    }

    public List<InvertedIndexDto> getCommonIdsWithTimestamps(List<InvertedIndexDto> listOfInvertedIndexDtos) {
        List<String> commonIds = getCommonIds(listOfInvertedIndexDtos);
        List<InvertedIndexDto> newListOfInvertedIndexDtos = new ArrayList<>();

        // Each map looped over represents a word
        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            InvertedIndexDto newInvertedIndexDto = getInvertedIndexDtoWithCommonIds(invertedIndexDto, commonIds);
            newListOfInvertedIndexDtos.add(newInvertedIndexDto);
        }

        return newListOfInvertedIndexDtos;
    }

    private List<String> getCommonIds(List<InvertedIndexDto> listOfInvertedIndexDtos) {
        List<List<String>> listOfListOfIds = new ArrayList<>();

        // Fills the list-of ids with all ids
        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            List<String> listOfIds = new ArrayList<>(invertedIndexDto.getMapOfIdWithTimestamps().keySet());
            listOfListOfIds.add(listOfIds);
        }

        // Makes a list with only the ids that appear in every word
        List<String> listOfCommonIds = listOfListOfIds.getFirst();
        for (int i = 1; i < listOfListOfIds.size(); i++) {
            listOfCommonIds.retainAll(listOfListOfIds.get(i));
        }

        return listOfCommonIds;
    }

    public InvertedIndexDto mergeCommonAndUncommonIds(InvertedIndexDto currentInvertedIndexDto,
                                                      InvertedIndexDto newInvertedIndexDto) {
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

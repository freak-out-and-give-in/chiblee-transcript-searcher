package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import com.scrape.exception.InvalidPhraseException;
import com.scrape.model.InvertedIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InvertedIndexParsingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexService invertedIndexService;

    @Autowired
    public InvertedIndexParsingService(InvertedIndexService invertedIndexService) {
        this.invertedIndexService = invertedIndexService;
    }

    public LinkedHashMap<String, List<Integer>> findThisPhrase(String phrase) {
        validateInput(phrase);

        return searchForPhrase(phrase);
    }

    private void validateInput(String phrase) {
        log.debug("Validating input for the phrase {}", phrase);

        if (phrase.isEmpty()) {
            throw new InvalidPhraseException("The phrase should be not empty");
        }

        if (phrase.length() == 1) {
            throw new InvalidPhraseException("The phrase should be at least 2 characters long");
        }

        if (phrase.length() > 50) {
            throw new InvalidPhraseException("The phrase should be less than 51 characters long");
        }

        // If the phrase isn't alphanumeric
        if (!phrase.matches("[A-Za-z0-9 ]+")) {
            throw new InvalidPhraseException("The phrase should only contain alphanumeric characters");
        }
    }

    private LinkedHashMap<String, List<Integer>> searchForPhrase(String phrase) {
        log.info("Searching for the phrase {}", phrase);

        String[] arrayOfWords = phrase.toLowerCase().trim().split(" ");
        List<String> listOfWordsNotRemoved = new ArrayList<>(Arrays.asList(arrayOfWords));

        List<InvertedIndexDto> listOfInvertedIndexDtos = convertToInvertedIndexDtos(listOfWordsNotRemoved, arrayOfWords, phrase);
        List<InvertedIndexDto> commonIdsWithTimestamps = getCommonIdsWithTimestamps(listOfInvertedIndexDtos);
        LinkedHashMap<String, List<String>> idsAndTimestamps = filterInvertedIndexForCloseTimestamps(commonIdsWithTimestamps);

        return convertTimestampsToSeconds(idsAndTimestamps);
    }

    private List<InvertedIndexDto> convertToInvertedIndexDtos(List<String> listOfWordsNotRemoved, String[] arrayOfWords, String phrase) {
        log.debug("Converting inverted indexes into a map");

        // term, (id(0), timestamps(1+))
        List<InvertedIndexDto> listOfInvertedIndexDtos = new ArrayList<>();
        for (String tempWord : arrayOfWords) {
            InvertedIndex invertedIndex = invertedIndexService.getInvertedIndex(tempWord);

            // Word cannot be found (might be a StopWord)
            if (invertedIndex == null) {
                listOfWordsNotRemoved.remove(tempWord);
                continue;
            }

            InvertedIndexDto invertedIndexDto = new InvertedIndexDto(tempWord, convertInvertedIndexStringsToMap(invertedIndex.getVideoIdWithTimestamps()));
            listOfInvertedIndexDtos.add(invertedIndexDto);
        }

        if (listOfInvertedIndexDtos.isEmpty()) {
            throw new InvalidPhraseException("The phrase " + phrase + " does not contain any words that are in our database");
        }

        return listOfInvertedIndexDtos;
    }

    // Converts a HashMap.toString() to a list
    // e.g. {s0sKAjaPHu8=[03:52:31.040], p4oGmEGaAew=[03:00:11.680, 03:14:02.399, 03:14:05.200], ebM8fywbYbQ=[00:22:06.059]}
    private LinkedHashMap<String, List<String>> convertInvertedIndexStringsToMap(String invertedIndexString) {
        LinkedHashMap<String, List<String>> mapOfIdWithTimestamps = new LinkedHashMap<>();

        // Removes start & end squiggly brackets
        invertedIndexString = invertedIndexString.substring(1, invertedIndexString.length() - 1);
        List<String> listOfInvertedIndexString = List.of(invertedIndexString.split("], "));

        for (String tempInvertedIndex : listOfInvertedIndexString) {
            // Adding on the ending square bracket that's removed by split
            tempInvertedIndex += "]";
            String id = tempInvertedIndex.substring(0, 11);
            String timestamps = tempInvertedIndex.substring(13, tempInvertedIndex.length() - 1);

            List<String> listOfTimestamps = List.of(timestamps.split(", "));
            mapOfIdWithTimestamps.put(id, listOfTimestamps);
        }


        log.debug("Converted the string {}, to a map: {}", invertedIndexString, mapOfIdWithTimestamps);

        return mapOfIdWithTimestamps;
    }

    private List<String> splitTimestamps(String timestamps) {
        log.debug("Splitting the timestamps {}", timestamps);

        // Removes start & end square brackets
        timestamps = timestamps.substring(1, timestamps.length() - 1);

        if (timestamps.contains(", ")) {
            return new ArrayList<>(Arrays.asList(timestamps.split(", ")));
        } else {
            return new ArrayList<>(List.of(timestamps));
        }
    }

    private LinkedHashMap<String, List<String>> filterInvertedIndexForCloseTimestamps(List<InvertedIndexDto> listOfInvertedIndexDtos) {
        log.debug("Filtering common ids with timestamps so only the timestamps close to each other are recognised as a part of a phrase");

        InvertedIndexDto firstInvertedIndexDto = listOfInvertedIndexDtos.getFirst();
        List<String> listOfIds = firstInvertedIndexDto.getMapOfIdWithTimestamps().keySet().stream().toList();

        LinkedHashMap<String, List<String>> idsAndTimestampsForThePhrase = new LinkedHashMap<>();
        // Loops over the ids (every word has the same ids due to previous processing)
        for (String currentId : listOfIds) {
            List<String> firstTimestamps = firstInvertedIndexDto.getMapOfIdWithTimestamps().get(currentId);
            List<List<String>> allTimestampsFromSpecificId = new ArrayList<>(invertedIndexService.getInvertedIndexDtosTimestamps(listOfInvertedIndexDtos, currentId));

            // Loops over first II object.get(id)
            for (String firstTimestamp : firstTimestamps) {
                List<String> listOfTimestamps = new ArrayList<>();
                listOfTimestamps.add(firstTimestamp);

                // Loops over the words
                for (int w = 0; w < listOfInvertedIndexDtos.size(); w++) {
                    boolean matchFlag = false;

                    // Skips the first word as we are already using it as our base word
                    if (w == 0) {
                        continue;
                    }

                    // Loops over timestamps
                    for (int t = 0; t < allTimestampsFromSpecificId.get(w).size(); t++) {
                        String timestampAsString = allTimestampsFromSpecificId.get(w).get(t);

                        // If first and this timestamp are close then add to list,
                        // will then check subsequent words after this
                        if (areTheseTimestampsClose(firstTimestamp, timestampAsString)) {
                            matchFlag = true;
                            listOfTimestamps.add(timestampAsString);

                            break;
                        }
                    }

                    // If a timestamp was found to not be close to the first timestamp,
                    // then the first timestamp does not have a match.
                    // We will then move on to the next timestamp of the first II object
                    if (!matchFlag) {
                        listOfTimestamps.clear();
                        break;
                    }
                }

                // If the list of timestamps is full, then a close timestamp was found in all the word's same id
                if (listOfTimestamps.size() == listOfInvertedIndexDtos.size()) {
                    // Adding the id and the earliest timestamp to the data structure we will return
                    String earliestTimestamp = getEarliestTimestamp(listOfTimestamps);

                    // If current id already has a timestamp of the phrase
                    if (idsAndTimestampsForThePhrase.containsKey(currentId)) {
                        List<String> timestamps = new ArrayList<>(idsAndTimestampsForThePhrase.get(currentId));
                        timestamps.add(earliestTimestamp);

                        idsAndTimestampsForThePhrase.put(currentId, timestamps);
                    } else {
                        idsAndTimestampsForThePhrase.put(currentId, List.of(earliestTimestamp));
                    }

                    // Removing the added timestamps, so they aren't added again
                    // Starts at 1 because there is no need to remove as it's a part of the first II object,
                    // and we won't go back over it.
                    for (int b = 1; b < listOfTimestamps.size(); b++) {
                        String timestampAtIndex = listOfTimestamps.get(b);
                        allTimestampsFromSpecificId.get(b).remove(timestampAtIndex);
                    }
                }
            }
        }

        return idsAndTimestampsForThePhrase;
    }

    private String getEarliestTimestamp(List<String> timestamps) {
        String earliestTimestamp = timestamps.getFirst();
        int earliestTimestampAsInteger = convertTimestampToInteger(timestamps.getFirst());

        for (int i = 1; i < timestamps.size(); i++) {
            int thisTimestampAsInteger = convertTimestampToInteger(timestamps.get(i));
            if (thisTimestampAsInteger < earliestTimestampAsInteger) {
                earliestTimestamp = timestamps.get(i);
                earliestTimestampAsInteger = thisTimestampAsInteger;
            }
        }

        return earliestTimestamp;
    }

    private boolean areTheseTimestampsClose(String timestamp1, String timestamp2) {
        int secondsDefinedAsClose = 6;

        int intTimestamp1 = convertTimestampToInteger(timestamp1);
        int intTimestamp2 = convertTimestampToInteger(timestamp2);

        //  sdac <= x <= sdac
        return (intTimestamp1 - intTimestamp2 >= -secondsDefinedAsClose) && (intTimestamp1 - intTimestamp2 <= secondsDefinedAsClose);
    }

    // e.g. input = 03:52:31.040, output = 35231
    private int convertTimestampToInteger(String timestamp) {
        // Removes the .milliseconds
        timestamp = timestamp.substring(0, 8);
        return Integer.parseInt(timestamp.replace(":", ""));
    }

    private List<InvertedIndexDto> getCommonIdsWithTimestamps(List<InvertedIndexDto> listOfInvertedIndexDtos) {
        log.debug("Finding the common ids with their timestamps");

        List<String> commonIds = getCommonIds(listOfInvertedIndexDtos);
        List<InvertedIndexDto> newListOfInvertedIndexDtos = new ArrayList<>();

        // Each map looped over represents a word
        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            InvertedIndexDto newInvertedIndexDto = getInvertedIndexDtoWithCommonIds(invertedIndexDto, commonIds);
            newListOfInvertedIndexDtos.add(newInvertedIndexDto);
        }

        return newListOfInvertedIndexDtos;
    }

    private InvertedIndexDto getInvertedIndexDtoWithCommonIds(InvertedIndexDto invertedIndexDto, List<String> commonIds) {
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

    private LinkedHashMap<String, List<Integer>> convertTimestampsToSeconds(LinkedHashMap<String, List<String>> idsAndTimestamps) {
        LinkedHashMap<String, List<Integer>> mapWithTimestampSeconds = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : idsAndTimestamps.entrySet()) {
            List<Integer> seconds = convertTimestampToSeconds(entry.getValue());
            mapWithTimestampSeconds.put(entry.getKey(), seconds);
        }

        return mapWithTimestampSeconds;
    }

    // e.g. 03:52:31.040 -> 13951
    private List<Integer> convertTimestampToSeconds(List<String> timestamps) {
        List<Integer> listOfTimestamps = new ArrayList<>();
        for (String timestamp : timestamps) {
            timestamp = timestamp.substring(0, 8);

            listOfTimestamps.add(LocalTime.parse(timestamp, DateTimeFormatter.ofPattern("HH:mm:ss")).toSecondOfDay());
        }

        return listOfTimestamps;
    }
}

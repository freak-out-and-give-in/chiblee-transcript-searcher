package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import com.scrape.exception.InvalidPhraseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvertedIndexParsingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexDtoService invertedIndexDtoService;

    private Base base;

    @Autowired
    public InvertedIndexParsingService(InvertedIndexDtoService invertedIndexDtoService) {
        this.invertedIndexDtoService = invertedIndexDtoService;
        base = new Base();
    }

    public LinkedHashMap<String, List<Integer>> findThisPhrase(String phrase) {
        validatePhraseInput(phrase);

        return searchForPhrase(phrase);
    }

    private void validatePhraseInput(String phrase) {
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

        List<InvertedIndexDto> listOfInvertedIndexDtos = invertedIndexDtoService.findDtosOfPhrase(phrase);
        List<InvertedIndexDto> sharedIdsWithTimestamps = invertedIndexDtoService.calculateSharedIdsWithTimestamps(listOfInvertedIndexDtos);
        LinkedHashMap<String, List<String>> idsAndTimestamps = filterInvertedIndexForCloseTimestamps(sharedIdsWithTimestamps);

        return convertTimestampsToSecondsAsMap(idsAndTimestamps);
    }

    private LinkedHashMap<String, List<String>> filterInvertedIndexForCloseTimestamps(List<InvertedIndexDto> sharedIdsWithTimestamps) {
        log.debug("Filtering shared ids with timestamps so only the timestamps close to each other are recognised as a part of a phrase");

        InvertedIndexDto firstInvertedIndexDto = sharedIdsWithTimestamps.getFirst();
        List<String> listOfIds = firstInvertedIndexDto.getMapOfIdWithTimestamps().keySet().stream().toList();

        LinkedHashMap<String, List<String>> idsAndTimestampsForThePhrase = new LinkedHashMap<>();
        // Loops over the ids (every word has the same ids due to previous processing)
        for (String currentId : listOfIds) {
            List<String> firstTimestamps = firstInvertedIndexDto.getMapOfIdWithTimestamps().get(currentId);
            List<List<String>> allTimestampsFromSpecificId = new ArrayList<>(invertedIndexDtoService
                    .calculateTimestamps(sharedIdsWithTimestamps, currentId));

            // Loops over first II object.get(id)
            for (String firstTimestamp : firstTimestamps) {
                List<String> listOfTimestamps = new ArrayList<>();
                listOfTimestamps.add(firstTimestamp);

                // Loops over the words
                for (int w = 0; w < sharedIdsWithTimestamps.size(); w++) {
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
                if (listOfTimestamps.size() == sharedIdsWithTimestamps.size()) {
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

    private LinkedHashMap<String, List<Integer>> convertTimestampsToSecondsAsMap(LinkedHashMap<String, List<String>> idsAndTimestamps) {
        LinkedHashMap<String, List<Integer>> mapWithTimestampSeconds = new LinkedHashMap<>();

        for (Map.Entry<String, List<String>> entry : idsAndTimestamps.entrySet()) {
            List<Integer> seconds = convertIdsAndTimestampsToSeconds(entry.getValue());
            mapWithTimestampSeconds.put(entry.getKey(), seconds);
        }

        return mapWithTimestampSeconds;
    }

    public List<Integer> convertIdsAndTimestampsToSeconds(List<String> idsAndTimestamps) {
        List<Integer> listOfTimestamps = new ArrayList<>();
        for (String idAndTimestamp : idsAndTimestamps) {
            String timestamp = idAndTimestamp.substring(0, 12);

            listOfTimestamps.add(base.convertTimestampToSeconds(timestamp));
        }

        return listOfTimestamps;
    }
}

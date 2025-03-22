package com.scrape.service;

import com.scrape.exception.InvalidWordCountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class TranscriptParsingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptService transcriptService;

    @Autowired
    public TranscriptParsingService(TranscriptService transcriptService) {
        this.transcriptService = transcriptService;
    }

    public List<String> getPhraseContext(LinkedHashMap<String, List<Integer>> idAndTimestamps, int wordCount) {
        log.debug("Getting the phrase's context");

        validateWordCount(wordCount);

        List<String> textContext = new ArrayList<>();
        for (String videoId : idAndTimestamps.keySet()) {
            LinkedHashMap<Integer, String> mapOfTimestampsAndText = transcriptService
                    .makeMapOfTimestampsAndText(videoId);

            for (int timestampInSeconds : idAndTimestamps.get(videoId)) {
                String text = buildTextFromTimestamp(mapOfTimestampsAndText, timestampInSeconds, wordCount);

                textContext.add(text);
            }
        }

        return textContext;
    }

    private void validateWordCount(int wordCount) {
        if (wordCount <= 0) {
            throw new InvalidWordCountException("The word count should be greater than 0");
        }

        if (wordCount > 100) {
            throw new InvalidWordCountException("The word count should be less than 101");
        }
    }

    private String buildTextFromTimestamp(LinkedHashMap<Integer, String> mapOfTimestampsAndText,
                                          int startingTimestampInSeconds, int wordCount) {
        log.debug("Building text from a timestamp");

        StringBuilder sb = new StringBuilder();
        String startingText = mapOfTimestampsAndText.get(startingTimestampInSeconds);
        sb.append(startingText);

        // Loops through seconds
        for (int s = 1; s < 20; s++) {
            String sbString = sb.toString().trim();

            // If the string has exactly enough words
            sbString = makeTextMatchWordCountIfGreater(sbString, startingText, wordCount);

            // If the string has exactly enough words
            if (getWordCount(sbString) == wordCount) {
                return sbString.trim();
            }


            // This tries to find close text by guessing.
            // It does this by slowly adding and subtracting the starting text

            // Appends the text that is s seconds ahead of the starting text
            String upperText = mapOfTimestampsAndText.get(startingTimestampInSeconds + s);
            if (upperText != null) {
                sb.append(" ").append(upperText);
            }

            // Appends the text that is s seconds behind the starting text
            String lowerText = mapOfTimestampsAndText.get(startingTimestampInSeconds - s);
            if (lowerText != null) {
                // Inserts text before the starting text
                sb.insert(0, lowerText + " ");
            }
        }

        return sb.toString().trim();
    }

    private int getWordCount(String text) {
        text = text.trim();

        if (text.isEmpty())
            return 0;

        return text.split("\\s+").length; // separate string around spaces
    }

    private String makeTextMatchWordCountIfGreater(String sbString, String startingText, int wordCount) {
        while (getWordCount(sbString) > wordCount) {
            int indexOfStartingText = sbString.indexOf(startingText);

            int beforeStartingTextWordCount = getWordCount(sbString.substring(0, indexOfStartingText));
            int afterStartingTextWordCount = getWordCount(sbString.substring(
                    indexOfStartingText + startingText.length()));

            if (beforeStartingTextWordCount > afterStartingTextWordCount) {
                // If there are more words before the starting text than after
                // Then remove a word that comes before
                int firstIndexOfSpace = sbString.indexOf(" ");
                sbString = sbString.substring(firstIndexOfSpace).trim();
            } else {
                // If there are more words after the starting text than before
                // Then remove a word that comes after
                // OR
                // There are an equal amount of words after/before the starting text
                // Remove one, lets say the last word

                int lastIndexOfSpace = sbString.length() - new StringBuilder(sbString).reverse().indexOf(" ");
                sbString = sbString.substring(0, lastIndexOfSpace).trim();
            }
        }

        return sbString;
    }
}

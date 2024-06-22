package com.scrape.service;

import com.scrape.exception.InvalidPhraseException;
import com.scrape.model.Transcript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class DatabaseParsingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TimestampsAndTextService timestampsAndTextService;

    private TranscriptService transcriptService;

    @Autowired
    public DatabaseParsingService(TimestampsAndTextService timestampsAndTextService, TranscriptService transcriptService) {
        this.timestampsAndTextService = timestampsAndTextService;
        this.transcriptService = transcriptService;
    }

    public HashMap<String, HashMap<String, String>> findThisPhrase(String phrase, int wordCountEachSide)  {
        wordCountEachSide = validateWordCount(wordCountEachSide);
        phrase = validatePhrase(phrase);

        log.info("Finding the phrase {} with a word count each side of {}", phrase, wordCountEachSide);
        List<Transcript> transcripts = transcriptService.getAllTranscripts();

        // map(id, map(timestamp, phrase))
        HashMap<String, HashMap<String, String>> idWithTimestampAndPhrase = new HashMap<>();

        for (Transcript transcript : transcripts) {
            String timestampAndText = "";//timestampsAndTextService.getTimestampsAndText();
            String key = transcript.getId();

            StringBuilder linesStringBuilder = new StringBuilder();
            StringBuilder textStringBuilder = new StringBuilder();
            for (String line : timestampAndText.split("\n")) {
                linesStringBuilder.append(line);

                if (!line.contains(",")) {
                    break;
                }

                String lineText = line.split(",")[1];
                textStringBuilder.append(lineText).append(" ");

                if (textStringBuilder.toString().trim().contains(phrase)) {
                    // Hopefully a deep copy and not a shallow copy
                    StringBuilder tempLines = linesStringBuilder;

                    // Removes the timestamps one by one and checks if the timestamps + text contains the text now.
                    // Its purpose can be illustrated by an example: if the phrase is 40 words long, we need to find
                    // the first timestamp, and we can't do that by the current timestamp or current timestamp - 1.
                    while (!tempLines.toString().contains(phrase)) {
                        // Removing the last timestamp
                        int indexForLastTimestampComma = tempLines.length() - new StringBuilder(tempLines).reverse().indexOf(",") - 13;
                        tempLines = new StringBuilder(tempLines.substring(0, indexForLastTimestampComma) + " " + tempLines.substring(indexForLastTimestampComma + 13));
                    }

                    int indexForLastTimestampComma = tempLines.length() - new StringBuilder(tempLines).reverse().indexOf(",") - 1;
                    String timestamp = tempLines.substring(indexForLastTimestampComma - 12, indexForLastTimestampComma);

                    // Expanding the phrase to be +- the word count too
                    String phraseExpandedWithWordCount = expandPhraseWithWordsEitherSide(textStringBuilder.toString(), phrase, wordCountEachSide);
                    if (idWithTimestampAndPhrase.get(key) != null) {
                        HashMap<String, String> currentTimestampAndText = new HashMap<>(idWithTimestampAndPhrase.get(key));
                        currentTimestampAndText.put(timestamp, phraseExpandedWithWordCount);
                        idWithTimestampAndPhrase.put(key, currentTimestampAndText);
                    } else {
                        HashMap<String, String> currentTimestampAndText = new HashMap<>();
                        currentTimestampAndText.put(timestamp, phraseExpandedWithWordCount);
                        idWithTimestampAndPhrase.put(key, currentTimestampAndText);
                    }

                    // Removing phrase from textStringbuilder
                    int indexOfPhrase = textStringBuilder.indexOf(phrase);
                    // We want to remove the double space that is left when removing a phrase,
                    // by also removing one of the spaces at either ends.
                    // If statements here are just so we don't go out of bounds
                    if (indexOfPhrase > 0) {
                        textStringBuilder.replace(indexOfPhrase - 1, indexOfPhrase + phrase.length(), "");
                    } else if (indexOfPhrase + phrase.length() < textStringBuilder.length()) {
                        textStringBuilder.replace(indexOfPhrase, indexOfPhrase + phrase.length() + 1, "");
                    } else {
                        textStringBuilder.replace(indexOfPhrase, indexOfPhrase + phrase.length(), "");
                    }
                }
            }
        }

        return idWithTimestampAndPhrase;
    }

    private int validateWordCount(int wordCountEachSide) {
        int maxWordCountEachSide = 20;

        // Validation to ensure word count is between 0 and 30 (inclusive both ends)
        if (wordCountEachSide < 0) {
            log.debug("The word count each side was too small: {}, we have now made it 0.", wordCountEachSide);
            wordCountEachSide = 0;
        } else if (wordCountEachSide > maxWordCountEachSide) {
            log.debug("The word count each side was too big: {}, we have now made it {}.", wordCountEachSide, maxWordCountEachSide);
            wordCountEachSide = maxWordCountEachSide;
        }

        return wordCountEachSide;
    }

    private String validatePhrase(String phrase) {
        if (phrase.length() <= 5) {
            log.debug("Throwing an exception as the phrase {} is too short.", phrase);
            throw new InvalidPhraseException("This phrase is too short with a length of " + phrase.length());
        }

        if (phrase.length() > 100) {
            log.debug("Throwing an exception as the phrase {} is too long.", phrase);
            throw new InvalidPhraseException("This phrase is too long, being " + phrase.length() + " characters long");
        }

        return phrase;
    }

    private String expandPhraseWithWordsEitherSide(String text, String phrase, int wordCountEachSide) {
        log.debug("Expanding the phrase {} with a word count either side of {}", phrase, wordCountEachSide);
        // To account for the 1 space either side of the phrase
        wordCountEachSide++;

        int textLength = text.length();
        int indexOfPhrase = text.indexOf(phrase);
        int indexOfBeginningOfPhraseReversed = textLength - indexOfPhrase - 1;


        // This section finds the lower index
        String tempReversedText = new StringBuilder(text).reverse().toString();
        int lowerIndex = -1;
        for (int i = 0; i < wordCountEachSide; i++) {
            int indexOfNextUpperSpace = tempReversedText.indexOf(" ", indexOfBeginningOfPhraseReversed);

            // If the next upper space (this is reversed text) doesn't exist, it returns -1
            // So wet set the lower index to the max length (reversed text)
            if (indexOfNextUpperSpace < 0) {
                lowerIndex = textLength;
                break;
            }

            tempReversedText = tempReversedText.substring(0, indexOfNextUpperSpace) + "X" + tempReversedText.substring(indexOfNextUpperSpace + 1);
            lowerIndex = indexOfNextUpperSpace;
        }

        lowerIndex = textLength - lowerIndex;


        // This section finds the upper index
        int indexOfEndOfPhrase = indexOfPhrase + phrase.length() - 1;
        String tempText = text;
        int upperIndex = -1;
        for (int i = 0; i < wordCountEachSide; i++) {
            int indexOfNextUpperSpace = tempText.indexOf(" ", indexOfEndOfPhrase);

            // If the next upper space doesn't exist, it returns -1
            // So wet set the upper index to the max length
            if (indexOfNextUpperSpace < 0) {
                upperIndex = textLength;
                break;
            }

            tempText = tempText.substring(0, indexOfNextUpperSpace) + "X" + tempText.substring(indexOfNextUpperSpace + 1);
            upperIndex = indexOfNextUpperSpace;
        }

        return text.substring(lowerIndex, upperIndex).trim();

    }
}

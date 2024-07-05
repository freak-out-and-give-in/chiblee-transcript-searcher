package com.scrape.service;

import com.scrape.model.InvertedIndex;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InvertedIndexWritingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexService invertedIndexService;

    private TranscriptTxtParsingService transcriptTxtParsingService;

    @Autowired
    public InvertedIndexWritingService(InvertedIndexService invertedIndexService, TranscriptTxtParsingService transcriptTxtParsingService) {
        this.invertedIndexService = invertedIndexService;
        this.transcriptTxtParsingService = transcriptTxtParsingService;
    }

    public void writeInvertedIndexesToDatabase() {
        log.info("Writing inverted indexes to the database...");
        HashMap<String, HashMap<String, List<String>>> invertedIndex = buildInvertedIndex();

        int invertedIndexCount = 0;
        for (String word : invertedIndex.keySet()) {
            InvertedIndex tempInvertedIndex = new InvertedIndex(word, invertedIndex.get(word).toString());
            invertedIndexService.save(tempInvertedIndex);

            invertedIndexCount++;
        }

        log.info("Successfully wrote {} terms to the database", invertedIndexCount);
    }

    public HashMap<String, HashMap<String, List<String>>> buildInvertedIndex() {
        invertedIndexService.deleteAll();

        log.info("Building the inverted index");
        HashMap<String, LinkedHashMap<String, String>> transcripts = transcriptTxtParsingService.getTranscriptFromEachFile();

        return tokenizeRemoveStopWordsAndLemmatize(transcripts);
    }

    private HashMap<String, HashMap<String, List<String>>> tokenizeRemoveStopWordsAndLemmatize(HashMap<String, LinkedHashMap<String, String>> transcript) {
        log.info("Tokenizing, removing stop words and lemmatizing the transcripts");
        List<String> stopWords = transcriptTxtParsingService.getStopWords();

        // Setup for Lemmatization
        // set up pipeline properties
        Properties props = new Properties();
        // set the list of annotators to run
        props.setProperty("annotators", "tokenize,pos,lemma");
        // build pipeline
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // word, (id, (timestamps))
        HashMap<String, HashMap<String, List<String>>> customInvertedIndex = new HashMap<>();

        for (String titleAndId : transcript.keySet()) {
            int titleAndIdLength = titleAndId.length();
            String id = titleAndId.substring(titleAndIdLength - 12, titleAndIdLength - 1);

            for (Map.Entry<String, String> timestampsAndText : transcript.get(titleAndId).entrySet()) {
                String timestamp = timestampsAndText.getKey();
                String text = timestampsAndText.getValue();

                // Lemmatizing, Tokenizing and removing StopWords
                List<String> listOfWords = lemmatizeTokenizeAndRemoveStopWords(pipeline, stopWords, text);

                // Removing words with too few characters (e.g. 1 character long)
                listOfWords = removeWordsWithTooFewCharacters(listOfWords);

                // Somewhere in here is the error
                for (String word : listOfWords) {
                    // If the word is already stored
                    if (customInvertedIndex.containsKey(word)) {
                        HashMap<String, List<String>> mapOfIdWithTimestamps = customInvertedIndex.get(word);

                        // Initialises 'timestamps' based on if the id is already stored or not
                        List<String> timestamps = new ArrayList<>();
                        if (mapOfIdWithTimestamps.get(id) != null) {
                            timestamps = new ArrayList<>(mapOfIdWithTimestamps.get(id));
                        }

                        timestamps.add(timestamp);
                        mapOfIdWithTimestamps.put(id, timestamps);
                    } else {
                        HashMap<String, List<String>> mapOfIdWithTimestamp = new HashMap<>();
                        mapOfIdWithTimestamp.put(id, List.of(timestamp));

                        customInvertedIndex.put(word, mapOfIdWithTimestamp);
                    }
                }
            }
        }

        return customInvertedIndex;
    }

    // Lemmatization basically standardises all words by removing context
    // e.g. all of: sing, sang, sings, sung, sang become -> sing
    // Tokenization here is the process of taking a set of text and separating it into a set of words
    // Removing StopWords means removing commonly used words,
    // e.g. the, they, to, and, how, etc.
    // Also, we are turning words lowercase
    private List<String> lemmatizeTokenizeAndRemoveStopWords(StanfordCoreNLP pipeline, List<String> stopWords, String text) {
        text = replaceMusicAndExplicitFromText(text);

        List<String> listOfTokens = new ArrayList<>();
        // create a document object
        CoreDocument document = pipeline.processToCoreDocument(text);
        // display tokens
        for (CoreLabel tok : document.tokens()) {
            String word = tok.lemma().toLowerCase();

            // Removing StopWords
            if (!stopWords.contains(word)) {
                listOfTokens.add(word);
            }
        }

        return listOfTokens;
    }

    // Same method used when writing the transcripts for the database
    private String replaceMusicAndExplicitFromText(String text) {
        text = text.replace("[Music]", "MusicAnnotation");
        text = text.replace("[&nbsp;__&nbsp;]", "ExplicitAnnotation");

        return text;
    }

    private List<String> removeWordsWithTooFewCharacters(List<String> listOfWords) {
        int wordCharacterLengthMinimum = 1;

        Iterator<String> iterator = listOfWords.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().length() <= wordCharacterLengthMinimum) {
                iterator.remove();
            }
        }

        return listOfWords;
    }
}

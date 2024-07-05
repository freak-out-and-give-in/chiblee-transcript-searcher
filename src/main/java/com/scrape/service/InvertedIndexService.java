package com.scrape.service;

import com.scrape.dto.InvertedIndexDto;
import com.scrape.exception.PrivateException;
import com.scrape.model.InvertedIndex;
import com.scrape.repository.InvertedIndexRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InvertedIndexService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexRepository invertedIndexRepository;

    @Autowired
    public InvertedIndexService(InvertedIndexRepository invertedIndexRepository) {
        this.invertedIndexRepository = invertedIndexRepository;
    }

    public List<List<String>> getInvertedIndexDtosTimestamps(List<InvertedIndexDto> listOfInvertedIndexDtos, String id) {
        List<List<String>> allTimestamps = new ArrayList<>();

        for (InvertedIndexDto invertedIndexDto : listOfInvertedIndexDtos) {
            List<String> timestamps = new ArrayList<>(invertedIndexDto.getMapOfIdWithTimestamps().get(id));
            allTimestamps.add(timestamps);
        }

        return allTimestamps;
    }

    // No error here as sometimes it's ok to not find a word, with no need to log an error message
    public InvertedIndex getInvertedIndex(String term) {
        log.debug("Getting the inverted index with the word {}", term);

        return invertedIndexRepository.findByTerm(term);
    }

    public void save(InvertedIndex invertedIndex) {
        log.debug("Saving an inverted index to the database with word: {} and id with timestamps: {}", invertedIndex.getTerm(), invertedIndex.getVideoIdWithTimestamps());

        if (invertedIndex.getTerm().isEmpty()) {
            throw new PrivateException("The inverted index has no term");
        }

        if (invertedIndex.getVideoIdWithTimestamps().isEmpty()) {
            throw new PrivateException("The inverted index has no video id-with-timestamps");
        }

        invertedIndexRepository.save(invertedIndex);
    }

    public void deleteAll() {
        log.info("Deleting the inverted index from the database and flushing");

        invertedIndexRepository.deleteAll();
        invertedIndexRepository.flush();
    }
}

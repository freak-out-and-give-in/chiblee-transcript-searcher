package com.scrape.service;

import com.scrape.model.Transcript;
import com.scrape.repository.TranscriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TranscriptService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptRepository transcriptRepository;

    @Autowired
    public TranscriptService(TranscriptRepository transcriptRepository) {
        this.transcriptRepository = transcriptRepository;
    }

    public Transcript getTranscript(String id) {
        log.debug("Getting the transcript with the id {}", id);
        return transcriptRepository.findById(id).get();
    }

    public List<Transcript> getAllTranscripts() {
        log.debug("Getting all transcripts");
        return transcriptRepository.findAll();
    }

    public void saveTranscript(Transcript transcript) {
        log.debug("Saving transcript with the id {} and title {}", transcript.getId(), transcript.getTitle());
        transcriptRepository.save(transcript);
    }

    public void deleteAllTranscripts() {
        log.info("Deleting all transcripts.");

        transcriptRepository.deleteAll();
        transcriptRepository.flush();
    }
}

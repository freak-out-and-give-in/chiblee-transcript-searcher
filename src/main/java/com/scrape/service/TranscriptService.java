package com.scrape.service;

import com.scrape.exception.InvalidIdException;
import com.scrape.exception.PrivateException;
import com.scrape.model.Transcript;
import com.scrape.repository.TranscriptRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TranscriptService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private TranscriptRepository transcriptRepository;

    @Autowired
    public TranscriptService(TranscriptRepository transcriptRepository) {
        this.transcriptRepository = transcriptRepository;
    }

    public void save(Transcript transcript) {
        if (transcript.getVideoId().isEmpty()) {
            throw new PrivateException("The transcript's video id should not be empty");
        }

        if (transcript.getTitle().isEmpty()) {
            throw new PrivateException("The transcript's title should not be empty");
        }

        if (transcript.getTimestampsAndText().isEmpty()) {
            throw new PrivateException("The transcript's timestamps-and-text should not be empty");
        }

        transcriptRepository.save(transcript);
    }

    public Transcript getByVideoId(String videoId) {
        Transcript transcript = transcriptRepository.getTranscriptByVideoId(videoId);
        if (transcript == null) {
            throw new InvalidIdException("No transcript was found with the video id " + videoId);
        }

        return transcript;
    }

    public List<Transcript> getByTitle(String title) {
        List<Transcript> transcript = transcriptRepository.getTranscriptsByTitle(title);
        if (transcript == null) {
            throw new NoSuchElementException("No video was found with the title " + title);
        }

        return transcript;
    }

    public String getVideoIdByTitle(String title) {
        String videoId = transcriptRepository.getTranscriptsByTitle(title).getFirst().getVideoId();
        if (videoId == null) {
            throw new NoSuchElementException("No video was found with the title " + title);
        }

        return videoId;
    }

    // Efficient
    public LinkedHashMap<Integer, String> makeMapOfTimestampsAndText(String videoId) {
        // This stream can get the text/timestamps per video between 15-100ms, but most are ~20ms.
        // This is SIGNIFICANTLY faster compared to a conventional, non-streaming method.

        return Arrays.stream(getByVideoId(videoId).getTimestampsAndText()
                        .split("#hg"))
                .collect(Collectors.toMap(text -> convertTimestampToSeconds(text.substring(0, 8)),
                        text -> text.substring(12),
                        // If there is a duplicate entry for a key, add the values together
                        (t1, t2) -> t1 + " " + t2, LinkedHashMap::new));
    }

    // Same method as InvertedIndexParsingService.convertTimestampToSeconds()
    // e.g. 03:52:31 -> 13951
    private int convertTimestampToSeconds(String timestamp) {
        return LocalTime.parse(timestamp, DateTimeFormatter.ofPattern("HH:mm:ss")).toSecondOfDay();
    }

    public void deleteAll() {
        log.info("Deleting all transcripts");

        transcriptRepository.deleteAll();
        transcriptRepository.flush();
    }
}

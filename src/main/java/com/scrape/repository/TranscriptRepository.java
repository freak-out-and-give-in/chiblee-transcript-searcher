package com.scrape.repository;

import com.scrape.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranscriptRepository extends JpaRepository<Transcript, String> {

    Transcript getTranscriptByVideoId(String videoId);

    List<Transcript> getTranscriptsByTitle(String title);
}

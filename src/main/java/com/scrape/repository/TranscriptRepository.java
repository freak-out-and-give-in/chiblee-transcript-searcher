package com.scrape.repository;

import com.scrape.model.Transcript;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptRepository extends JpaRepository<Transcript, String> {
}

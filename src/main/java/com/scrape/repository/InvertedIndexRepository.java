package com.scrape.repository;

import com.scrape.model.InvertedIndex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvertedIndexRepository extends JpaRepository<InvertedIndex, String> {

    InvertedIndex findByTerm(String term);
}

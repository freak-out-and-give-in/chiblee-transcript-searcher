package com.scrape.repository;

import com.scrape.model.TimestampsAndText;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimestampsAndTextRepository extends JpaRepository<TimestampsAndText, String> {
}

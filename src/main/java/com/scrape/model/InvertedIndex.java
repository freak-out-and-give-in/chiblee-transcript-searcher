package com.scrape.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class InvertedIndex {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String term;

    // HashMap<String, List<String>>
    @Column(name = "id_with_timestamps", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String videoIdWithTimestamps;

    public InvertedIndex() {
    }

    public InvertedIndex(String term, String videoIdWithTimestamps) {
        this.term = term;
        this.videoIdWithTimestamps = videoIdWithTimestamps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getVideoIdWithTimestamps() {
        return videoIdWithTimestamps;
    }

    public void setVideoIdWithTimestamps(String videoIdWithTimestamps) {
        this.videoIdWithTimestamps = videoIdWithTimestamps;
    }
}

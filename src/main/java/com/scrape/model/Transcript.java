package com.scrape.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Transcript {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private String videoId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String timestampsAndText;

    public Transcript() {
    }

    public Transcript(String videoId, String title, String timestampsAndText) {
        this.videoId = videoId;
        this.title = title;
        this.timestampsAndText = timestampsAndText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestampsAndText() {
        return timestampsAndText;
    }

    public void setTimestampsAndText(String timestampsAndText) {
        this.timestampsAndText = timestampsAndText;
    }
}

package com.scrape.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "transcripts")
public class Transcript {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "transcript", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TimestampsAndText> timestampsAndText;

    public Transcript() {
    }

    public Transcript(String id, String title, List<TimestampsAndText> timestampsAndText) {
        this.id = id;
        this.title = title;
        this.timestampsAndText = timestampsAndText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<TimestampsAndText> getTimestampsAndText() {
        return timestampsAndText;
    }

    public void setTimestampsAndText(List<TimestampsAndText> timestampsAndText) {
        this.timestampsAndText = timestampsAndText;
    }
}

package com.scrape.model;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "timestamps-and-text")
public class TimestampsAndText {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "timestamp", columnDefinition = "TIME")
    private LocalTime timestamp;

    @Column(name = "text")
    private String text;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "transcript-id", nullable = false)
    private Transcript transcript;

    public TimestampsAndText() {
    }

    public TimestampsAndText(int id, LocalTime timestamp, String text, Transcript transcript) {
        this.id = id;
        this.timestamp = timestamp;
        this.text = text;
        this.transcript = transcript;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
    }
}

package com.scrape.dto;

import java.util.LinkedHashMap;

public class TranscriptDto {

    private String id;

    private LinkedHashMap<Integer, String> timestampsAndText;

    public TranscriptDto(String id, LinkedHashMap<Integer, String> timestampsAndText) {
        this.id = id;
        this.timestampsAndText = timestampsAndText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LinkedHashMap<Integer, String> getTimestampsAndText() {
        return timestampsAndText;
    }

    public void setTimestampsAndText(LinkedHashMap<Integer, String> timestampsAndText) {
        this.timestampsAndText = timestampsAndText;
    }
}

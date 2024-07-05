package com.scrape.dto;

import java.util.LinkedHashMap;
import java.util.List;

public class PhraseDto {

    private LinkedHashMap<String, List<Integer>> idAndTimestamps;

    private List<String> context;

    public PhraseDto(LinkedHashMap<String, List<Integer>> idAndTimestamps, List<String> context) {
        this.idAndTimestamps = idAndTimestamps;
        this.context = context;
    }

    public LinkedHashMap<String, List<Integer>> getIdAndTimestamps() {
        return idAndTimestamps;
    }

    public void setIdAndTimestamps(LinkedHashMap<String, List<Integer>> idAndTimestamps) {
        this.idAndTimestamps = idAndTimestamps;
    }

    public List<String> getContext() {
        return context;
    }

    public void setContext(List<String> context) {
        this.context = context;
    }
}

package com.scrape.dto;

import java.util.HashMap;
import java.util.List;

public class InvertedIndexDto {

    private String term;

    private HashMap<String, List<String>> mapOfIdWithTimestamps;

    public InvertedIndexDto() {
    }

    public InvertedIndexDto(String term, HashMap<String, List<String>> mapOfIdWithTimestamps) {
        this.term = term;
        this.mapOfIdWithTimestamps = mapOfIdWithTimestamps;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public HashMap<String, List<String>> getMapOfIdWithTimestamps() {
        return mapOfIdWithTimestamps;
    }

    public void setMapOfIdWithTimestamps(HashMap<String, List<String>> mapOfIdWithTimestamps) {
        this.mapOfIdWithTimestamps = mapOfIdWithTimestamps;
    }
}

package com.scrape.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class Base {

    @Autowired
    public Base() {
    }

    private final String configAndTranscriptsFolder = "C:\\Users\\James\\OneDrive\\Documents\\folder\\chiblee\\transcript-project";

    private final String transcriptsPath = configAndTranscriptsFolder + "\\transcripts";

    private final String configPath = configAndTranscriptsFolder + "\\transcripts-config";

    private final String archiveFileName = "archive.txt";

    // e.g. 03:52:31.130 -> 13951
    public int convertTimestampToSeconds(String timestamp) {
        timestamp = timestamp.substring(0, 8);
        return LocalTime.parse(timestamp, DateTimeFormatter.ofPattern("HH:mm:ss")).toSecondOfDay();
    }

    public String getTranscriptPathWithFileName(String fileName) {
        return transcriptsPath + "\\" + fileName;
    }

    public String getTranscriptsPath() {
        return transcriptsPath;
    }

    public String getArchiveFileName() {
        return archiveFileName;
    }

    public String getArchiveFile() {
        return configPath + "\\" + archiveFileName;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getStopWordsPath() {
        return "demo/src/main/resources/static/text/stopwords.txt";
    }

}

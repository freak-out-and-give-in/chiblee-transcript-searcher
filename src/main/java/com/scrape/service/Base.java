package com.scrape.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BaseMethods {

    // e.g. 03:52:31.130 -> 13951
    public int convertTimestampToSeconds(String timestamp) {
        timestamp = timestamp.substring(0, 8);
        return LocalTime.parse(timestamp, DateTimeFormatter.ofPattern("HH:mm:ss")).toSecondOfDay();
    }

}

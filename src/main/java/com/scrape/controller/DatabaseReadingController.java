package com.scrape.controller;

import com.scrape.exception.UnauthorizedAccessException;
import com.scrape.service.DatabaseParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@RestController
public class DatabaseReadingController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private DatabaseParsingService databaseParsingService;

    // This class manages all the readings from the database that a user can do

    @Autowired
    public DatabaseReadingController(DatabaseParsingService databaseParsingService) {
        this.databaseParsingService = databaseParsingService;
    }

    @GetMapping("/findThisPhrase")
    public HashMap<String, HashMap<String, String>> findThisPhrase(@RequestParam String phrase, @RequestParam int wordCount) {
        // map(id, map(timestamp, phrase))
        return databaseParsingService.findThisPhrase(phrase, wordCount);
    }

    @GetMapping("/findThisTranscript")
    public HashMap<String, String[]> findThisTranscript(@RequestParam String title, @RequestParam(required = false) String id) {
        log.info("Finding a transcript whose title is {} and id is {}.", title, id);

        // Look for transcript
        // If title is unique and has been found:
        // Else, we need to ask for the id:

        HashMap<String, String[]> mapOfUrlsTimestampsAndText = new HashMap<>();
        mapOfUrlsTimestampsAndText.put("PvPdxqYtJjQ", new String[] {"00:00:12", "text here"});

        return mapOfUrlsTimestampsAndText;
    }

}

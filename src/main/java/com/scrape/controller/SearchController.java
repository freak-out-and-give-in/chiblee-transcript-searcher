package com.scrape.controller;

import com.scrape.config.RateLimited;
import com.scrape.dto.PhraseDto;
import com.scrape.dto.TranscriptDto;
import com.scrape.service.InvertedIndexParsingService;
import com.scrape.service.TranscriptParsingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;

@RestController
public class SearchController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexParsingService invertedIndexParsingService;

    private TranscriptParsingService transcriptParsingService;

    @Autowired
    public SearchController(InvertedIndexParsingService invertedIndexParsingService, TranscriptParsingService transcriptParsingService) {
        this.invertedIndexParsingService = invertedIndexParsingService;
        this.transcriptParsingService = transcriptParsingService;
    }

    @RateLimited
    @GetMapping("/findPhrase")
    public PhraseDto findPhrase(@RequestParam String phrase, @RequestParam int wordCount) {
        log.info("Finding transcripts containing the phrase {} and with a word count of {}", phrase, wordCount);

        LinkedHashMap<String, List<Integer>> idAndTimestamps = invertedIndexParsingService.findThisPhrase(phrase);
        List<String> context = transcriptParsingService.getPhraseContext(idAndTimestamps, wordCount);

        return new PhraseDto(idAndTimestamps, context);
    }

    @RateLimited
    @GetMapping("/findTranscriptByTitle")
    public TranscriptDto findTranscriptByTitle(@RequestParam String title) {
        log.info("Finding a transcript with a title of {}", title);

        return transcriptParsingService.getTranscriptIdTimestampsAndTextByTitle(title);
    }

    @RateLimited
    @GetMapping("/findTranscriptByVideoId")
    public TranscriptDto findTranscriptByVideoId(@RequestParam String videoId) {
        log.info("Finding a transcript with an id of {}", videoId);

        return transcriptParsingService.getTranscriptIdTimestampsAndTextByVideoId(videoId);
    }

}

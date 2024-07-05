package com.scrape.controller;

import com.scrape.exception.UnauthorizedAccessException;
import com.scrape.service.TranscriptTxtWritingService;
import com.scrape.service.InvertedIndexWritingService;
import com.scrape.service.TranscriptWritingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexWritingService invertedIndexWritingService;

    private TranscriptTxtWritingService transcriptTxtWritingService;

    private TranscriptWritingService transcriptWritingService;

    @Autowired
    public AdminController(InvertedIndexWritingService invertedIndexWritingService, TranscriptTxtWritingService transcriptTxtWritingService,
                           TranscriptWritingService transcriptWritingService) {
        this.invertedIndexWritingService = invertedIndexWritingService;
        this.transcriptTxtWritingService = transcriptTxtWritingService;
        this.transcriptWritingService = transcriptWritingService;
    }

    @PostMapping("/admin/initDB")
    public void initialiseDatabase() {
        checkForAuthorization();
        invertedIndexWritingService.writeInvertedIndexesToDatabase();
    }

    // Only should be done after downloading transcripts with yt-dlp
    @PostMapping("/admin/downloadTranscripts")
    public void downloadTranscripts() {
        checkForAuthorization();
        transcriptTxtWritingService.downloadTranscripts();
    }

    @PostMapping("/admin/initTranscripts")
    public void initialiseTranscripts() {
        checkForAuthorization();
        transcriptWritingService.writeTranscriptsToDatabase();
    }

    private void checkForAuthorization() {
        if (System.getenv("ADMIN-CODE") == null) {
            log.warn("Cannot access the admin page because the system environment variable ADMIN-CODE is null");
            throw new UnauthorizedAccessException("You do not have the authorization to access this page");
        }
    }
}

package com.scrape.controller;

import com.scrape.exception.UnauthorizedAccessException;
import com.scrape.service.TranscriptTxtWritingService;
import com.scrape.service.InvertedIndexWritingService;
import com.scrape.service.TranscriptWritingService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AdminController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private InvertedIndexWritingService invertedIndexWritingService;

    private TranscriptTxtWritingService transcriptTxtWritingService;

    private TranscriptWritingService transcriptWritingService;

    @Autowired
    public AdminController(InvertedIndexWritingService invertedIndexWritingService,
                           TranscriptTxtWritingService transcriptTxtWritingService,
                           TranscriptWritingService transcriptWritingService) {
        this.invertedIndexWritingService = invertedIndexWritingService;
        this.transcriptTxtWritingService = transcriptTxtWritingService;
        this.transcriptWritingService = transcriptWritingService;
    }

    @GetMapping("/admin")
    public ModelAndView showAdminPage() {
        checkForAuthorization("loading the admin page");
        log.info("Loading the admin page");

        return new ModelAndView("/html/admin.html");
    }

    @PostMapping("/admin/initDB")
    public void initialiseDatabase() {
        checkForAuthorization("initialising the inverted index database");
        invertedIndexWritingService.writeInvertedIndexesToDatabase();
    }

    // Only should be done after downloading transcripts with yt-dlp
    @PostMapping("/admin/downloadTranscripts")
    public void downloadTranscripts() {
        checkForAuthorization("downloading transcripts");
        transcriptTxtWritingService.downloadTranscripts();
    }

    @PostMapping("/admin/initTranscripts")
    public void initialiseTranscripts() {
        checkForAuthorization("initialising transcripts");
        transcriptWritingService.writeTranscriptsToDatabase();
    }

    @Transactional
    @PostMapping("/admin/downloadAll")
    public void downloadTranscriptsAndInitialiseDatabases() {
        String attemptedActionInPresentTense = "downloading transcripts, initialising the transcripts database and " +
                "initialising the inverted index database";
        checkForAuthorization(attemptedActionInPresentTense);
        log.info("Started: {}", attemptedActionInPresentTense);

        downloadTranscripts();
        initialiseTranscripts();
        initialiseDatabase();

        log.info("Successfully: downloaded transcripts, initialised the transcripts database and initialised the " +
                "inverted index database");
    }

    private void checkForAuthorization(String attemptedAction) {
        log.debug("Checking for authorization to be able to: {}", attemptedAction);
        if (System.getenv("ADMIN-CODE") == null) {
            log.warn("The user does not have authorization for this action, " +
                    "because the system environment variable ADMIN-CODE is null");
            throw new UnauthorizedAccessException("You do not have the authorization to access this page");
        }

        log.debug("The user has been successfully authorized");
    }
}

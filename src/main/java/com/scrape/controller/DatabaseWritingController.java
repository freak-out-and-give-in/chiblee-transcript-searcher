package com.scrape.controller;

import com.scrape.exception.UnauthorizedAccessException;
import com.scrape.service.DatabaseWritingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class DatabaseWritingController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    // This class is only meant to be used by an ADMIN
    // This is because this class manages appending/deleting to the database

    private DatabaseWritingService databaseWritingService;

    @Autowired
    public DatabaseWritingController(DatabaseWritingService databaseWritingService) {
        this.databaseWritingService = databaseWritingService;
    }

    @PostMapping("/admin/initDB")
    public void initialiseDatabase() {
        log.info("About to initialise the database");

        if (System.getenv("ADMIN-CODE") == null) {
            log.warn("Cannot access the admin page because the system environment variable ADMIN-CODE is null");
            throw new UnauthorizedAccessException("You do not have the authorization to access this page");
        }

        databaseWritingService.writeTranscriptsToDatabase();
    }
}

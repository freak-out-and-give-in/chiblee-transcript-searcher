package com.scrape.controller;

import com.scrape.exception.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class PageController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PageController() {
    }

    @GetMapping("/")
    public ModelAndView showIndexPage() {
        log.info("Loading the index page");

        return new ModelAndView("/html/index.html");
    }

    @GetMapping("/admin")
    public ModelAndView showAdminPage() {
        log.info("Loading the admin page");

        if (System.getenv("ADMIN-CODE") == null) {
            log.warn("Cannot access the admin page because the system environment variable ADMIN-CODE is null");
            throw new UnauthorizedAccessException("You do not have the authorization to access this page");
        }

        return new ModelAndView("/html/admin.html");
    }

    @GetMapping("/robots.txt")
    public ModelAndView showRobotsTxtPage() {
        log.info("Showing the robots.txt page");

        return new ModelAndView("/text/robots.txt");
    }
}

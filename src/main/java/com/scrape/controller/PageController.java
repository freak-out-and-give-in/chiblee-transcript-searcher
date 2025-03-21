package com.scrape.controller;

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

    @GetMapping("/robots.txt")
    public ModelAndView showRobotsTxtPage() {
        log.info("Showing the robots.txt page");

        return new ModelAndView("/text/robots.txt");
    }
}

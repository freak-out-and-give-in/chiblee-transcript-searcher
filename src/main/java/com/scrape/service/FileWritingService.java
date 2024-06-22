package com.scrape.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileWritingService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private FileParsingService fileParsingService;

    public FileWritingService(FileParsingService fileParsingService) {
        this.fileParsingService = fileParsingService;
    }

    // This only needs to be done immediately after downloading all the txt files using yt-dlp
    // This is because there are duplicate downloads
    public void deleteAllFilesWithDotOrig() {
        log.debug("Start of deleting all files ending in .orig");
        int totalChecked = 0;
        int totalDeleted = 0;

        for (File fileEntry : fileParsingService.getIndividualTranscriptFiles()) {
            String fileName = fileEntry.getName();
            String ending = fileName.substring(fileName.length() - 7);
            if (!ending.equals(".en.vtt")) {
                //deletes all files with .orig (seemingly duplicates)
                deleteTranscriptFile(fileName);
                totalDeleted++;
            }

            totalChecked++;
        }

        log.debug("Deleted {} files out of {} checked, that end in .orig", totalDeleted, totalChecked);
    }

    private void deleteTranscriptFile(String fileName) {
        log.debug("Deleting the transcript {}", fileName);

        File file = new File(fileParsingService.getTranscriptPathWithFileName(fileName));
        file.delete();
    }
}
